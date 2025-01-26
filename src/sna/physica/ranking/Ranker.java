package sna.physica.ranking;
/**
 * Ranker implementation where all values are in Long/long to increase the capacity 
 * and stop from overflowing with large networks
 */
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import sna.physica.graph.CIReturnObject;
import sna.physica.graph.IndexedGraph;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraDistance;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

public abstract class Ranker {

	protected IndexedGraph graph;

	protected long[] nodeValueMap;
	
	long[] rankValueMap;
	
	Map<Long, Integer> valueRankMap;
	
	Map<Integer, Set<Integer>> rankNodeSet;
	
	public Ranker(IndexedGraph graph) {
		this.graph = graph;
	}
	
	public abstract void evaluate();

	protected void preProcessing() {
		nodeValueMap = new long[graph.getVertexCount()];
	}
	
	protected void postProcessing() {
		Set<Long> valueSet = new TreeSet<Long>();
		for (int i=0; i<nodeValueMap.length; i++) {
			valueSet.add(nodeValueMap[i]);
		}
		rankValueMap = new long[valueSet.size()];
		valueRankMap = new HashMap<Long, Integer>();
		rankNodeSet = new HashMap<Integer, Set<Integer>>();
		int rank = valueSet.size();
		for (long value: valueSet) {
			rankValueMap[rank-1] = value;
			valueRankMap.put(value, rank--);
		}
		for (int i=0; i<nodeValueMap.length; i++) {
			int r = valueRankMap.get(nodeValueMap[i]);
			Set<Integer> nodeSet = rankNodeSet.get(r);
			if (nodeSet == null) {
				nodeSet = new HashSet<Integer>();
				rankNodeSet.put(r, nodeSet);
			}
			nodeSet.add(i);
		}
	}

	protected void postProcessingReverse() {
		Set<Long> valueSet = new TreeSet<Long>();
		for (int i=0; i<nodeValueMap.length; i++) {
			valueSet.add(nodeValueMap[i]);
		}
		rankValueMap = new long[valueSet.size()];
		valueRankMap = new HashMap<Long, Integer>();
		rankNodeSet = new HashMap<Integer, Set<Integer>>();
		int rank = 1;
		for (long value: valueSet) {
			rankValueMap[rank-1] = value;
			valueRankMap.put(value, rank++);
		}
		for (int i=0; i<nodeValueMap.length; i++) {
			int r = valueRankMap.get(nodeValueMap[i]);
			Set<Integer> nodeSet = rankNodeSet.get(r);
			if (nodeSet == null) {
				nodeSet = new HashSet<Integer>();
				rankNodeSet.put(r, nodeSet);
			}
			nodeSet.add(i);
		}
	}
	
	public Graph<String, Integer> getGraph() {
		return graph.getGraph();
	}

	public int length() {
		return rankValueMap.length;
	}
	
	public long getRankValue(int rank) {
		return rankValueMap[rank-1];
	}

	public int getRankVolume(int rank) {
		if (rankNodeSet.get(rank) == null) {
			return 0;
		}
		return rankNodeSet.get(rank).size();
	}
	
	public Set<Integer> getRankIndexSet(int rank) {
		return rankNodeSet.get(rank);
	}

	public Set<String> getRankNodeSet(int rank) {
		if (rankNodeSet.get(rank) == null) {
			return null;
		}
		Set<String> nodeSet = new TreeSet<String>();
		for (int vi: rankNodeSet.get(rank)) {
			nodeSet.add(graph.getVertex(vi));
		}
		return nodeSet;
	}

	public int getNodeRank(int vi) {
		return valueRankMap.get(nodeValueMap[vi]);
	}

	public int getNodeRank(String v) {
		return valueRankMap.get(nodeValueMap[graph.getIndex(v)]);
	}

	public long getNodeValue(int vi) {
		return nodeValueMap[vi];
	}

	public long[] getNodeValueMap() {
		return nodeValueMap;
	}
	public long getNodeValue(String v) {
		return nodeValueMap[graph.getIndex(v)];
	}

	public double getMonotonicity() {
		double n = graph.getVertexCount();
		double sum = 0;
		for (int rank: rankNodeSet.keySet()) {
			double nr = rankNodeSet.get(rank).size();
			sum += nr*(nr-1);
		}
		double value = 1.0 - (sum / (n*(n-1)));
		return value*value;
	}
	
	public String getNodeByRankOrder(int order) {
		int currOrder = 0;
		for (int rank=1; rank<=this.length(); rank++) {
			if (order > currOrder + this.getRankVolume(rank)) {
				currOrder += this.getRankVolume(rank);
				continue;
			}
			for (int v: this.getRankIndexSet(rank)) {
				if (++currOrder == order) {
					return graph.getVertex(v);
				}
			}
		}
		return null;
	}

	public int getIndexByRankOrder(int order) {
		int currOrder = 0;
		for (int rank=1; rank<=this.length(); rank++) {
			if (order > currOrder + this.getRankVolume(rank)) {
				currOrder += this.getRankVolume(rank);
				continue;
			}
			for (int v: this.getRankIndexSet(rank)) {
				if (++currOrder == order) {
					return v;
				}
			}
		}
		return -1;
	}
	
	public Set<Integer> getTopSeedNodeSetByFraction(double p){
	Set<Integer> nodeSet = new HashSet<Integer>();
	//double p = k*0.1; 
	//int seedNodeCount = (int)(graph.getVertexCount()*p); 
	int seedNodeCount = (int)(Math.round(graph.getVertexCount()*p)); 
	//System.out.println("Vertex Count: "+graph.getVertexCount()+"  seedNodeCount:="+seedNodeCount);
	int rank=1;
	int nodeCount = 0;
	while(nodeCount< seedNodeCount){
		Set<Integer> vi = this.getRankIndexSet(rank);
		for(Integer x:vi){
			nodeSet.add(x);	
			nodeCount++;
			//System.out.println("Rank::"+rank+"   Added node is: "+graph.getVertex(x)+"  Node count::"+nodeCount);
			if(nodeCount>=seedNodeCount)
				break;
		}
		rank++;
	}
	return nodeSet;
	}

	/*Below method is for Last rank with no overlap. It is also possible to compute using minDist=1*/
	public CIReturnObject getTopSeedNodeSetByFractionWithoutOverlap(double p){
		CIReturnObject retObj = new CIReturnObject();
		Set<Integer> nodeSet = new HashSet<Integer>();
		//double p = k*0.1; 
		//int seedNodeCount = (int)(graph.getVertexCount()*p); 
		int seedNodeCount = (int)(Math.round(graph.getVertexCount()*p)); 
		//System.out.println("Vertex Count: "+graph.getVertexCount()+"  seedNodeCount:="+seedNodeCount);
		int rank=1;
		int nodeCount = 0;
		while(nodeCount< seedNodeCount){
			Set<Integer> vi = this.getRankIndexSet(rank);
			for(Integer x:vi){
				nodeSet.add(x);	
				nodeCount++;
			//System.out.println("Rank::"+rank+"   Added node is: "+graph.getVertex(x)+"  Node count::"+nodeCount);
				if(nodeCount>=seedNodeCount)
					break;
			}
			rank++;
		}
		retObj.setSeedNodeSet(nodeSet);
		retObj.setLastRank(rank);
		return retObj;
		}
	
public CIReturnObject getTopSeedNodeSetByFractionImproved(double p, int minDist){
	    CIReturnObject retObj = new CIReturnObject();
	    Set<Integer> nodeSet = new HashSet<Integer>();
		Graph<Integer, Integer> g = new UndirectedSparseGraph<Integer, Integer>();
		for (int vi=0; vi<graph.getVertexCount(); vi++) {
			for (int vj: graph.getNeighbors(vi)) {
				g.addEdge(g.getEdgeCount(), vi, vj);
			}
		}
		//UnweightedShortestPath<Integer, Integer> dijkstra1 = new UnweightedShortestPath<Integer, Integer>(g);
		DijkstraDistance<Integer, Integer> dijkstra = new DijkstraDistance<Integer, Integer>(g);
		dijkstra.enableCaching(true);
		dijkstra.setMaxDistance(minDist);
		//double p = k*0.1; 
		//int seedNodeCount = (int)(graph.getVertexCount()*p); 
		int seedNodeCount = (int)(Math.round(graph.getVertexCount()*p)); 
		//System.out.println("Vertex Count: "+graph.getVertexCount()+"  seedNodeCount:="+seedNodeCount);
		int rank=1;
		int nodeCount = 0;
		
		/**
		 * Adding the Node with rank=1. If multiple nodes present with rank=1
		 * then only the first one gets added
		 */
		/*Set<Integer> vx = this.getRankIndexSet(1);
		for(Integer x:vx){
			nodeSet.add(x);
			nodeCount++;
			break;
			}
		*/	
		while(nodeCount< seedNodeCount){
			Set<Integer> vi = this.getRankIndexSet(rank);
			if(vi==null || vi.size()==0) {
				System.out.println("No node with Rank :"+rank+"  fraction% p:="+p+"  nodecount:="+nodeCount);	
				break;
			}
			//Set<Integer> temp = vi;
			for(Integer x: vi){
				Map<Integer, Number> distanceMap = new HashMap<Integer, Number>();
				distanceMap = dijkstra.getDistanceMap(x);
				boolean flag=true;
				for(Integer y: nodeSet){
					//Number shortest = dijkstra.getDistance(x,y);
					Number shortest = distanceMap.get(y);
					if(shortest!=null && shortest.intValue()<minDist){
						flag = false;
					}
				}
				if(flag){
					nodeSet.add(x);
					nodeCount++;
				}
				
				if(nodeCount>=seedNodeCount)
					break;
			}
			//System.out.println("nodeCount:::"+nodeCount);
			rank++;
		}
		//System.out.println("out of while loop.... with nodeCount:="+nodeSet.size()+"   and  Last Rank:="+rank);
		retObj.setSeedNodeSet(nodeSet);
		retObj.setLastRank(rank-1);
		return retObj;
		}

public CIReturnObject getTopSeedNodeSetByFractionImprovedExperimental(double p, int minDist){
    CIReturnObject retObj = new CIReturnObject();
    Set<Integer> nodeSet = new HashSet<Integer>();
    Set<Integer> nodeSetNeighbors = new HashSet<Integer>();
	Graph<Integer, Integer> g = new UndirectedSparseGraph<Integer, Integer>();
	for (int vi=0; vi<graph.getVertexCount(); vi++) {
		for (int vj: graph.getNeighbors(vi)) {
			g.addEdge(g.getEdgeCount(), vi, vj);
		}
	}
	
	int seedNodeCount = (int)(Math.round(graph.getVertexCount()*p)); 
	//System.out.println("Vertex Count: "+graph.getVertexCount()+"  seedNodeCount:="+seedNodeCount);
	int rank=1;
	int nodeCount = 0;
	
	/**
	 * Adding the Node with rank=1. If multiple nodes present with rank=1
	 * then only the first one gets added
	 */
		
		/*  Set<Integer> vx = this.getRankIndexSet(1); 
		  for(Integer x:vx){ 
			  nodeSet.add(x);
			  //nodeSetNeighbors.addAll(get_1_Hop_Neighbors(g,x)); 
			  nodeSetNeighbors.addAll(g.getNeighbors(x)); 
			  nodeCount++; 
			  break; 
		  }
		*/ 
		
	while(nodeCount< seedNodeCount){
		Set<Integer> vi = this.getRankIndexSet(rank);
		
		if(vi==null || vi.size()==0) {
			System.out.println("##### No node with Rank :"+rank+"  fraction% p:="+p+"  nodecount:="+nodeCount);	
			break;
		}
	//	for(Integer z:vi)System.out.println("Rank:"+rank+"nodes:"+graph.getVertex(z));
		for(Integer x: vi){
			if(!nodeSetNeighbors.contains(x)) {
				 nodeSet.add(x);	
				// nodeSetNeighbors.addAll(get_1_Hop_Neighbors(g,x));
				 if(minDist==2) {
				 nodeSetNeighbors.addAll(g.getNeighbors(x));
				 }else if(minDist==3) {
					 nodeSetNeighbors.addAll(get_2_Hop_Neighbors(g,x));
				 }else if(minDist==4) {
					 nodeSetNeighbors.addAll(get_3_Hop_Neighbors(g,x));
				 }else {
					 System.err.println("Wrong Value of MinDist!!!!!");
				 }
				 
				 nodeCount++; 
			}
		}
		rank++;
	}
	//System.out.println("out of while loop.... with nodeCount:="+nodeSet.size()+"   and  Last Rank:="+rank);
	retObj.setSeedNodeSet(nodeSet);
	retObj.setLastRank(rank-1);
	return retObj;
	}

private Set<Integer> get_2_Hop_Neighbors(Graph<Integer, Integer> g, Integer x) {
	Set<Integer> neighbors = new HashSet<Integer>();
	for (Integer v:g.getNeighbors(x)) {
		for(Integer w: g.getNeighbors(v)) {
			if (neighbors.isEmpty()) {
				neighbors.add(w);
			}else if(!neighbors.contains(w)) {
				neighbors.add(w);
				}
		}
	}
	return neighbors;
}

private Set<Integer> get_3_Hop_Neighbors(Graph<Integer, Integer> g, Integer x) {
	Set<Integer> neighbors = new HashSet<Integer>();
	for (Integer v:g.getNeighbors(x)) {
		for(Integer w: g.getNeighbors(v)) {
			for(Integer u: g.getNeighbors(w)) {
			if (neighbors.isEmpty()) {
				neighbors.add(u);
			}else if(!neighbors.contains(u)) {
				neighbors.add(u);
				}
		   }
		}
	}
	return neighbors;
}

public double calculateDistinctMetric(KShellRanker kshellRanker ){
		int n = this.nodeValueMap.length;
		//System.out.println("n:="+n);
		long[] nodeValueMapKs = kshellRanker.getNodeValueMap();
		//System.out.println("nodeValueMapKs.length :="+nodeValueMapKs.length);
		TreeSet<Long> valueSet = new TreeSet<Long>();
		for (int i=0; i<nodeValueMapKs.length; i++) {
			valueSet.add(nodeValueMapKs[i]);
		}
		//System.out.println("valueSet.size() :="+valueSet.size());
		//int ksMax = kshellRanker.getRankValue(1);
		//System.out.println("ksMax: "+ksMax);
		long ksMax1= valueSet.last();
		//System.out.println("ksMax1: "+ksMax1);
		long ksMin = valueSet.first(); //it returns the lowest value in the list
		int noOfUniqueNodes = 0 ;
		for(int k=1;k<=(ksMax1-ksMin+1);k++){
		//Set<Integer> coreSet = kshellRanker.getRankIndexSet(k); // set of all nodes with KShell rank=k
		
		Set<Integer> coreSet = kshellRanker.getRankIndexSet(k); // set of all nodes with KS value=k
		if(coreSet == null || coreSet.size()==0)
			continue; //continue with next k-shell value.
		
		//System.out.println("K-shell Rank:::"+k+" Nodes are ::"+coreSet.toString());
		int coreSetSize = coreSet.size();
		//System.out.println("k = "+k+" coreSetSize : = "+coreSetSize);
		Set<Long> coreSetNodeVals = new TreeSet<Long>();
		for (int each:coreSet){
			//System.out.println("node id::"+each+" Node Value : "+nodeValueMap[each]);
			//System.out.println("node id::"+each+" Node Value : "+nodeValueMapKs[each]+" Node Name: "+graph.getVertex(each));
			coreSetNodeVals.add(nodeValueMap[each]);
			}
				
		//System.out.println("coreSetNodeVals.size() : "+coreSetNodeVals.size());
		 noOfUniqueNodes += coreSetNodeVals.size();
		}
		double d = (double)noOfUniqueNodes/n;
		//System.out.println("Distinct Metric Value: "+d);
		return d;
	}

}
