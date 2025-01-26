package sna.physica.ranking;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import sna.physica.graph.IndexedGraph;
/**
 * LSS: A locality-based structure system to evaluate the spreader’s importance in social complex networks, 
 * Expert Systems with Applications Volume 228, 15 October 2023, 120326
 * https://doi.org/10.1016/j.eswa.2023.120326
 * 
 * @author Raju
 *
 */
public class LSS_Ullah_Ranker extends Ranker {
	//double alpha;
	int precision;
	public LSS_Ullah_Ranker(IndexedGraph graph) {
		super(graph);
		//this.alpha = 1;
		this.precision = 100;
	}
	public LSS_Ullah_Ranker(IndexedGraph graph, int precision) {
		super(graph);
		//this.alpha = alpha;
		this.precision = precision;
	}
	@Override
	public void evaluate() {
		long t1 = new Date().getTime();
		System.out.println("START :::: LSS_Ullah_Ranker.evaluate():: Time: "+new Date(t1));
		preProcessing();
		int n = graph.getVertexCount();
		int[] kshellMap = decompose();
		for (int i=0; i<graph.getVertexCount(); i++) {
			int k_i = graph.degree(i);
			int ks_i = kshellMap[i];
			int ewt_i = k_i * ks_i;
			//System.out.println("node#:"+i +"node label: "+graph.getVertex(i)+" k_i:"+k_i+" ks_i: "+ks_i+" ewt_i:"+ewt_i);
			double twsum_i=0.0;
			for(int j:graph.getNeighbors(i)){
				int alpha = getNumOfTriangles(i,j);
				int k_j = graph.degree(j);
				int ks_j = kshellMap[j];
				int ewt_j = k_j * ks_j;
				double cf_ij = (ks_i - alpha) * (ks_j - alpha);
				double tw_ij = (cf_ij* ewt_i * ewt_j)/(alpha+1);
				//System.out.println("tw_ij: "+tw_ij +" alpha:"+alpha);
				twsum_i+= tw_ij;
			}
		//	System.out.println("************ twsum_i: "+twsum_i + " N:"+n);
			double lss_i = (ks_i* twsum_i)/n;
			nodeValueMap[i] = (int)(lss_i* precision) ;
		//	System.out.println("************ LSS_i: "+lss_i);
		}
		
		postProcessing();
		long t2 = new Date().getTime();
		System.out.println("END :::: LSS_Ullah_Ranker.evaluate():: Time: "+new Date(t2)+"   Time Taken: "+(t2-t1)/1000.0 +" seconds");
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
		//ksMax = currentKS;
		return kshellMap;
	}
	private int getNumOfTriangles(int vi, int vj) {
		Set<Integer> phi1 = graph.getNeighborsByIndex(vi);
		Set<Integer> phi2 = graph.getNeighborsByIndex(vj);
        phi1.retainAll(phi2); // only keeps the neighbors of the set phi1 that are also present in phi2.
        return phi1.size();
	}
	public double getNodeValueDouble(String v) {
		return (double)nodeValueMap[graph.getIndex(v)]/precision;
	}
}
