package sna.physica.ranking;
/***
 * X_KS: This measure loops up to r hop neighborhood.
 *  Loop through all nodes one by one and do the following:
 *  	Again loop through all nodes and for each node:
 *  		add the (node degree/dist^2) if the distance is <=r
 *  	multiply by ks index
 *      save for each node
 *      
 *      It should use less memory as the shortestpath distance map is being computed for every k-shell instead of keeping 
 *      all nodes distance map it keeps only for the nodes belongs to that k-shell
 *      
 *      Another advantage of this method will be the possibility of not computing for lower shell values as only top ranked
 *      nodes are important and as we are going by k-shells and starts with the highest k-shell and going towards lower shells
 *      So depending on the fraction of top nodes required we could stop well in advance without calculating  for all nodes.
 *  
 *      Publication:
 *      Maji, Giridhar, Animesh Dutta, Mariana Curado Malta, and Soumya Sen. 
 *      "Identifying and ranking super spreaders in real world complex networks without influence overlap." 
 *      Expert Systems with Applications 179 (2021): 115061.
 */
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import edu.uci.ics.jung.algorithms.shortestpath.UnweightedShortestPath;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import sna.physica.graph.IndexedGraph;

public class Ranker_X_KS extends Ranker {
	public int precision;
	 public int r;
	 public double[] nodeValues;
	 public KShellRanker kshellRanker;
	/*public MyRanker(IndexedGraph graph) {
		super(graph);
	}*/
	public Ranker_X_KS(IndexedGraph graph, KShellRanker kshellRanker, int r, int precision) {
		super(graph);
		this.r=r;
		this.precision = precision;
		this.kshellRanker = kshellRanker;
		
	}

	@Override
	public void evaluate() {
		System.out.println("Start: MyRanker_KS.evaluate():: Time: "+new Date());
		preProcessing();
		nodeValues = new double[graph.getVertexCount()];
		
		//KShellRanker kshellRanker = new KShellRanker(graph);
		//kshellRanker.evaluate();
		
		Graph<Integer, Integer> g = new UndirectedSparseGraph<Integer, Integer>();
		for (int vi=0; vi<graph.getVertexCount(); vi++) {
			for (int vj: graph.getNeighbors(vi)) {
				g.addEdge(g.getEdgeCount(), vi, vj);
			}
		}

		//DijkstraDistance<Integer, Integer> dijkstra = new DijkstraDistance<Integer, Integer>(g);
		//dijkstra.enableCaching(true);

		 UnweightedShortestPath<Integer, Integer> dijkstra1 = 
					new UnweightedShortestPath<Integer, Integer>(g);
		//int[] theta1 = new int[g.getVertexCount()];	
		//double[] theta = new double[g.getVertexCount()];	
//		int count = 1;
		int ksMax = (int)kshellRanker.getRankValue(1);
		//System.out.println("KS MAX = "+ksMax);
		//for (int rank = 1; rank <= ksMax; rank++){
		for (int rank = ksMax; rank > 0; rank--){
			Set<Integer> coreSet = kshellRanker.getRankIndexSet(rank);
			if(coreSet == null)continue;
			//System.out.println("Rank = "+rank+" Node#  ="+coreSet.size());
			Map<Integer, Map<Integer, Number>> distanceMap = new HashMap<Integer, Map<Integer, Number>>();
			for (int core: coreSet) {
				int ks = (int)kshellRanker.getNodeValue(core);
				distanceMap.put(core, dijkstra1.getDistanceMap(core));
				double measure = 0.0;
				for (int v=0; v < graph.getVertexCount(); v++) {
					 Number shortest = distanceMap.get(core).get(v);
                     if (shortest == null){
                        // System.err.println("Hitting isolated!!!! -> " + isolated++);  
                     }
                     else {
                       Integer s= shortest.intValue();
                       if (s <= r && s>0) {
                    	 measure += graph.degree(v)/Math.pow(s,2);
                        } 
					}
				}
				double value = ks * measure ;
				//System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ value:: "+value+"    node:::"+core);
				nodeValues[core] = value;
				nodeValueMap[core] = (int)(value * precision);
			}
		}
	//return theta1;
		

		/*
		 * int[] thetaMap = improved(kshellRanker, r);
		 * 
		 * for (int v=0; v<graph.getVertexCount(); v++) { int value = thetaMap[v];
		 * nodeValueMap[v] = value; }
		 */
		//postProcessingReverse();
		
		
		postProcessing();
		
		System.out.println("End: MyRanker_KS.evaluate():: Time: "+new Date());
	}

	/*
	private int[] improved(KShellRanker kshellRanker, int r) {
		
		Graph<Integer, Integer> g = new UndirectedSparseGraph<Integer, Integer>();
		for (int vi=0; vi<graph.getVertexCount(); vi++) {
			for (int vj: graph.getNeighbors(vi)) {
				g.addEdge(g.getEdgeCount(), vi, vj);
			}
		}

		DijkstraDistance<Integer, Integer> dijkstra = new DijkstraDistance<Integer, Integer>(g);
		dijkstra.enableCaching(true);

		int[] theta1 = new int[g.getVertexCount()];	
		//double[] theta = new double[g.getVertexCount()];	
//		int count = 1;
		int ksMax = kshellRanker.getRankValue(1);
		//System.out.println("KS MAX = "+ksMax);
		//for (int rank = 1; rank <= ksMax; rank++){
		for (int rank = ksMax; rank > 0; rank--){
			Set<Integer> coreSet = kshellRanker.getRankIndexSet(rank);
			if(coreSet == null)continue;
			//System.out.println("Rank = "+rank+" Node#  ="+coreSet.size());
			Map<Integer, Map<Integer, Number>> distanceMap = new HashMap<Integer, Map<Integer, Number>>();
			for (int core: coreSet) {
				int ks = kshellRanker.getNodeValue(core);
				distanceMap.put(core, dijkstra.getDistanceMap(core));
				double measure = 0.0;
				for (int v=0; v < graph.getVertexCount(); v++) {
					 Number shortest = distanceMap.get(core).get(v);
                     if (shortest == null){
                        // System.err.println("Hitting isolated!!!! -> " + isolated++);  
                     }
                     else {
                       Integer s= shortest.intValue();
                       if (s <= r && s>0) {
                    	 measure += graph.degree(v)/Math.pow(s,2);
                        } 
					}
				}
				double value = ks * measure ;
				//System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ value:: "+value+"    node:::"+core);
				theta1[core] = (int)(value * precision);
			}
		}
	return theta1;
		
	 } */
	
	public double getNodeValueDouble(String v) {
		return (double)nodeValueMap[graph.getIndex(v)]/precision;
	}

	public double getNodeValueDouble(int v) {
		return (double)nodeValueMap[v]/precision;
	}
	
	
	}