import type { NextPage } from "next";
import Blocks from "../components/graph/blocks";
import Mempools from "../components/graph/mempools";
import MemPoolTreemap from "../components/graph/memPoolTreeMap";
import memPoolTreemap from "../components/graph/memPoolTreeMap";
import Treemap from "../components/graph/Treemap";
import MyTransaction from "../components/MyTransaction";
import data from "../data/data";

const Home: NextPage = () => {
  return (
    <div className="container w-full mx-auto pt-20">
      <div className="w-full px-4 md:px-0 md:mt-8 mb-16 text-gray-800 leading-normal">
        <MyTransaction />
        <Mempools />
        <hr className="border-b-2 border-gray-600 my-8 mx-4" />

        <div className="flex flex-row justify-center py-8">
          <Treemap data={data} height={400} width={600} />
          <MemPoolTreemap height={400} width={600} />
        </div>
      </div>
    </div>
  );
};

export default Home;
