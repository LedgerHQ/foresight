import type { NextPage } from "next";
import Blocks from "../components/graph/blocks";
import Mempools from "../components/graph/mempools";
import MemPoolTreemap from "../components/graph/memPoolTreeMap";
import memPoolTreemap from "../components/graph/memPoolTreeMap";
import Example from "../components/graph/Treemap-visx";
import MyTransaction from "../components/MyTransaction";
import VisualiseTransaction from "../components/visualiseTransaction";
import data from "../data/data";

const Home: NextPage = () => {
  return (
    <div className="container w-full mx-auto">
      <div className="w-full px-4 md:px-0 md:mt-8 mb-16 text-gray-800 leading-normal">
        {/* <MyTransaction /> */}
        <div className="flex">
          <Example height={900} width={1200} />
          <Mempools />
        </div>
        <hr className="border-b-2 border-gray-600 my-8 mx-4" />

        <div className="flex flex-row justify-center py-8 mx-4 bg-[#191919]">
          {/* <Treemap data={data} height="100%" width="1fr" /> */}
          {/* <MemPoolTreemap height="100%" width="1fr" /> */}
        </div>
        <VisualiseTransaction />

        <div></div>
      </div>
    </div>
  );
};

export default Home;
