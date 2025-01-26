package sna.physica.ranking;

import edu.uci.ics.jung.graph.Graph;
import it.unimi.dsi.law.stat.KendallTau;

public class RankComparator {
	
	public static double getTau(Ranker ranker1, Ranker ranker2) {
//		double[] v1 = ranker1.getRankArray();
//		double[] v2 = ranker2.getRankArray();

		Graph<String, Integer> graph = ranker1.getGraph();
		
		double[] v1 = new double[graph.getVertexCount()];
		double[] v2 = new double[graph.getVertexCount()];
		int index = 0;
		for (String v: graph.getVertices()) {
			v1[index] = ranker1.getNodeRank(v);
			v2[index] = ranker2.getNodeRank(v);
//			System.out.println(v1[index]);
			index++;
		}
		
		return KendallTau.INSTANCE.compute(v1, v2);
	}
	
}
