import { useMemPool } from "./context/state";

const VisualiseTransaction = () => {
  const { selectedTransaction } = useMemPool();
  return (
    <div className="px-4 text-white flex-1">
      <div>
        hash : {selectedTransaction?.hash.substring(0, 20)}...
        {selectedTransaction?.hash.substring(40)}
      </div>
      <div>Gas : {selectedTransaction?.gas}</div>
      <div>sender : {selectedTransaction?.sender}</div>
      <div>receiver : {selectedTransaction?.receiver}</div>
      <div>ETH : {selectedTransaction?.value}</div>
    </div>
  );
};

export default VisualiseTransaction;
