import { useMemPool } from "../context/state";

export interface MempoolData {
  id: string;
  status: "pending" | "complete";
  size: number;
  transactionNumber: number;
}
interface Props {
  mempollData: MempoolData;
}

const LoadingState = (mempollData: MempoolData) => {
  const { status, size } = mempollData;

  return (
    <div
      className={`absolute bottom-0 left-0 ${
        status === "complete"
          ? "bg-gradient-to-t from-[#8CD37E] bg-[#6DC85B]"
          : "bg-gradient-to-tr from-base via-indigo-400 to-[#6DC85B] background-animate"
      }  w-full`}
      style={{ height: `${size}%` }}
    ></div>
  );
};

const MemPool = ({ mempollData }: Props) => {
  const { status, size, transactionNumber, id } = mempollData;
  const { setMemPoolCode } = useMemPool();
  return (
    <div
      className="group flex justify-center align-middle cursor-pointer relative"
      onClick={() => setMemPoolCode(id)}
    >
      <div className="absolute -inset-2 bg-gradient-to-r from-purple-600 to-pink-600 rounded-lg blur opacity-25 group-hover:opacity-100 transition duration-1000 group-hover:duration-200 "></div>

      <div
        className={`h-48 w-48 rounded bg-black flex jutify-center items-center text-center  ${
          status === "complete" ? "border-[#6DC85B]" : "border-base"
        } border relative`}
      >
        <div className="text-xs font-medium rounded-sm text-blue-100  w-full text-center p-0.5 leading-none z-10 ">
          <div className="text-base text-blue-100">{size}%</div>
          <div className="text-xs font-light">
            {transactionNumber} transaction
          </div>
        </div>
        <LoadingState {...mempollData} />
      </div>
    </div>
  );
};

export default MemPool;
