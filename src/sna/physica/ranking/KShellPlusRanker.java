package sna.physica.ranking;
/**
 * This Extended K-shell ranker is also proposed in the same paper along with coreplus ranker.
 * Bae, J., & Kim, S. (2014). Identifying and ranking influential spreaders in complex networks by neighborhood coreness. 
 * Physica A: Statistical Mechanics and its Applications, 395, 549-559.
 */
import sna.physica.graph.IndexedGraph;

public class KShellPlusRanker extends Ranker {

	public KShellPlusRanker(IndexedGraph graph) {
		super(graph);
	}

	@Override
	public void evaluate() {

		int maxDegree = 0;
		for (int v=0; v<graph.getVertexCount(); v++) {
			int degree = graph.degree(v);
			if (maxDegree < degree) {
				maxDegree = degree;
			}
		}
		
		KShellRanker kshellRanker = new KShellRanker(graph);
		kshellRanker.evaluate();

		preProcessing();

		for (int vi=0; vi<graph.getVertexCount(); vi++) {
			int k = graph.degree(vi);
			int ks = (int)kshellRanker.getNodeValue(vi);
			
			nodeValueMap[vi] = (ks*(maxDegree+1)+k);
		}
		
		postProcessing();
	}

}
