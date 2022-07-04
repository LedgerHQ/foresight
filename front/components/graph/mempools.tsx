import Mempool, { MempoolData } from "./mempool";

const Mempools = () => {
  const fillingPool = {
    id: 1,
    status: "pending",
    size: 24,
    transactionNumber: 2351,
  } as MempoolData;

  const filledMempools = [
    { id: 123432453, status: "complete", size: 87, transactionNumber: 2351 },
    { id: 123432453, status: "complete", size: 87, transactionNumber: 2351 },
    { id: 123432453, status: "complete", size: 87, transactionNumber: 2351 },
  ] as MempoolData[];

  return (
    <div className="flex flex-row justify-center divide-x-2 divide-gray-400 divide-dashed py-8">
      <div className="flex-1 flex justify-end pr-4 py-8">
        <Mempool mempollData={fillingPool} />
      </div>
      <div className="flex flex-row gap-4 flex-1 pl-4 py-8">
        {filledMempools.map((m) => (
          <Mempool mempollData={m} />
        ))}
      </div>
    </div>
  );
};

export default Mempools;
