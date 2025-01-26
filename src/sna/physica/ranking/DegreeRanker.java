package sna.physica.ranking;

import sna.physica.graph.IndexedGraph;

public class DegreeRanker extends Ranker {

	public DegreeRanker(IndexedGraph graph) {
		super(graph);
		
	}

	@Override
	public void evaluate() {
		
		preProcessing();

		for (int v=0; v<graph.getVertexCount(); v++) {
			int value = graph.degree(v);
			nodeValueMap[v] = value;
		}
		
		postProcessing();
	}

}
