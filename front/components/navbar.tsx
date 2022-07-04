const NavBar = () => {
  return (
    <nav id="header" className=" fixed w-full z-10 top-0 shadow">
      <div className="w-full container mx-auto flex flex-wrap items-center mt-0 pt-3 pb-3 md:pb-0">
        <div className="w-1/2 pl-2 md:pl-0">
          <a
            className="text-gray-100 text-base xl:text-3xl no-underline hover:no-underline font-bold"
            href="#"
          >
            <i className="fas fa-moon text-blue-400 pr-3"></i> Forseight
          </a>
        </div>
      </div>
    </nav>
  );
};

export default NavBar;
