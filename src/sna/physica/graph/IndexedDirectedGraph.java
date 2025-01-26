package sna.physica.graph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;

public class IndexedDirectedGraph {
public static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	Graph<String, Integer> graph;
	
	String[] nodes;
	
	Map<String, Integer> indexMap;
	
	int[][] adjacencyList;
	
	Map<Integer, Double> edge_weights; // added for weighted
	

	public static IndexedDirectedGraph readGraph(String filename) throws Exception {
		Graph<String, Integer> graph = new DirectedSparseGraph<String, Integer>();
		
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		int count = 0;
		while (reader.ready()) {
			String line = reader.readLine();
			if (line.trim().length() == 0) continue;
			if (line.trim().startsWith("#")) continue;
			StringTokenizer st = new StringTokenizer(line.trim(), " \t");
			String vi = st.nextToken().trim();
			String vj = st.nextToken().trim();
			if (vi.equals(vj)) continue;
			graph.addEdge(graph.getEdgeCount(), vi, vj);
			
			if (++count % 100000 == 0) {
				System.err.println(count);
			}
		}
		reader.close();
		return new IndexedDirectedGraph(graph);
	}

	public static IndexedDirectedGraph readGraphWithEdgeWt(String filename) throws Exception {
		Graph<String, Integer> graph = new DirectedSparseGraph<String, Integer>();
		Map<Integer,Double> edge_weights = new HashMap<Integer,Double>(); //added for weighted
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		int count = 0;
		while (reader.ready()) {
			String line = reader.readLine();
			if (line.trim().length() == 0) continue;
			if (line.trim().startsWith("#")) continue;
			StringTokenizer st = new StringTokenizer(line.trim(), " \t");
			String vi = st.nextToken().trim();
			String vj = st.nextToken().trim();
			if (vi.equals(vj)) continue;
			int eid = graph.getEdgeCount();
			graph.addEdge(eid, vi, vj);
			String eWt = st.nextToken().trim();
			edge_weights.put(eid, Double.parseDouble(eWt));
			//Integer eid = graph.findEdge(vi, vj);
			
			if (++count % 100000 == 0) {
				System.err.println(count);
			}
		}
		reader.close();
		return new IndexedDirectedGraph(graph,edge_weights);
	}
	public IndexedDirectedGraph(Graph<String, Integer> graph) {

		this.graph = graph;
		
		this.nodes = new String[graph.getVertexCount()];
		this.indexMap = new HashMap<String, Integer>();
		
		Object[] nodeArray = graph.getVertices().toArray();
		for (int i=0; i<nodeArray.length; i++) {
			nodes[i] = nodeArray[i].toString();
			indexMap.put(nodes[i], i);
		}
		
		this.adjacencyList = new int[graph.getVertexCount()][];
		for (String v: graph.getVertices()) {
			int vi = indexMap.get(v);
			this.adjacencyList[vi] = new int[graph.getNeighborCount(v)];
			int index = 0;
			for (String w: graph.getNeighbors(v)) {
				int wi = indexMap.get(w);
				this.adjacencyList[vi][index++] = wi;
			}
		}
	}

	public IndexedDirectedGraph(Graph<String, Integer> graph, Map<Integer,Double> edge_weights) {

		this.graph = graph;
		
		this.nodes = new String[graph.getVertexCount()];
		this.indexMap = new HashMap<String, Integer>();
		  // added for weighted
		 this.edge_weights =edge_weights;
		Object[] nodeArray = graph.getVertices().toArray();
		for (int i=0; i<nodeArray.length; i++) {
			nodes[i] = nodeArray[i].toString();
			indexMap.put(nodes[i], i);
		}
		
		this.adjacencyList = new int[graph.getVertexCount()][];
		for (String v: graph.getVertices()) {
			int vi = indexMap.get(v);
			this.adjacencyList[vi] = new int[graph.getNeighborCount(v)];
			int index = 0;
			for (String w: graph.getNeighbors(v)) {
				int wi = indexMap.get(w);
				this.adjacencyList[vi][index++] = wi;
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
//		for (int i=0; i<adjacencyList.length; i++) {
//			sb.append(String.format("[%d]: ", i));
//			for (int j=0; j<adjacencyList[i].length; j++) {
//				sb.append(String.format("%d, ", adjacencyList[i][j]));
//			}
//			sb.append(LINE_SEPARATOR);
//		}
		for (int i=0; i<adjacencyList.length; i++) {
			sb.append(String.format("[%s]: ", nodes[i]));
			for (int j=0; j<adjacencyList[i].length; j++) {
				sb.append(String.format("%s, ", nodes[adjacencyList[i][j]]));
			}
			sb.append(LINE_SEPARATOR);
		}
		return sb.toString();
	}

	public Graph<String, Integer> getGraph() {
		return graph;
	}

	public int getVertexCount() {
		return graph.getVertexCount();
	}

	public int getEdgeCount() {
		return graph.getEdgeCount();
	}

	public int degree(int vi) {
		return adjacencyList[vi].length;
	}

	public Collection<String> getVertices() {
		return graph.getVertices();
	}
	
	public int[] getNeighbors(int vi) {
		return adjacencyList[vi];
	}

	public Set<Integer> getNeighborsByIndex(int vi) {
		Set<Integer> neighbor = new HashSet<Integer>();
		for (int j=0; j<adjacencyList[vi].length; j++) {
			neighbor.add(adjacencyList[vi][j]);
		}
		return neighbor;
	}

	public String getVertex(int vi) {
		return nodes[vi];
	}

	public int getIndex(String v) {
		return indexMap.get(v);
	}

	public Map<Integer, Double> getEdge_weights() {
		return edge_weights;
	}

	public void setEdge_weights(Map<Integer, Double> edge_weights) {
		this.edge_weights = edge_weights;
	}
	
	public double getEdgeWeight(int v1, int v2){
		Integer edgeId = this.getGraph().findEdge(this.getVertex(v1), this.getVertex(v2));
		return this.edge_weights.get(edgeId);
	}

}
