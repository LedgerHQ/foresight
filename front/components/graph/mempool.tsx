export interface MempoolData {
  id: number;
  status: "pending" | "complete";
  size: number;
  transactionNumber: number;
}
interface Props {
  mempollData: MempoolData;
}

const MemPool = ({ mempollData }: Props) => {
  const { status } = mempollData;
  return (
    <div className="group flex justify-center align-middle cursor-pointer relative">
      <div className="absolute -inset-2 bg-gradient-to-r from-purple-600 to-pink-600 rounded-lg blur opacity-25 group-hover:opacity-100 transition duration-1000 group-hover:duration-200 "></div>

      <div
        className={`h-48 w-48 rounded bg-black flex jutify-center items-center text-center  ${
          status === "complete" ? "border-[#6DC85B]" : "border-base"
        } border relative`}
      >
        <div className="text-xs font-medium rounded-sm text-blue-100  w-full text-center p-0.5 leading-none z-10 ">
          <div className="text-base text-blue-100">{mempollData.size}%</div>
          <div className="text-xs font-light">
            {mempollData.transactionNumber} transaction
          </div>
        </div>
        <div
          className={`absolute bottom-0 left-0 bg-gradient-to-t from-indigo-500 ${
            status === "complete" ? "bg-[#6DC85B]" : "bg-base"
          } rounded w-full`}
          style={{ height: `${mempollData.size}%` }}
        ></div>
      </div>
    </div>
  );
};

// const MemPool = ({ mempollData }: Props) => {
//   return (
//     <div className="group relative max-w-7xl mx-auto">
//       <div className="absolute -inset-1 bg-gradient-to-r from-purple-600 to-pink-600 rounded-lg blur opacity-25 group-hover:opacity-100 transition duration-1000 group-hover:duration-200"></div>
//       <div className="px-7 py-6 bg-white ring-1 ring-gray-900/5 rounded-lg leading-none flex items-top justify-start space-x-6">
//         <svg
//           className="w-8 h-8 text-purple-600"
//           fill="none"
//           viewBox="0 0 24 24"
//         >
//           <path
//             stroke="currentColor"
//             stroke-linecap="round"
//             stroke-linejoin="round"
//             stroke-width="1.5"
//             d="M6.75 6.75C6.75 5.64543 7.64543 4.75 8.75 4.75H15.25C16.3546 4.75 17.25 5.64543 17.25 6.75V19.25L12 14.75L6.75 19.25V6.75Z"
//           ></path>
//         </svg>
//         <div className="space-y-2">
//           <p className="text-slate-800">
//             Learn how to make a glowing gradient background!
//           </p>
//           <a
//             href="#"
//             className="block text-indigo-400 group-hover:text-slate-800 transition duration-200"
//           >
//             Read Article →
//           </a>
//         </div>
//       </div>
//     </div>
//   );
// };

export default MemPool;
