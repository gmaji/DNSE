package sna.physica.ranking;
/***
 * My improvement over the below paper for Weighted networks. Finally this one is used in J5 (EwDN method- JOCS)
 * Instead of potential edge weight = k_i +k_j; I use real edge weights. Rest part remains the same.
 * B. Wei, J. Liu, D. Wei, C. Gao, Y. Deng, Weighted k-shell decomposition for complex networks based on potential edge weights, 
 * Physica A (2014), http://dx.doi.org/10.1016/j.physa.2014.11.012
 * 
 * Publication:
 * Maji, Giridhar, and Soumya Sen. "Ranking influential nodes in complex network using edge weight degree based shell decomposition." 
 * Journal of Computational Science 74 (2023): 102179.
 */

import java.util.HashSet;
import java.util.Set;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import sna.physica.graph.IndexedGraph;

public class EdgeWtDegreeDecomposition extends Ranker {
//double alpha;
//double avg_edge_wt;
int precision;
	public EdgeWtDegreeDecomposition(IndexedGraph graph) {
		this(graph,100);
	}

	public EdgeWtDegreeDecomposition(IndexedGraph graph, int precision) {
		super(graph);
		//this.alpha = alpha;
		//this.avg_edge_wt = avg_edge_wt;
		this.precision = precision;
	}
	
	@Override
	public void evaluate() {
		System.out.println("Entry: MyEdgeWtDegreeDecomposition-EwDn.evaluate()");
		preProcessing();

		int[] kshellMap = decompose();
		
		for (int v=0; v<graph.getVertexCount(); v++) {
			int value = kshellMap[v];
			nodeValueMap[v] = value;
		}
		
		postProcessing();
		System.out.println("Exit: MyEdgeWtDegreeDecomposition-EwDn.evaluate()");
	}

	private int[] decompose() {

		Graph<Integer, Integer> g = new UndirectedSparseGraph<Integer, Integer>();
		for (int vi=0; vi<graph.getVertexCount(); vi++) {
			for (int vj: graph.getNeighbors(vi)) {
				g.addEdge(g.getEdgeCount(), vi, vj);
			}
		}
		
		int[] kshellMap = new int[graph.getVertexCount()]; 
		//double avg_k = getAverageDegree();
		int currentKS = 1*precision;
		while (g.getVertexCount() > 0) {
			boolean isRemoved = false;
			Set<Integer> targets = new HashSet<Integer>(g.getVertices());
			for (int vi: targets) {
				int k_i = g.degree(vi);
				//System.out.println("*********** Node Start : "+vi+"   Wth degree : "+k_i);
				//System.out.println("######### Neighbors Now ##########");
				double edge_wt_sum = 0.0;
				for (int vj: g.getNeighbors(vi)) {
					//int edge_wt = k_i + g.degree(vj);
					double edge_wt = graph.getEdgeWeight(vi, vj);
					//System.out.println("Edge wt for node :" +vj+"  is : "+edge_wt);
					edge_wt_sum += edge_wt*g.degree(vj);
				}
				//double alpha =0.5;
				
				//double weighted_ks = alpha*k_i + (1-alpha)*(edge_wt_sum);
				double weighted_ks = edge_wt_sum;
				if ((int)Math.floor(weighted_ks*precision) <= currentKS) {
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
	
	
	double getAverageDegree(){
		long degree_k = 0;
		//long degree_sqr_k =0;
		int vertex_count = graph.getVertexCount();
		System.err.println("vertex_count = "+vertex_count);	
	for (int v=0; v<vertex_count; v++) {
		int value = graph.degree(v);
		//System.out.println("node :"+v +" Degree : "+value);
		degree_k = degree_k + value;
		//degree_sqr_k = degree_sqr_k + (value*value);
	}
	//System.out.println("Total degree = "+ degree_k);
	double avg_k = (double)degree_k/vertex_count;
	System.out.println("avg_k = "+avg_k);
	return avg_k;
	}
	
	public double getNodeValueDouble(String v) {
		return (double)nodeValueMap[graph.getIndex(v)]/precision;
	}
	
}
