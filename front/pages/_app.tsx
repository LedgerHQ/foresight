import "../styles/globals.css";
import type { AppProps } from "next/app";
import Layout from "../components/layout";
import { MemPoolProvider } from "../components/context/state";

function MyApp({ Component, pageProps }: AppProps) {
  return (
    <MemPoolProvider>
      <Layout>
        <Component {...pageProps} />
      </Layout>
    </MemPoolProvider>
  );
}

export default MyApp;
