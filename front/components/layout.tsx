import Navbar from "./navbar";
// import Footer from './footer'
import { NextComponentType } from "next";

const Layout = ({ children }: { children: JSX.Element }) => {
  return (
    <>
      <Navbar />
      <main>{children}</main>
      {/* <Footer /> */}
    </>
  );
};
export default Layout;
