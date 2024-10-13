var MapVisualizer;
(function(MapVisualizer) {
  const width = 800;
  const height = 600;

  let svg
  let simulation;

  function init(elementSelector) {
    svg = d3.select(elementSelector)
      .attr("width", width)
      .attr("height", height);
  }

  function clearGraph() {
    svg.selectAll("*").remove();
    if (simulation) {
      simulation.stop();
    }
  }

  function drawGraph(links) {
    const g = svg.append("g") // Crear un grupo para el gráfico;

    // thanks a lot https://observablehq.com/@ee2dev/d3-force-playground for allowing me to calibrate the visualization of this graph!
    simulation = d3.forceSimulation()
      .force("link", d3.forceLink().id(d => d.id).distance(50))
      .force("center", d3.forceCenter(width / 2, height / 2))
      .force("charge", d3.forceManyBody().strength(-20).distanceMin(20).distanceMax(150))
      .force("collide", d3.forceCollide().strength(2).radius(50).iterations(10))
      .alphaDecay(0.05)

    const nodes = Array.from(new Set(links.flatMap(l => [l.source, l.target]))).map(id => ({id}));

    // Definir el marcador de flecha
    svg.append("defs").append("marker")
      .attr("id", "arrow")
      .attr("viewBox", "0 -5 10 10")
      .attr("refX", 16) // Posición de la flecha respecto al enlace
      .attr("refY", 0)
      .attr("markerWidth", 6)
      .attr("markerHeight", 6)
      .attr("orient", "auto")
      .append("path")
      .attr("d", "M0,-5L10,0L0,5") // Definición de la flecha en forma de triángulo
      .attr("fill", "black"); // Color de la flecha

    // Dibuja los enlaces (links) como líneas
    const link = g.append("g")
      .attr("class", "links")
      .selectAll("line")
      .data(links)
      .enter().append("line")
      .attr("class", "link")
      .style("stroke", function(d) {
        if (d.level === 0) {
          return "lightcoral"; // Color para enlaces de nivel 1
        } else if (d.level === 1) {
          return "blue"; // Color para enlaces de nivel 2
        } else {
          return "gray"; // Color para otros enlaces
        }
      })
      .attr("stroke-width", d => Math.sqrt(d.value))
      .attr("marker-end", "url(#arrow)");

    // Dibuja los nodos como círculos
    const rootUrl = document.getElementById('rootUrl').value;
    const node = g.append("g")
      .attr("class", "nodes")
      .selectAll("circle")
      .data(nodes)
      .enter().append("circle")
      .attr("class", "node")
      .attr("r", function(d) {
        if (d.id === rootUrl) {
          return 10; // Tamaño mayor para el nodo raíz
        } else {
          return 5; // Tamaño normal para los demás nodos
        }
      })
      .style("fill", function(d) {
        if (d.id === rootUrl) {
          return "red"; // Color rojo para el nodo raíz
        } else {
          return "blue"; // Color para el resto de los nodos
        }
      })
      .call(d3.drag() // Habilitar el arrastre en los nodos
        .on("start", dragstarted) // Al comenzar el arrastre
        .on("drag", dragged) // Durante el arrastre
        .on("end", dragended)); // Al finalizar el arrastre

    // Agrega etiquetas de texto a los nodos
    const label = g.append("g")
      .attr("class", "labels")
      .selectAll("text")
      .data(nodes)
      .enter().append("text")
      .attr("class", "label")
      .attr("dy", -10) // Ajuste para mostrar el texto encima del nodo
      .attr("text-anchor", "middle")
      .style("font-size", "10px") // Ajusta el tamaño del texto si es necesario
      .style("display", "none") // Inicialmente ocultos
      .text(d => d.id); // Mostrar el ID como texto

    // Mostrar/Ocultar textos cuando el mouse está sobre los nodos
    node.on("mouseover", function(event, d) {
        label.filter(n => n.id === d.id).style("display", "block");
      })
      .on("mouseout", function(event, d) {
        label.filter(n => n.id === d.id).style("display", "none");
      });

    function getAdjacentNodes(selectedNodeId) {
      // Obtener los nodos adyacentes a través de los enlaces
      const adjacentNodes = link
        .filter(l => l.source.id === selectedNodeId || l.target.id === selectedNodeId)
        .data()
        .map(l => l.source.id === selectedNodeId ? l.target.id : l.source.id);

      adjacentNodes.push(selectedNodeId);
      return adjacentNodes;
    }

    // Función para mostrar solo nodos adyacentes
    function showAdjacentNodes(selectedNodeId) {
      const adjacentNodes = getAdjacentNodes(selectedNodeId);
      node.style("display", d => adjacentNodes.includes(d.id) ? "block" : "none");
    }

    function showAllNodes() {
      node.style("display", "block");
    }

    let showingAllNodes = true;
    node.on("click", function(event, d) {
      if (showingAllNodes) {
        showAdjacentNodes(d.id); // Mostrar solo los adyacentes si todos están visibles
      } else {
        showAllNodes(); // Mostrar todos los nodos si los adyacentes están visibles
      }
      showingAllNodes = !showingAllNodes; // Alternar estado
    });

    simulation
      .nodes(nodes)
      .on("tick", ticked);

    simulation.force("link")
      .links(links);

    const zoom = d3.zoom()
      .scaleExtent([0.1, 5])
      .on("zoom", (event) => {
        g.attr("transform", event.transform);
      });

    svg.call(zoom);

    function ticked() {
      link
        .attr("x1", d => d.source.x)
        .attr("y1", d => d.source.y)
        .attr("x2", d => d.target.x)
        .attr("y2", d => d.target.y);

      node
        .attr("cx", d => d.x)
        .attr("cy", d => d.y);

      label
        .attr("x", d => d.x)
        .attr("y", d => d.y);
    }

    // Funciones para el arrastre de nodos
    function dragstarted(event, d) {
      if (!event.active) simulation.alphaTarget(0.3).restart();
      d.fx = d.x;
      d.fy = d.y;
    }

    function dragged(event, d) {
      d.fx = event.x;
      d.fy = event.y;
    }

    function dragended(event, d) {
      if (!event.active) simulation.alphaTarget(0);
      d.fx = null;
      d.fy = null;
    }

  }

  MapVisualizer.init = init;
  MapVisualizer.clearGraph = clearGraph;
  MapVisualizer.drawGraph = drawGraph;
})(MapVisualizer || (MapVisualizer = {}));