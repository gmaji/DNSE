package sna.physica.ranking;

import sna.physica.graph.IndexedGraph;
import edu.uci.ics.jung.algorithms.scoring.BetweennessCentrality;


public class BetweennessRanker extends Ranker {

	public static final int PRECISION = 100;
	
	public BetweennessRanker(IndexedGraph graph) {
		super(graph);
	}

	@Override
	public void evaluate() {
		
		preProcessing();
		
		BetweennessCentrality<String, Integer> betweenness = 
				new BetweennessCentrality<String, Integer>(graph.getGraph());
		
		for (String v: graph.getVertices()) {
			int value = (int)(betweenness.getVertexScore(v)*PRECISION);
			nodeValueMap[graph.getIndex(v)] = value;
		}
		
		postProcessing();
	}
	
	public double getNodeValueDouble(String v) {
		return (double)nodeValueMap[graph.getIndex(v)]/PRECISION;
	}

}
