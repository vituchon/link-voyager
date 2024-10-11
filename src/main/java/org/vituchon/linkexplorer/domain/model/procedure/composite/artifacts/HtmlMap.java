/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vituchon.linkexplorer.domain.model.procedure.composite.artifacts;


import java.math.BigDecimal;
import java.util.*;
import java.util.logging.Logger;
import org.giordans.graphs.BaseWeightedGraph;
import org.giordans.graphs.Edge;
import org.giordans.graphs.Path;
import org.giordans.graphs.PathFinder;
import org.giordans.graphs.WeightedGraph;

/**
 *
 * @author Administrador
 */
public class HtmlMap {

    private static final Logger LOGGER = Logger.getLogger(MultiPageLinkInspectorStatus.class.getName());

    private final String root;
    private final WeightedGraph<String> map;

    private static final Number LINK_WEIGHT = new BigDecimal(1);

    HtmlMap(String root) {
        super();
        this.root = root;
        this.map = new BaseWeightedGraph<>();
        this.map.addNode(root);
    }

    public String getRoot() {
        return root;
    }

    synchronized void addLink(String from, String to) {
        if (!this.map.getNodes().contains(to)) {
            map.addNode(to);
        }
        map.addEdge(from, to, LINK_WEIGHT);
    }

    public int distanceToRoot(String url) {
        PathFinder<String> pathFinder = map.pathFinder(root);
        return pathFinder.getDist(url).intValue();
    }

    public synchronized String toTabbedString() {
        StringBuilder sb = new StringBuilder();
        Set<String> nodes = this.map.getNodes();
        PathFinder<String> pathFinder = this.map.pathFinder(root);
        for (String node : nodes) {
            if (!node.equals(this.root)) {
                Set<Path<String>> paths = pathFinder.getPaths(map, root, node);
                sb.append("From ").append(root).append(" to : ").append(node).append("\n");
                for (Path path : paths) {
                    sb.append("\t").append(path.toString(" -> ")).append("\n");
                }
            }
        }
        return sb.toString();
    }
    
    public String toJsonString() {
    	Set<Pair> pairs = generateUniquePairs();
        return HtmlMap.toJsonArray(pairs);
    }
    
    public Set<Pair> generateUniquePairs() {
    	Set<Pair> uniquePairs = new HashSet<>();
        PathFinder<String> pathFinder = this.map.pathFinder(root);
        Set<String> nodes = this.map.getNodes();
        for (String node : nodes) {
            Set<Edge<String, Number>> edges = this.map.getOutboundEdges(node); 
            for (Edge<String, Number> edge : edges) {
                Pair pair = new Pair(edge.getSource(), edge.getDestination());
                uniquePairs.add(pair);
			}
        }
        return uniquePairs;
    }
    
    public static String toJsonArray(Set<Pair> pairs) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        if (!pairs.isEmpty()) {
            for (Pair pair : pairs) {
                sb.append("{\"source\":\"" + pair.getLeft() + "\",");
                sb.append("\"target\":\"" + pair.getRight() + "\"},");
            }
            sb.setLength(sb.length() - 1);
        }
        sb.append("]");
        return sb.toString();
    }
    // Clase auxiliar para representar un par de strings
    static class Pair {
        private final String left;
        private final String right;

        public Pair(String left, String right) {
            this.left = left;
            this.right = right;
        }

        public String getLeft() {
            return left;
        }

        public String getRight() {
            return right;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Pair pair = (Pair) o;

            if (!left.equals(pair.left)) return false;
            return right.equals(pair.right);
        }

        @Override
        public int hashCode() {
            int result = left.hashCode();
            result = 31 * result + right.hashCode();
            return result;
        }
    }

    public WeightedGraph<String> getMap() {
        return this.map;
    }
}
