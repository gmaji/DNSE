package sna.physica.ranking;
/***
 * My improvement over the below paper for Weighted networks. 
 * But this is used in Hotspots in CDR network analysis paper. It is shown as an improvement over j5.
 * j5: Instead of potential edge weight = k_i +k_j; I use real edge weights. Rest part remains the same.
 * j5: Maji, Giridhar, and Soumya Sen. "Ranking influential nodes in complex network using edge weight degree based shell decomposition." 
 * Journal of Computational Science 74 (2023): 102179.
 * 
 * Publication: 
 * Maji, Giridhar, Sharmistha Mandal, and Soumya Sen. 
 * "Identification of city hotspots by analyzing telecom call detail records using complex network modeling." 
 * Expert Systems with Applications 215 (2023): 119298.
 */
import java.util.Date;
import sna.physica.graph.IndexedGraph;

public class EdgeWtDegreeDecomposition_HotspotPlus extends Ranker {
//int precision;
public EdgeWtDegreeDecomposition_Hotspot mEWDegree;


	public EdgeWtDegreeDecomposition_HotspotPlus(IndexedGraph graph,EdgeWtDegreeDecomposition_Hotspot mEWDegree ) {
		super(graph);
		this.mEWDegree = mEWDegree;
	}
	
	@Override
	public void evaluate() {
		
		System.out.println("Start: MyEdgeWtDegreeDecomposition_HotspotPlus.evaluate():: Time: "+new Date());
		preProcessing();

				
		long[] thetaMap = mEWDegree.getNodeValueMap();
		
		
		for (int v=0; v<graph.getVertexCount(); v++) {
			double value = 0.0;
			for(int w: graph.getNeighbors(v)) {
			 value += thetaMap[w];
		}
			nodeValueMap[v] = (int)value;
		}
		//postProcessingReverse();
		postProcessing();
		
		System.out.println("End: MyEdgeWtDegreeDecomposition_HotspotPlus.evaluate():: Time: "+new Date());
	}
	
	public double getNodeValueDouble(String v) {
		return (double)nodeValueMap[graph.getIndex(v)]/mEWDegree.precision;
	}
}
