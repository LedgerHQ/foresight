import type { NextPage } from "next";
import Blocks from "../components/graph/blocks";
import MemPools from "../components/graph/mempool";
import MyTransaction from "../components/MyTransaction";

const Home: NextPage = () => {
  return (
    <div className="container w-full mx-auto pt-20">
      <div className="w-full px-4 md:px-0 md:mt-8 mb-16 text-gray-800 leading-normal">
        <MyTransaction />
        <MemPools />
        <hr className="border-b-2 border-gray-600 my-8 mx-4" />

        <div className="flex flex-row">
          <Blocks />
          <div className="">
            <h3 className="h3">Salut la zone</h3>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Home;
