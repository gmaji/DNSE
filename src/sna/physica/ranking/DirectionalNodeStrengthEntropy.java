package sna.physica.ranking;
import java.util.Date;
/**
 * DNSE Submitted to "Journal of Computational Mathematics and Data Science" Elsevier
 * Paper Title:  Directional Node Strength Entropy Centrality: Ranking Influential Nodes in Complex Networks
 */
import java.util.HashSet;
import java.util.Set;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import sna.physica.graph.IndexedGraph;

public class DirectionalNodeStrengthEntropy extends Ranker {
	double lambda;
	int precision;
	int ksMax=1;
public DirectionalNodeStrengthEntropy(IndexedGraph graph, double lambda, int precision) {
		super(graph);
		this.lambda = lambda;
		this.precision = precision;
	}
public DirectionalNodeStrengthEntropy(IndexedGraph graph, int precision) {
	super(graph);
	this.lambda = 0.5;
	this.precision = precision;
}
	@Override
	public void evaluate() {
		
		long t1 = new Date().getTime();
		System.out.println("START :::: DirectionalNodeStrengthV2.evaluate():: Time: "+new Date(t1));
		preProcessing();

		int[] kshellMap = decompose();
		//double avg_ks = getAverageKShellIndex(kshellMap);
		//double avg_degree = getAverageDegree();
		//double alpha;
		KShellRanker kshellRanker = new KShellRanker(graph);
		kshellRanker.evaluate();
		int ksMax = (int)kshellRanker.getRankValue(1);
		int netw_size = graph.getVertexCount();
		//System.out.println(" ksMax = "+ksMax+"  And avg_ks = "+avg_ks+ " And Avg_degree = "+avg_degree+" netw_size:"+netw_size);
		//double alpha1 = (avg_ks/avg_degree)*1000;
		Double[] coreness = new Double[netw_size];
		
		for (int v=0; v<netw_size; v++) {
			double k_i  = (double)graph.degree(v)/(netw_size-1);
			int ks_i = kshellMap[v];
			coreness[v] = 0.0;
			double directionalNodeStrength = 0.0;
			double edge_wt = 0.0;
			double directional_entropy_score=0.0;
			//double total_ns=0;
			/*
			for (int w: graph.getNeighbors(v)) {
				int k_j  = graph.degree(w);
				int ks_j = kshellMap[w];
				edge_wt = ((k_i * ks_i)+ (k_j * ks_j));
				total_ns += k_j * edge_wt;
			}*/
			for (int w: graph.getNeighbors(v)) {
				double k_j  = (double)graph.degree(w)/(netw_size-1);
				int ks_j = kshellMap[w];
				//edge_wt = ((k_i * ks_i)+(k_j * ks_j));//dnsv1
				//edge_wt = ((k_i * ks_i)+(k_j * ks_j))/(2*ksMax*k_max);//dnsv3
				edge_wt = ((k_i * ks_i)+(k_j * ks_j))/(2*ksMax);//dnsv2
				directionalNodeStrength = k_j * edge_wt;
				//double dns = nodeStrength * Math.log(nodeStrength);//dnsv1
				double directionalNodeEntropy = netw_size * directionalNodeStrength * Math.log(1/directionalNodeStrength);//dnsv2 & v3
				//System.out.println(" node1:"+graph.getVertex(v)+" node2:"+graph.getVertex(w)+" edge_wt="+edge_wt+" nodeStrength="+nodeStrength+ " directional_entropy_score="+dns);
	//System.out.println(graph.getVertex(v)+"\t"+graph.getVertex(w)+"\t"+edge_wt+"\t"+directionalNodeStrength+ "\t"+directionalNodeEntropy);
			
				directional_entropy_score+= directionalNodeEntropy;
				//coreness[v] += kshellMap[w];
			}
			//coreness[v] = edge_wt;
			int finalNodeScore = (int)(netw_size * k_i * directional_entropy_score * precision);
			//nodeValueMap[v] = (int)(k_i * directional_entropy_score * precision);
			nodeValueMap[v] = finalNodeScore;
			//System.out.println("Final Node Score for Node "+graph.getVertex(v)+"  is "+(double)finalNodeScore/precision);
		}
		
		    postProcessing();
		    long t2 = new Date().getTime();
			System.out.println("END :::: DirectionalNodeStrengthV2.evaluate():: Time: "+new Date(t2)+"   Time Taken: "+(t2-t1)/1000.0 +" seconds");
		    
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
