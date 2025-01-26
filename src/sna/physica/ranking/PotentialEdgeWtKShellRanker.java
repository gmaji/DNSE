package sna.physica.ranking;
/***
 * B. Wei, J. Liu, D. Wei, C. Gao, Y. Deng, Weighted k-shell decomposition for complex networks based on potential edge weights, 
 * Physica A (2014), http://dx.doi.org/10.1016/j.physa.2014.11.012
 */

import java.util.HashSet;
import java.util.Set;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import sna.physica.graph.IndexedGraph;

public class PotentialEdgeWtKShellRanker extends Ranker {
double alpha;
	public PotentialEdgeWtKShellRanker(IndexedGraph graph) {
		this(graph,0.5);
	}

	public PotentialEdgeWtKShellRanker(IndexedGraph graph, double alpha) {
		super(graph);
		this.alpha = alpha;
	}
	
	@Override
	public void evaluate() {
		System.out.println("Entry: PotentialEdgeWtKShellRanker.evaluate()");
		preProcessing();

		int[] kshellMap = decompose();
		
		for (int v=0; v<graph.getVertexCount(); v++) {
			int value = kshellMap[v];
			nodeValueMap[v] = value;
		}
		
		postProcessing();
		System.out.println("Exit: PotentialEdgeWtKShellRanker.evaluate()");
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
				int k_i = g.degree(vi);
				//System.out.println("*********** Node Start : "+vi+"   Wth degree : "+k_i);
				//System.out.println("######### Neighbors Now ##########");
				int edge_wt_sum = 0;
				for (int vj: g.getNeighbors(vi)) {
					int edge_wt = k_i + g.degree(vj);
					//System.out.println("Edge wt for node :" +vj+"  is : "+edge_wt);
					edge_wt_sum += edge_wt;
				}
				//double alpha =0.5;
				double weighted_ks = alpha*k_i + (1-alpha)* edge_wt_sum;
				
				if ((int)Math.floor(weighted_ks) <= currentKS) {
					//System.out.println("@@@@@@@@@@@@@@@@@@@@@@Node removed : "+vi +" wth KS : "+currentKS);
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
