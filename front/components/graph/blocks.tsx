import { useEffect } from "react";
import { useMemPool } from "../context/state";

const sizeBlock = (): number => {
  return Math.floor(Math.random() * 3);
};

const Blocks = () => {
  const { selectedMemPool } = useMemPool();

  useEffect(() => {}, [selectedMemPool]);

  return (
    <>
      <div>
        <div className="text-xl">mempool numero {selectedMemPool}</div>
        <div className="grid overflow-hidden auto-cols-auto auto-rows-auto gap-4 grid-flow-row p-4  rotate-180">
          {[0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 16].map(
            (b, index) => (
              <div
                key={index}
                className={`bg-gray-900 col-span-${sizeBlock()} row-span-${sizeBlock()} text-white px-4 py-6 rounded hover:bg-gray-600 hover:cursor-pointer rotate-180`}
              >
                {b}
              </div>
            )
          )}
          <div className="col-span-12 row-auto"></div>
        </div>
      </div>
    </>
  );
};

export default Blocks;
