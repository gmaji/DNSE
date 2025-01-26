package sna.physica.ranking;
/**
 * Understand Page Rank from here:
 * https://memgraph.github.io/networkx-guide/algorithms/centrality-algorithms/pagerank/

   * Creates an instance for the specified graph, edge weights, and random jump probability.
  * graph the input graph
  * edge_weight the edge weights (transition probabilities)
  * alpha the probability of taking a random jump to an arbitrary vertex. It is complementary with nextworkx page rank damping factor. 
  * In networkx alpha is the probability of clicking the same link whereas here in Jung pagerank alpha is the probalility of moving away to some
  * other node. So, default value of alpha in JUNG pagerank is 0.15 where as it would be 0.85 in networkx to obtaibn same node PR scores.
     */
import sna.physica.graph.IndexedGraph;
import edu.uci.ics.jung.algorithms.scoring.PageRank;


public class PageRankRanker extends Ranker {

	public static final int PRECISION = 100000;
	private int maxIterations;
	private double tolerance;
	private double alpha;
	public PageRankRanker(IndexedGraph graph) {
		super(graph);
		this.alpha = 0.15; // that is commonly used value. It is same as setting alpha-0.85 in networkx.pagerank
		this.maxIterations =100;
		this.tolerance = 0.0001;
	}
	public PageRankRanker(IndexedGraph graph, double alpha) {
		super(graph);
		this.alpha= alpha;
		this.maxIterations = 100;
		this.tolerance = 0.000001;
	}

	@Override
	public void evaluate() {
		
		preProcessing();
		//alpha=0.1;
		PageRank<String, Integer> pageranker = new PageRank<String, Integer>(graph.getGraph(), alpha);
		pageranker.setTolerance(this.tolerance) ;
		pageranker.setMaxIterations(this.maxIterations);

		pageranker.evaluate();
		
		for (String v: graph.getVertices()) {
			int value = (int)(pageranker.getVertexScore(v)*PRECISION);
			nodeValueMap[graph.getIndex(v)] = value;
		}
		
		postProcessing();
	}
	
	public double getNodeValueDouble(String v) {
		return (double)nodeValueMap[graph.getIndex(v)]/PRECISION;
	}

}
