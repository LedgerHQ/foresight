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

type memPoolContextType = {
  llapi?: LedgerLiveApi;
  selectedMemPool: string | null;
  setMemPoolCode: Dispatch<SetStateAction<string | null>>;
};

const MemPoolContextDefaultValues: memPoolContextType = {
  llapi: undefined,
  selectedMemPool: null,
  setMemPoolCode: () => {},
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

  // Client-side-only code
  useEffect(() => {
    const llapi = new LedgerLiveApi(new WindowMessageTransport());
    llapi.connect();
    console.log("contect", llapi);
    setllapi(llapi);
    setLoading(false);
  }, []);

  const setSession = (access_token: string) => {
    if (typeof window !== "undefined") {
      window.localStorage.setItem("token", access_token);
    }
  };

  const value = {
    llapi,
    MemPoolLoading: loading,
    selectedMemPool,
    setMemPoolCode,
    setSession,
  };

  return (
    <MemPoolContext.Provider value={value}>{children}</MemPoolContext.Provider>
  );
}
