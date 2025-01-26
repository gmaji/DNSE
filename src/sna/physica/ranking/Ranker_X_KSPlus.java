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
import sna.physica.graph.IndexedGraph;

public class Ranker_X_KSPlus extends Ranker {
	public int precision;
	 public int r;
	 public Ranker_X_KS myRanker3_ks;
	 
	/*public MyRanker(IndexedGraph graph) {
		super(graph);
	}*/
	public Ranker_X_KSPlus(IndexedGraph graph, Ranker_X_KS myRanker3_ks,  int r, int precision) {
		super(graph);
		this.myRanker3_ks = myRanker3_ks;
		this.r=r;
		this.precision = precision;
	}

	@Override
	public void evaluate() {
		System.out.println("Start: MyRanker_KSPlus.evaluate():: Time: "+new Date());
		preProcessing();

				
		double[] thetaMap = myRanker3_ks.nodeValues;
		
		
		for (int v=0; v<graph.getVertexCount(); v++) {
			double value = 0.0;
			for(int w: graph.getNeighbors(v)) {
			 value += thetaMap[w];
		}
			nodeValueMap[v] = (int)(value*precision);
		}
		//postProcessingReverse();
		postProcessing();
		
		System.out.println("End: MyRanker_KSPlus.evaluate():: Time: "+new Date());
	  }
	public double getNodeValueDouble(String v) {
		return (double)nodeValueMap[graph.getIndex(v)]/precision;
	}

	public double getNodeValueDouble(int v) {
		return (double)nodeValueMap[v]/precision;
	}
	
	
	}