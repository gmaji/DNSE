package sna.physica.ranking;

import java.util.HashSet;
import java.util.Set;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import sna.physica.graph.IndexedGraph;

public class MDDRanker extends Ranker {

	public int precision;
	
	double lambda;
	
	public MDDRanker(IndexedGraph graph, double lambda) {
		this(graph, lambda, 100);
	}

	public MDDRanker(IndexedGraph graph, double lambda, int precision) {
		super(graph);
		this.lambda = lambda;
		this.precision = precision;
	}

	@Override
	public void evaluate() {
		
		preProcessing();

		int[] kmixedMap = decomposeMixed();
		
		for (int v=0; v<graph.getVertexCount(); v++) {
			int value = kmixedMap[v];
			nodeValueMap[v] = value;
		}
		
		postProcessing();
	}

	private int[] decomposeMixed() {

		Graph<Integer, Integer> g = new UndirectedSparseGraph<Integer, Integer>();
		for (int vi=0; vi<graph.getVertexCount(); vi++) {
			for (int vj: graph.getNeighbors(vi)) {
				g.addEdge(g.getEdgeCount(), vi, vj);
			}
		}
		
		int[] kmixedMap = new int[graph.getVertexCount()]; 
		for (int vi=0; vi<kmixedMap.length; vi++) {
			kmixedMap[vi] = g.degree(vi)*precision;
		}
		
		int currentKM = 1*precision;
		while (g.getVertexCount() > 0) {

			Set<Integer> targets = new HashSet<Integer>(g.getVertices());
			
			boolean isRemoved = false;
			for (int vi: targets) {
				int km = kmixedMap[vi];
				if (km <= currentKM) {
					g.removeVertex(vi);
					isRemoved = true;
					kmixedMap[vi] = currentKM;
				}
			}
			
			for (int vi: g.getVertices()) {
				int kt = graph.degree(vi);
				int kr = g.degree(vi);
				int ke = (kt - kr);
				int km = kr*precision + ke*((int)(lambda*precision));
				kmixedMap[vi] = km;
			}
			
			if(!isRemoved) {
				currentKM = Integer.MAX_VALUE;
				for (int vi: g.getVertices()) {
					int km = kmixedMap[vi];
					if (currentKM > km) {
						currentKM = km;
					}
				}
			}
		}
		
		return kmixedMap;
	}
	
	public double getNodeValueDouble(String v) {
		return (double)nodeValueMap[graph.getIndex(v)]/precision;
	}

}
