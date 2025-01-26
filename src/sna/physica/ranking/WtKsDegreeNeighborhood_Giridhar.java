package sna.physica.ranking;
/**
 * Paper published in JOCS 2019. 
 * Maji, Giridhar. "Influential spreaders identification in complex networks with potential edge weight based k-shell degree neighborhood method." 
 * Journal of Computational Science 39 (2020): 101055. 
 * https://doi.org/10.1016/j.jocs.2019.101055
 */
import java.util.HashSet;
import java.util.Set;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import sna.physica.graph.IndexedGraph;

public class WtKsDegreeNeighborhood_Giridhar extends Ranker {
	double lambda;
	//double mu;
	int precision;
	int ksMax=1;
public WtKsDegreeNeighborhood_Giridhar(IndexedGraph graph,double lambda, int precision) {
		super(graph);
		this.lambda = lambda;
		//this.mu = mu;
		this.precision = precision;
	}

	@Override
	public void evaluate() {
		
		preProcessing();

		int[] kshellMap = decompose();
		double avg_ks = getAverageKShellIndex(kshellMap);
		double avg_degree = getAverageDegree();
		double alpha;
		/*KShellRanker kshellRanker = new KShellRanker(graph);
		kshellRanker.evaluate();
		int ksMax = kshellRanker.getRankValue(1);
		*/
		System.out.println(" ksMax = "+ksMax+"  And avg_ks = "+avg_ks+ " And Avg_degree = "+avg_degree);
		double alpha1 = (avg_ks/avg_degree)*1000;
		long alpha2 = Math.round(alpha1);
		alpha = (double)alpha2/1000;
		System.out.println("Lambda = " +lambda + "  Alpha ==" +alpha);
		//int[] coreness = new int[graph.getVertexCount()];
		Double[] coreness = new Double[graph.getVertexCount()];
		
		for (int v=0; v<graph.getVertexCount(); v++) {
			int k_i  = graph.degree(v);
			int ks_i = kshellMap[v];
			coreness[v] = 0.0;
			double edge_wt = 0.0;
			for (int w: graph.getNeighbors(v)) {
				int k_j  = graph.degree(w);
				int ks_j = kshellMap[w];
				edge_wt += (ks_i + ks_j)+ (lambda + alpha)*(k_i + k_j);
				//coreness[v] += kshellMap[w];
			}
			coreness[v] = edge_wt;
			nodeValueMap[v] = (int)(edge_wt* precision);
		}
		
		    postProcessing();
	}
	private double getAverageKShellIndex(int[] kshellMap) {
		int n = kshellMap.length;
		double sum =0 ;
		for(int ks:kshellMap){
			sum += ks;
		}
		return (sum/n);
	}

	public double getNodeValueDouble(String v) {
		return (double)nodeValueMap[graph.getIndex(v)]/precision;
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
		ksMax = currentKS;
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
}
