import { useEffect, useState } from "react";
import Mempool, { MempoolData } from "./mempool";

const Mempools = () => {
  const [fillingPool, setFillingPool] = useState<MempoolData>({
    id: "1",
    status: "pending",
    size: 40,
    transactionNumber: 2351,
  });

  useEffect(() => {
    try {
      fetch("http://51.210.220.222/foresight/api/mempool")
        .then((data) => {
          console.log("data");
          return data.json();
        })
        .then((a) => {
          console.log(a);
          setFillingPool({ ...fillingPool, transactionNumber: a[0] });
        })
        .catch((e) => console.log(e));
    } catch (e) {
      console.log(e);
    }
  });

  const filledMempools = [
    { id: "123432453", status: "complete", size: 40, transactionNumber: 0 },
    { id: "123432453", status: "complete", size: 40, transactionNumber: 0 },
    { id: "123432453", status: "complete", size: 40, transactionNumber: 0 },
  ] as MempoolData[];

  return (
    <div className="flex flex-col justify-center divide-y-2 divide-gray-400 divide-dashed py-8">
      <div className="flex-1 flex justify-end  py-8">
        <Mempool mempollData={fillingPool} />
      </div>
      <div className="flex flex-col gap-4 flex-1 pl-4 py-8">
        {filledMempools.map((m, index) => (
          <Mempool key={index} mempollData={m} />
        ))}
      </div>
    </div>
  );
};

export default Mempools;
