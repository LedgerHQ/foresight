import type { NextPage } from "next";
import Head from "next/head";
import Image from "next/image";
import styles from "../styles/Home.module.css";

const Home: NextPage = () => {
  return (
    <div className="container w-full mx-auto pt-20">
      <div className="w-full px-4 md:px-0 md:mt-8 mb-16 text-gray-800 leading-normal">
        <div className="flex flex-wrap">
          <div className="w-full md:w-1/2 xl:w-1/3 p-3">
            <div className="bg-gray-900 border border-gray-800 rounded shadow p-2">
              <div className="flex flex-row items-center">
                <div className="flex-shrink pr-4">
                  <div className="rounded p-3 bg-green-600">
                    <i className="fa fa-wallet fa-2x fa-fw fa-inverse"></i>
                  </div>
                </div>
                <div className="flex-1 text-right md:text-center">
                  <h5 className="font-bold uppercase text-gray-400">
                    Total Revenue
                  </h5>
                  <h3 className="font-bold text-3xl text-gray-600">
                    $3249{" "}
                    <span className="text-green-500">
                      <i className="fas fa-caret-up"></i>
                    </span>
                  </h3>
                </div>
              </div>
            </div>
          </div>
          <div className="w-full md:w-1/2 xl:w-1/3 p-3">
            <div className="bg-gray-900 border border-gray-800 rounded shadow p-2">
              <div className="flex flex-row items-center">
                <div className="flex-shrink pr-4">
                  <div className="rounded p-3 bg-pink-600">
                    <i className="fas fa-users fa-2x fa-fw fa-inverse"></i>
                  </div>
                </div>
                <div className="flex-1 text-right md:text-center">
                  <h5 className="font-bold uppercase text-gray-400">
                    Total Users
                  </h5>
                  <h3 className="font-bold text-3xl text-gray-600">
                    249{" "}
                    <span className="text-pink-500">
                      <i className="fas fa-exchange-alt"></i>
                    </span>
                  </h3>
                </div>
              </div>
            </div>
          </div>
          <div className="w-full md:w-1/2 xl:w-1/3 p-3">
            <div className="bg-gray-900 border border-gray-800 rounded shadow p-2">
              <div className="flex flex-row items-center">
                <div className="flex-shrink pr-4">
                  <div className="rounded p-3 bg-yellow-600">
                    <i className="fas fa-user-plus fa-2x fa-fw fa-inverse"></i>
                  </div>
                </div>
                <div className="flex-1 text-right md:text-center">
                  <h5 className="font-bold uppercase text-gray-400">
                    New Users
                  </h5>
                  <h3 className="font-bold text-3xl text-gray-600">
                    2{" "}
                    <span className="text-yellow-600">
                      <i className="fas fa-caret-up"></i>
                    </span>
                  </h3>
                </div>
              </div>
            </div>
          </div>
          <div className="w-full md:w-1/2 xl:w-1/3 p-3">
            <div className="bg-gray-900 border border-gray-800 rounded shadow p-2">
              <div className="flex flex-row items-center">
                <div className="flex-shrink pr-4">
                  <div className="rounded p-3 bg-blue-600">
                    <i className="fas fa-server fa-2x fa-fw fa-inverse"></i>
                  </div>
                </div>
                <div className="flex-1 text-right md:text-center">
                  <h5 className="font-bold uppercase text-gray-400">
                    Server Uptime
                  </h5>
                  <h3 className="font-bold text-3xl text-gray-600">152 days</h3>
                </div>
              </div>
            </div>
          </div>
          <div className="w-full md:w-1/2 xl:w-1/3 p-3">
            <div className="bg-gray-900 border border-gray-800 rounded shadow p-2">
              <div className="flex flex-row items-center">
                <div className="flex-shrink pr-4">
                  <div className="rounded p-3 bg-indigo-600">
                    <i className="fas fa-tasks fa-2x fa-fw fa-inverse"></i>
                  </div>
                </div>
                <div className="flex-1 text-right md:text-center">
                  <h5 className="font-bold uppercase text-gray-400">
                    To Do List
                  </h5>
                  <h3 className="font-bold text-3xl text-gray-600">7 tasks</h3>
                </div>
              </div>
            </div>
          </div>
          <div className="w-full md:w-1/2 xl:w-1/3 p-3">
            <div className="bg-gray-900 border border-gray-800 rounded shadow p-2">
              <div className="flex flex-row items-center">
                <div className="flex-shrink pr-4">
                  <div className="rounded p-3 bg-red-600">
                    <i className="fas fa-inbox fa-2x fa-fw fa-inverse"></i>
                  </div>
                </div>
                <div className="flex-1 text-right md:text-center">
                  <h5 className="font-bold uppercase text-gray-400">Issues</h5>
                  <h3 className="font-bold text-3xl text-gray-600">
                    3{" "}
                    <span className="text-red-500">
                      <i className="fas fa-caret-up"></i>
                    </span>
                  </h3>
                </div>
              </div>
            </div>
          </div>
        </div>

        <hr className="border-b-2 border-gray-600 my-8 mx-4" />

        <div className="flex flex-row flex-wrap flex-grow mt-2">
          {/* // <div className="w-full md:w-1/2 p-3"> 
                    <!--Graph Card-->
                    <div className="bg-gray-900 border border-gray-800 rounded shadow">
                        <div className="border-b border-gray-800 p-3">
                            <h5 className="font-bold uppercase text-gray-600">Graph</h5>
                        </div>
                        <div className="p-5">
                            <canvas id="chartjs-7" className="chartjs" width="undefined" height="undefined"></canvas>
                            <script>
                                new Chart(document.getElementById("chartjs-7"), {
                                    "type": "bar",
                                    "data": {
                                        "labels": ["January", "February", "March", "April"],
                                        "datasets": [{
                                            "label": "Page Impressions",
                                            "data": [10, 20, 30, 40],
                                            "borderColor": "rgb(255, 99, 132)",
                                            "backgroundColor": "rgba(255, 99, 132, 0.2)"
                                        }, {
                                            "label": "Adsense Clicks",
                                            "data": [5, 15, 10, 30],
                                            "type": "line",
                                            "fill": false,
                                            "borderColor": "rgb(54, 162, 235)"
                                        }]
                                    },
                                    "options": {
                                        "scales": {
                                            "yAxes": [{
                                                "ticks": {
                                                    "beginAtZero": true
                                                }
                                            }]
                                        }
                                    }
                                });
                            </script>
                        </div>
                    </div>
                    <!--/Graph Card-->
                </div> */}

          {/* <div className="w-full md:w-1/2 p-3">
                    <!--Graph Card-->
                    <div className="bg-gray-900 border border-gray-800 rounded shadow">
                        <div className="border-b border-gray-800 p-3">
                            <h5 className="font-bold uppercase text-gray-600">Graph</h5>
                        </div>
                        <div className="p-5">
                            <canvas id="chartjs-0" className="chartjs" width="undefined" height="undefined"></canvas>
                            <script>
                                new Chart(document.getElementById("chartjs-0"), {
                                    "type": "line",
                                    "data": {
                                        "labels": ["January", "February", "March", "April", "May", "June", "July"],
                                        "datasets": [{
                                            "label": "Views",
                                            "data": [65, 59, 80, 81, 56, 55, 40],
                                            "fill": false,
                                            "borderColor": "rgb(75, 192, 192)",
                                            "lineTension": 0.1
                                        }]
                                    },
                                    "options": {}
                                });
                            </script>
                        </div>
                    </div>
                    <!--/Graph Card-->
                </div> */}
          {/* 
                <div className="w-full md:w-1/2 xl:w-1/3 p-3">
                    <!--Graph Card-->
                    <div className="bg-gray-900 border border-gray-800 rounded shadow">
                        <div className="border-b border-gray-800 p-3">
                            <h5 className="font-bold uppercase text-gray-600">Graph</h5>
                        </div>
                        <div className="p-5">
                            <canvas id="chartjs-1" className="chartjs" width="undefined" height="undefined"></canvas>
                            <script>
                                new Chart(document.getElementById("chartjs-1"), {
                                    "type": "bar",
                                    "data": {
                                        "labels": ["January", "February", "March", "April", "May", "June", "July"],
                                        "datasets": [{
                                            "label": "Likes",
                                            "data": [65, 59, 80, 81, 56, 55, 40],
                                            "fill": false,
                                            "backgroundColor": ["rgba(255, 99, 132, 0.2)", "rgba(255, 159, 64, 0.2)", "rgba(255, 205, 86, 0.2)", "rgba(75, 192, 192, 0.2)", "rgba(54, 162, 235, 0.2)", "rgba(153, 102, 255, 0.2)", "rgba(201, 203, 207, 0.2)"],
                                            "borderColor": ["rgb(255, 99, 132)", "rgb(255, 159, 64)", "rgb(255, 205, 86)", "rgb(75, 192, 192)", "rgb(54, 162, 235)", "rgb(153, 102, 255)", "rgb(201, 203, 207)"],
                                            "borderWidth": 1
                                        }]
                                    },
                                    "options": {
                                        "scales": {
                                            "yAxes": [{
                                                "ticks": {
                                                    "beginAtZero": true
                                                }
                                            }]
                                        }
                                    }
                                });
                            </script>
                        </div>
                    </div>
                </div> */}

          {/* <div className="w-full md:w-1/2 xl:w-1/3 p-3">
                    <div className="bg-gray-900 border border-gray-800 rounded shadow">
                        <div className="border-b border-gray-800 p-3">
                            <h5 className="font-bold uppercase text-gray-600">Graph</h5>
                        </div>
                        <div className="p-5"><canvas id="chartjs-4" className="chartjs" width="undefined" height="undefined"></canvas>
                            <script>
                                new Chart(document.getElementById("chartjs-4"), {
                                    "type": "doughnut",
                                    "data": {
                                        "labels": ["P1", "P2", "P3"],
                                        "datasets": [{
                                            "label": "Issues",
                                            "data": [300, 50, 100],
                                            "backgroundColor": ["rgb(255, 99, 132)", "rgb(54, 162, 235)", "rgb(255, 205, 86)"]
                                        }]
                                    }
                                });
                            </script>
                        </div>
                    </div>
                </div> */}

          <div className="w-full md:w-1/2 xl:w-1/3 p-3">
            <div className="bg-gray-900 border border-gray-800 rounded shadow">
              <div className="border-b border-gray-800 p-3">
                <h5 className="font-bold uppercase text-gray-600">Template</h5>
              </div>
              <div className="p-5"></div>
            </div>
          </div>

          <div className="w-full p-3">
            <div className="bg-gray-900 border border-gray-800 rounded shadow">
              <div className="border-b border-gray-800 p-3">
                <h5 className="font-bold uppercase text-gray-600">Table</h5>
              </div>
              <div className="p-5">
                <table className="w-full p-5 text-gray-700">
                  <thead>
                    <tr>
                      <th className="text-left text-gray-600">Name</th>
                      <th className="text-left text-gray-600">Side</th>
                      <th className="text-left text-gray-600">Role</th>
                    </tr>
                  </thead>

                  <tbody>
                    <tr>
                      <td>Obi Wan Kenobi</td>
                      <td>Light</td>
                      <td>Jedi</td>
                    </tr>
                    <tr>
                      <td>Greedo</td>
                      <td>South</td>
                      <td>Scumbag</td>
                    </tr>
                    <tr>
                      <td>Darth Vader</td>
                      <td>Dark</td>
                      <td>Sith</td>
                    </tr>
                  </tbody>
                </table>

                <p className="py-2">
                  <a href="#" className="text-white">
                    See More issues...
                  </a>
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Home;
