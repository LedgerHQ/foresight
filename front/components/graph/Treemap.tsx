import { useRef, useEffect } from "react";
import * as d3 from "d3";

export default function Treemap({ data, width, height }: any) {
  const svgRef = useRef(null);

  console.log("DATA", data);
  function renderTreemap() {
    const svg = d3.select(svgRef.current);

    svg.attr("width", width).attr("height", height);

    const root = d3
      .hierarchy(data)
      .sum((d) => d.value)
      .sort((a: any, b: any) => b.value - a.value);

    const treemapRoot = d3.treemap().size([width, height]).padding(1)(root);

    const nodes = svg
      .selectAll("g")
      .data(treemapRoot.leaves())
      .join("g")
      .attr("transform", (d) => `translate(${d.x0},${d.y0})`);

    const fader = (color: any) => d3.interpolateRgb(color, "#fff")(0.3);
    const colorScale = d3.scaleOrdinal(d3.schemeCategory10.map(fader));

    nodes
      .append("rect")
      .attr("width", (d) => d.x1 - d.x0)
      .attr("height", (d) => d.y1 - d.y0)
      .attr("fill", (d: any) => colorScale(d.data.name));

    const fontSize = 12;

    nodes
      .append("text")
      .text((d: any) => `${d.data.name} ${d.data.value}`)
      .attr("font-size", `${fontSize}px`)
      .attr("x", 3)
      .attr("y", fontSize);

    // d3.selectAll("rect").on("click", function (clickedTruc) {
    //   console.log(this.parentNode.parentNode, clickedTruc);

    //   // var title =
    //   //   this.parentNode.parentNode.getElementsByTagName("title")[0]
    //   //     .childNodes[0];
    // });
  }

  useEffect(() => {
    renderTreemap();
  }, [data]);

  return (
    <div>
      <svg ref={svgRef} />
    </div>
  );
}
