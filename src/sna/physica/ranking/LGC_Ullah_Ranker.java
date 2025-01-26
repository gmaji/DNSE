package sna.physica.ranking;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraDistance;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import sna.physica.graph.IndexedGraph;
/**
 * Identifying vital nodes from local and global perspectives in complex networks
 * Expert Systems with Applications Volume 186, 30 December 2021, 115778
 * https://doi.org/10.1016/j.eswa.2021.115778
 * 
 * @author Raju
 *
 */
public class LGC_Ullah_Ranker extends Ranker {
	double alpha;
	int precision;
	public LGC_Ullah_Ranker(IndexedGraph graph) {
		super(graph);
		this.alpha = 1.0;
		this.precision = 100;
	}
	public LGC_Ullah_Ranker(IndexedGraph graph, int precision) {
		super(graph);
		this.alpha = alpha;
		this.precision = precision;
	}
	@Override
	public void evaluate() {
		Date d1 = new Date();
		System.out.println("Entry: LGC_Ullah_Ranker.evaluate():Time:"+d1.getTime());
		preProcessing();
		int n = graph.getVertexCount();
		
		Graph<Integer, Integer> g = new UndirectedSparseGraph<Integer, Integer>();
		for (int vi=0; vi<graph.getVertexCount(); vi++) {
			for (int vj: graph.getNeighbors(vi)) {
				g.addEdge(g.getEdgeCount(), vi, vj);
			}
		}
		
		DijkstraDistance<Integer, Integer> dijkstra = new DijkstraDistance<Integer, Integer>(g);
		dijkstra.enableCaching(true);
		
		for (int i=0; i<graph.getVertexCount(); i++) {
			int k_i = graph.degree(i);
			double l_i = (double)k_i/n;
			Map<Integer, Number> distmap_i = dijkstra.getDistanceMap(i);
			double gisum_i=0.0;
			//System.out.println("vi: "+graph.getVertex(i));
			for( Integer vj: distmap_i.keySet()) {
				if(distmap_i.get(vj)!=null && i!=vj) {
				double k_j = graph.degree(vj);
				int d_ij = distmap_i.get(vj).intValue();
				double gi = (Math.sqrt(k_j+alpha))/d_ij;
				gisum_i+= gi;
				//System.out.println("v("+graph.getVertex(i)+"-"+graph.getVertex(vj)+"): distance: "+d_ij+" GI:"+gi);
				}
			}
				
			double lgc_i = l_i* gisum_i;
			//System.out.println("**gisum_i: "+gisum_i + " N:"+n+" L_i:"+l_i+" lgc_"+graph.getVertex(i)+" :"+lgc_i);
			nodeValueMap[i] = (int)(lgc_i* precision) ;
		//	System.out.println("************ LSS_i: "+lss_i);
		}
		
		postProcessing();
		Date d2 = new Date();
		System.out.println("Exit: LGC_Ullah_Ranker.evaluate(): Time:"+d2.getTime()+" Time Taken in seconds = "+(d2.getTime()-d1.getTime())/1000.0);
	}
	
	
	public double getNodeValueDouble(String v) {
		return (double)nodeValueMap[graph.getIndex(v)]/precision;
	}
}
