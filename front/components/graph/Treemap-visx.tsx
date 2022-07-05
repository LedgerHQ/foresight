import React, { useEffect, useState } from "react";
import { Group } from "@visx/group";
import { Pack, hierarchy } from "@visx/hierarchy";
import { scaleQuantize } from "@visx/scale";
import rawData, {
  Exoplanets as Datum,
} from "@visx/mock-data/lib/mocks/exoplanets";
import { localPoint } from "@visx/event";
import { useTooltip, useTooltipInPortal } from "@visx/tooltip";
import { useMemPool } from "../context/state";
import useWebSocket, { ReadyState } from "react-use-websocket";
import { HierarchyNode } from "@visx/hierarchy/lib/types";

function extent<D>(allData: D[], value: (d: D) => number): [number, number] {
  return [Math.min(...allData.map(value)), Math.max(...allData.map(value))];
}

export interface TransactionInterface {
  hash: string;
  type: string;
  block_height: number | null;
  created_at: string;
  mined_at: Date | null;
  dropped_at: Date | null;
  block_hash: string | null;
  sender: string;
  gas: string;
  gas_price: number;
  max_fee_per_gas: number | null;
  max_priority_fee_per_gas: number | null;
  input: string;
  nonce: number | null;
  receiver: string;
  transaction_index: number | null;
  value: string;
  status: string | null;
}

// const testData = [
//   {
//     hash: "0x7020347837bd20ad5fe868cefec66ed11d8f9da50419518651784e0bb0d780a7",
//     type: "Legacy",
//     block_height: null,
//     created_at: "2022-07-05T00:13:27.825077+00:00",
//     mined_at: null,
//     dropped_at: null,
//     block_hash: null,
//     sender: "0xc8e2f3a45d0be5109418b876c02c299c6004bdff",
//     gas: 21000,
//     gas_price: 5000000000,
//     max_fee_per_gas: null,
//     max_priority_fee_per_gas: null,
//     input: "0x",
//     nonce: 1,
//     receiver: "0xbd4282cc1ed4898576609939c499b6a8f33fee4e",
//     transaction_index: null,
//     value: 72945000000000,
//     status: null,
//   },
//   {
//     hash: "0x7020347837bd20ad5fe868cefec66ed11d8f9da50419518651784e0bb0d780a7",
//     type: "Legacy",
//     block_height: null,
//     created_at: "2022-07-05T00:13:27.825077+00:00",
//     mined_at: null,
//     dropped_at: null,
//     block_hash: null,
//     sender: "0xc8e2f3a45d0be5109418b876c02c299c6004bdff",
//     gas: 21000,
//     gas_price: 5000000000,
//     max_fee_per_gas: null,
//     max_priority_fee_per_gas: null,
//     input: "0x",
//     nonce: 1,
//     receiver: "0xbd4282cc1ed4898576609939c499b6a8f33fee4e",
//     transaction_index: null,
//     value: 72945000000000,
//     status: null,
//   },
// ] as TransactionInterface[];

// const colorScale = scaleQuantize({
//   domain: extent(rawData, (d) => d.radius),
//   range: ["#ffe108", "#ffc10e", "#fd6d6f", "#855af2", "#11d2f9", "#49f4e7"],
// });

const defaultMargin = { top: 10, left: 30, right: 40, bottom: 80 };

export type PackProps = {
  width: number;
  height: number;
  margin?: { top: number; right: number; bottom: number; left: number };
};

export default function Example({
  width,
  height,
  margin = defaultMargin,
}: PackProps) {
  const {
    tooltipData,
    tooltipLeft,
    tooltipTop,
    tooltipOpen,
    showTooltip,
    hideTooltip,
  } = useTooltip();

  const [root, setRoot] = useState<HierarchyNode<Datum>>();

  const powTen = (x: string) => {
    const parse = parseInt(x);
    if (isNaN(parse)) {
      return 0;
    }
    return Math.sqrt(parse / Math.pow(10, 8));
  };

  const setUpStuff = () => {
    const filteredPlanets = transactionData
      .filter((d) => d.value !== "0" && d.value != null)
      .map((t) => ({
        ...t,
        radius: powTen(t.value),
        distance: powTen(t.gas),
        id: t.hash,
      }));

    // console.log("planets", filteredPlanets);

    const pack = {
      children: filteredPlanets,
      name: "root",
      radius: 0,
      distance: 0,
    };

    setRoot(
      hierarchy<Datum>(pack)
        .sum((d) => d.radius * d.radius)
        .sort(
          (a, b) =>
            // sort by hierarchy, then distance
            (a?.data ? 1 : -1) - (b?.data ? 1 : -1) ||
            (a.children ? 1 : -1) - (b.children ? 1 : -1) ||
            (a.data.distance == null ? -1 : 1) -
              (b.data.distance == null ? -1 : 1) ||
            a.data.distance! - b.data.distance!
        )
    );
  };

  const { setTransaction } = useMemPool();
  // If you don't want to use a Portal, simply replace `TooltipInPortal` below with
  // `Tooltip` or `TooltipWithBounds` and remove `containerRef`
  const { containerRef, TooltipInPortal } = useTooltipInPortal({
    // use TooltipWithBounds
    detectBounds: true,
    // when tooltip containers are scrolled, this will correctly update the Tooltip position
    scroll: true,
  });

  const handleMouseOver = (event: any, datum: any) => {
    const coords = localPoint(event.target.ownerSVGElement, event);
    showTooltip({
      tooltipLeft: coords?.x,
      tooltipTop: coords?.y,
      tooltipData: datum,
    });
  };

  const [socketUrl, setSocketUrl] = useState(
    "ws://51.210.220.222/foresight/ws/processed-transactions"
  );
  const [transactionData, setTransactionData] = useState<
    TransactionInterface[]
  >([]);

  const { sendMessage, lastMessage, readyState } = useWebSocket(socketUrl);

  useEffect(() => {
    if (lastMessage !== null) {
      const receivedTransaction = JSON.parse(lastMessage.data).map(
        // (t: any) => ({ ...t, value: parseInt(t.value), gas: parseInt(t.gas) })
        (t: any) => ({ ...t })
      );
      // console.log(receivedTransaction);
      setTransactionData([...receivedTransaction]);
      setUpStuff();
    }
  }, [lastMessage]);

  const handleclick = (data: TransactionInterface) => {
    setTransaction(data);
  };

  const colorScale = (transactionType: string) => {
    if (transactionType === "EIP1559") return "#FF5300";
    else return "#B1A6F1";
  };

  return width < 10 ? null : (
    <div className=" flex-1 flex justify-center">
      <svg width={width} height={height}>
        <rect
          width={width}
          height={height}
          rx={14}
          fill="#black"
          onMouseOver={() => handleMouseOver}
          onMouseOut={hideTooltip}
        />
        {root ? (
          <Pack<Datum> root={root} size={[width, height]}>
            {(packData: any) => {
              const circles = packData.descendants().slice(2); // skip outer hierarchies
              return (
                <Group>
                  {circles.map((circle: any, i: number) => (
                    <circle
                      ref={containerRef}
                      key={`circle-${i}`}
                      r={circle.r}
                      cx={circle.x}
                      cy={circle.y}
                      id={circle.data.hash}
                      fill={colorScale(circle.data.transactionType)}
                      onClick={(data) => handleclick(circle.data)}
                      className="mempool-transaction"
                    />
                  ))}
                </Group>
              );
            }}
          </Pack>
        ) : null}
      </svg>
      {tooltipOpen && (
        <TooltipInPortal
          // set this to random so it correctly updates with parent bounds
          key={Math.random()}
          top={tooltipTop}
          left={tooltipLeft}
        >
          Data value <strong>toto</strong>
        </TooltipInPortal>
      )}
    </div>
  );
}
