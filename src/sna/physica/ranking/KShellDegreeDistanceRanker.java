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
 *      
 */
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import sna.physica.graph.IndexedGraph;

public class KShellDegreeDistanceRanker extends Ranker {
	public int precision;
	 public int r;
	// public double[] nodeValues;
	 public KShellRanker kshellRanker;
	/*public MyRanker(IndexedGraph graph) {
		super(graph);
	}*/
	public KShellDegreeDistanceRanker(IndexedGraph graph, KShellRanker kshellRanker, int r, int precision) {
		super(graph);
		this.r=r;
		this.precision = precision;
		this.kshellRanker = kshellRanker;
		
	}

	@Override
	public void evaluate() {
	System.out.println("Start: KShellDegreeDistanceRanker.evaluate():: Time: "+new Date());
	preProcessing();
	Graph<Integer, Integer> g = new UndirectedSparseGraph<Integer, Integer>();
	for (int vi=0; vi<graph.getVertexCount(); vi++) {
		for (int vj: graph.getNeighbors(vi)) {
			g.addEdge(g.getEdgeCount(), vi, vj);
			}
		}

	for (int core=0; core < graph.getVertexCount(); core++) {
		//	Map<Integer,Set<Integer>> dmap = new HashMap<Integer,Set<Integer>>();
			HashSet<Integer> processedNodes = new HashSet<Integer>();
			Set<Integer> temp1 = graph.getNeighborsByIndex(core);
			processedNodes.add(core); // add the node for which neighbors are computed
		//	dmap.put(1, temp1);
			HashSet<Integer> temp2 = new HashSet<Integer>();
			
		for(int v1:temp1)
		processedNodes.add(v1); // add all first hop neighbors
			
		for(int v1:temp1) {
			Set<Integer> neighbors = graph.getNeighborsByIndex(v1);
			for(Integer v2:neighbors) {
			if(processedNodes.isEmpty() || !processedNodes.contains(v2)) {
				processedNodes.add(v2);
				temp2.add(v2);
			}
			}
			}
	//	dmap.put(2, temp2);
		
	for(Integer v1:temp2)
	processedNodes.add(v1); // add 2nd hop neighbors
		
	HashSet<Integer> temp3 = new HashSet<Integer>();
		for(Integer v1:temp2) {
			Set<Integer> neighbors = graph.getNeighborsByIndex(v1);
				
			for(Integer v2:neighbors) {
				if(processedNodes.isEmpty() || !processedNodes.contains(v2)) {
				processedNodes.add(v2);
					temp3.add(v2);
			}
		}
	}
	//	dmap.put(3, temp3);	
			
		/*	System.out.print("\n First level node of: "+graph.getVertex(core)+"\n");
			for(Integer x: temp1) 
				System.out.print(graph.getVertex(x)+"\t");
			System.out.print("\n Second level node of: "+graph.getVertex(core)+"\n");
			for(Integer x: temp2)
				System.out.print(graph.getVertex(x)+"\t");
			System.out.print("\n Third level node of: "+graph.getVertex(core)+"\n");
			for(Integer x: temp3)
				System.out.print(graph.getVertex(x)+"\t");
		*/
		
		/* Compute any measures  up to 3 level node list */
		long ks_core = kshellRanker.getNodeValue(core);
		double measure = 0.0;
			
		for(Integer x: temp1) {
				long ks_x = kshellRanker.getNodeValue(x);	
				int k_x = graph.degree(x);
				measure += k_x*ks_x;
		}
			
		for(Integer x: temp2) {
				long ks_x = kshellRanker.getNodeValue(x);	
				int k_x = graph.degree(x);
				measure +=(double)k_x*ks_x/4.0;
		}
		
		for(Integer x: temp3) {
				long ks_x = kshellRanker.getNodeValue(x);	
				int k_x = graph.degree(x);
				measure +=(double)k_x*ks_x/9.0;
		}
			
		double value = ks_core * measure ;
		//System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ value:: "+value+"    node:::"+core);
		nodeValueMap[core] = (long)(value * precision);
	}
	
	postProcessing(); 
	System.out.println("End: KShellDegreeDistanceRanker.evaluate():: Time: "+new Date());	
}
		
	public double getNodeValueDouble(String v) {
		return (double)nodeValueMap[graph.getIndex(v)]/precision;
	}

	public double getNodeValueDouble(int v) {
		return (double)nodeValueMap[v]/precision;
	}
	
	
	}