package sna.physica.ranking;
import edu.uci.ics.jung.algorithms.scoring.EigenvectorCentrality;
/**
 * Understand Page Rank from here:
 * https://memgraph.github.io/networkx-guide/algorithms/centrality-algorithms/pagerank/
 * 
 * Calculates eigenvector centrality for each vertex in the graph.
 * The 'eigenvector centrality' for a vertex is defined as the fraction of
 * time that a random walk(er) will spend at that vertex over an infinite
 * time horizon.
 * Assumes that the graph is strongly connected.
 */
import sna.physica.graph.IndexedGraph;


public class EigenVectorRanker extends Ranker {

	public static final int PRECISION = 100000;
	
	public EigenVectorRanker(IndexedGraph graph) {
		super(graph);
	}

	@Override
	public void evaluate() {
		
		preProcessing();
		//alpha=0.1;
		EigenvectorCentrality<String, Integer> eigenVectoranker = new EigenvectorCentrality<String, Integer>(graph.getGraph());

		eigenVectoranker.evaluate();
		
		for (String v: graph.getVertices()) {
			int value = (int)(eigenVectoranker.getVertexScore(v)*PRECISION);
			nodeValueMap[graph.getIndex(v)] = value;
		}
		
		postProcessing();
	}
	
	public double getNodeValueDouble(String v) {
		return (double)nodeValueMap[graph.getIndex(v)]/PRECISION;
	}

}
