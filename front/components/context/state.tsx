import {
  createContext,
  Dispatch,
  ReactNode,
  SetStateAction,
  useContext,
  useEffect,
  useMemo,
  useState,
} from "react";
import { WindowMessageTransport } from "@ledgerhq/live-app-sdk";
import LedgerLiveApi from "@ledgerhq/live-app-sdk";
import { TransactionInterface } from "../graph/Treemap-visx";

type memPoolContextType = {
  llapi?: LedgerLiveApi;
  selectedMemPool: string | null;
  setMemPoolCode: Dispatch<SetStateAction<string | null>>;
  selectedTransaction: TransactionInterface | null;
  setTransaction: Dispatch<SetStateAction<TransactionInterface | null>>;
};

const MemPoolContextDefaultValues: memPoolContextType = {
  llapi: undefined,
  selectedMemPool: null,
  setMemPoolCode: () => {},
  selectedTransaction: null,
  setTransaction: () => {},
};

const MemPoolContext = createContext<memPoolContextType>(
  MemPoolContextDefaultValues
);

export function useMemPool() {
  return useContext(MemPoolContext);
}

type Props = {
  children: ReactNode;
};

export function MemPoolProvider({ children }: Props) {
  const [loading, setLoading] = useState(false);
  const [llapi, setllapi] = useState<LedgerLiveApi>();
  const [selectedMemPool, setMemPoolCode] = useState<string | null>(null);
  const [selectedTransaction, setTransaction] =
    useState<TransactionInterface | null>(null);

  // Client-side-only code
  useEffect(() => {
    const llapi = new LedgerLiveApi(new WindowMessageTransport());
    llapi.connect();
    setllapi(llapi);
    setLoading(false);
  }, []);

  const value = {
    llapi,
    MemPoolLoading: loading,
    selectedMemPool,
    setMemPoolCode,
    setTransaction,
    selectedTransaction,
  };

  return (
    <MemPoolContext.Provider value={value}>{children}</MemPoolContext.Provider>
  );
}
