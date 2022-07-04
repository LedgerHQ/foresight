import { useRef, useEffect, useState, Children } from "react";
import * as d3 from "d3";
import * as toPushData from "../../data/data";

export interface TransactionInterface {
  transactionId: string;
  value: number;
  fee: number;
  feeRate: number;
  virtualSize: number;
}
export default function MemPoolTreemap({ width, height }: any) {
  const tmpList = [];
  const [data, setData] = useState<{
    name: string;
    children: {
      name: string;
      children: any[];
    }[];
  }>();

  useEffect(() => {
    let i = 0;
    const interval = setInterval(() => {
      let tmp = toPushData.default.children[0].children.slice(0, i);
      if (tmp) {
        const chidldData = data?.children;
        console.log("chidldData", chidldData);
        console.log("tmp", tmp);

        setData({
          name: "Celtics",
          children: [
            {
              name: "Guards",
              children: [
                ...tmp,
                {
                  category: "Guards",
                  name: "",
                  value: 100 - tmp.reduce((a, b) => a + b.value, 0),
                },
              ],
            },
          ],
        });
        console.log(data);
        // renderTreemap();
        i++;
      }
      if (i > 5) i = 0;
    }, 2000);
    return () => clearInterval(interval);
  }, []);

  const svgRef = useRef(null);

  function renderTreemap() {
    const svg = d3.select(svgRef.current);
    svg.selectAll("*").remove();

    svg.attr("width", width).attr("height", height);

    const root = d3
      .hierarchy(data)
      .sum((d) => d.value)
      .sort((a: any, b: any) => b.value - a.value);

    const treemapRoot = d3.treemap().size([width, height]).padding(1)(root);

    const nodes = svg
      .selectAll("g")
      .data(treemapRoot.leaves())
      .join("g")
      .attr("transform", (d) => `translate(${d.x0},${d.y0})`);

    const fader = (color: any) => d3.interpolateRgb(color, "#fff")(0.3);
    const colorScale = d3.scaleOrdinal(d3.schemeCategory10.map(fader));

    nodes
      .append("rect")
      .attr("width", (d) => d.x1 - d.x0)
      .attr("height", (d) => d.y1 - d.y0)
      .attr("fill", (d: any) =>
        d.name === "" ? "#000" : colorScale(d.data.name)
      );

    const fontSize = 12;

    nodes
      .append("text")
      .text((d: any) => `${d.data.name} ${d.data.value}`)
      .attr("font-size", `${fontSize}px`)
      .attr("x", 3)
      .attr("y", fontSize);
  }

  useEffect(() => {
    if (data) renderTreemap();
  }, [data]);

  return (
    <div>
      <svg ref={svgRef} />
    </div>
  );
}
