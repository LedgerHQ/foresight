const NavBar = () => {
  return (
    <nav id="header" className="bg-gray-900 fixed w-full z-10 top-0 shadow">
      <div className="w-full container mx-auto flex flex-wrap items-center mt-0 pt-3 pb-3 md:pb-0">
        <div className="w-1/2 pl-2 md:pl-0">
          <a
            className="text-gray-100 text-base xl:text-xl no-underline hover:no-underline font-bold"
            href="#"
          >
            <i className="fas fa-moon text-blue-400 pr-3"></i> Forseight
          </a>
        </div>
        <div className="w-1/2 pr-0"></div>

        <div
          className="w-full flex-grow lg:flex lg:items-center lg:w-auto hidden lg:block mt-2 lg:mt-0 bg-gray-900 z-20"
          id="nav-content"
        >
          <ul className="list-reset lg:flex flex-1 items-center px-4 md:px-0">
            <li className="mr-6 my-2 md:my-0">
              <a
                href="#"
                className="block py-1 md:py-3 pl-1 align-middle text-blue-400 no-underline hover:text-gray-100 border-b-2 border-blue-400 hover:border-blue-400"
              >
                <i className="fas fa-home fa-fw mr-3 text-blue-400"></i>
                <span className="pb-1 md:pb-0 text-sm">Home</span>
              </a>
            </li>
            <li className="mr-6 my-2 md:my-0">
              <a
                href="#"
                className="block py-1 md:py-3 pl-1 align-middle text-gray-500 no-underline hover:text-gray-100 border-b-2 border-gray-900  hover:border-pink-400"
              >
                <i className="fas fa-tasks fa-fw mr-3"></i>
                <span className="pb-1 md:pb-0 text-sm">Tasks</span>
              </a>
            </li>
            <li className="mr-6 my-2 md:my-0">
              <a
                href="#"
                className="block py-1 md:py-3 pl-1 align-middle text-gray-500 no-underline hover:text-gray-100 border-b-2 border-gray-900  hover:border-purple-400"
              >
                <i className="fa fa-envelope fa-fw mr-3"></i>
                <span className="pb-1 md:pb-0 text-sm">Messages</span>
              </a>
            </li>
            <li className="mr-6 my-2 md:my-0">
              <a
                href="#"
                className="block py-1 md:py-3 pl-1 align-middle text-gray-500 no-underline hover:text-gray-100 border-b-2 border-gray-900  hover:border-green-400"
              >
                <i className="fas fa-chart-area fa-fw mr-3"></i>
                <span className="pb-1 md:pb-0 text-sm">Analytics</span>
              </a>
            </li>
            <li className="mr-6 my-2 md:my-0">
              <a
                href="#"
                className="block py-1 md:py-3 pl-1 align-middle text-gray-500 no-underline hover:text-gray-100 border-b-2 border-gray-900  hover:border-red-400"
              >
                <i className="fa fa-wallet fa-fw mr-3"></i>
                <span className="pb-1 md:pb-0 text-sm">Payments</span>
              </a>
            </li>
          </ul>

          <div className="relative pull-right pl-4 pr-4 md:pr-0"></div>
        </div>
      </div>
    </nav>
  );
};

export default NavBar;
