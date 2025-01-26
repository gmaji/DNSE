package sna.physica.ranking;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import sna.physica.graph.IndexedGraph;

public class KShellRanker extends Ranker {

	public KShellRanker(IndexedGraph graph) {
		super(graph);
	}

	@Override
	public void evaluate() {
		Date d1 = new Date();
		System.out.println("Entry: KShellRanker.evaluate():Time:"+d1.getTime());
		preProcessing();

		int[] kshellMap = decompose();
		
		for (int v=0; v<graph.getVertexCount(); v++) {
			int value = kshellMap[v];
			nodeValueMap[v] = value;
		}
		
		postProcessing();
		Date d2 = new Date();
		System.out.println("Exit: KShellRanker.evaluate(): Time:"+d2.getTime()+" Time Taken in seconds = "+(d2.getTime()-d1.getTime())/1000);
	}

	private int[] decompose() {

		Graph<Integer, Integer> g = new UndirectedSparseGraph<Integer, Integer>();
		for (int vi=0; vi<graph.getVertexCount(); vi++) {
			for (int vj: graph.getNeighbors(vi)) {
				g.addEdge(g.getEdgeCount(), vi, vj);
			}
		}
		
		int[] kshellMap = new int[graph.getVertexCount()]; 
		
		int currentKS = 1;
		while (g.getVertexCount() > 0) {
			boolean isRemoved = false;
			Set<Integer> targets = new HashSet<Integer>(g.getVertices());
			for (int vi: targets) {
				int k = g.degree(vi);
				if (k <= currentKS) {
					g.removeVertex(vi);
					isRemoved = true;
					kshellMap[vi] = currentKS;
				}
			}
			if(!isRemoved) currentKS++;
		}
		
		return kshellMap;
	}
}
