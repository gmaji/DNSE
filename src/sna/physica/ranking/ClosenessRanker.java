package sna.physica.ranking;

import edu.uci.ics.jung.algorithms.scoring.ClosenessCentrality;
import sna.physica.graph.IndexedGraph;

public class ClosenessRanker extends Ranker {

	public static final int PRECISION = 1000000000;
	
	public ClosenessRanker(IndexedGraph graph) {
		super(graph);
	}

	@Override
	public void evaluate() {
		
		preProcessing();
		
		ClosenessCentrality<String, Integer> closeness = 
				new ClosenessCentrality<String, Integer>(graph.getGraph());
		
		for (String v: graph.getVertices()) {
			int value = (int)(closeness.getVertexScore(v)*PRECISION);
			nodeValueMap[graph.getIndex(v)] = value;
		}
		
		postProcessing();
	}
	
	public double getNodeValueDouble(String v) {
		return (double)nodeValueMap[graph.getIndex(v)]/PRECISION;
	}

}
