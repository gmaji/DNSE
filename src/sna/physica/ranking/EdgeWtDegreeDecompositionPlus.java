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

import java.util.Date;
import sna.physica.graph.IndexedGraph;

public class EdgeWtDegreeDecompositionPlus extends Ranker {
//int precision;
public EdgeWtDegreeDecomposition mEWDegree2;


	public EdgeWtDegreeDecompositionPlus(IndexedGraph graph,EdgeWtDegreeDecomposition mEWDegree2 ) {
		super(graph);
		this.mEWDegree2 = mEWDegree2;
	}
	
	@Override
	public void evaluate() {
		
		System.out.println("Start: MyEdgeWtDegreeDecompositionPlus.evaluate():: Time: "+new Date());
		preProcessing();

				
		long[] thetaMap = mEWDegree2.getNodeValueMap();
		
		
		for (int v=0; v<graph.getVertexCount(); v++) {
			double value = 0.0;
			for(int w: graph.getNeighbors(v)) {
			 value += thetaMap[w];
		}
			nodeValueMap[v] = (int)value;
		}
		//postProcessingReverse();
		postProcessing();
		
		System.out.println("End: MyEdgeWtDegreeDecompositionPlus.evaluate():: Time: "+new Date());
	}
	
	public double getNodeValueDouble(String v) {
		return (double)nodeValueMap[graph.getIndex(v)]/mEWDegree2.precision;
	}
}
