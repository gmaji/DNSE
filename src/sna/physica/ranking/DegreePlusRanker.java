package sna.physica.ranking;

import java.util.Date;

import sna.physica.graph.IndexedGraph;
/**
 * It is also known as Neighbor's degree method as it's measure is the sum of neighbors node-degree
 * Pei, Sen, Lev Muchnik, Jos� S. Andrade Jr, Zhiming Zheng, and Hern�n A. Makse. "Searching for superspreaders of 
 * information in real-world social media." Scientific reports 4 (2014): 5547.
 * 
 * A number of predictors have been suggested to detect the most influential spreaders of information in
online social media across various domains such as Twitter or Facebook. In particular, degree, PageRank,
k-core and other centralities have been adopted to rank the spreading capability of users in information
dissemination media. So far, validation of the proposed predictors has been done by simulating the
spreading dynamics rather than following real information flow in social networks. Consequently, only
model-dependent contradictory results have been achieved so far for the best predictor. Here, we address
this issue directly. We search for influential spreaders by following the real spreading dynamics in a wide
range of networks. We find that the widely-used degree and PageRank fail in ranking users� influence. We
find that the best spreaders are consistently located in the k-core across dissimilar social platforms such as
Twitter, Facebook, Livejournal and scientific publishing in the American Physical Society. Furthermore,
when the complete global network structure is unavailable, we find that the sum of the nearest neighbors�
degree is a reliable local proxy for user�s influence. Our analysis provides practical instructions for optimal
design of strategies for ��viral�� information dissemination in relevant applications.
 * 
 * @author Raju
 *
 */
public class DegreePlusRanker extends Ranker {

	public DegreePlusRanker(IndexedGraph graph) {
		super(graph);
	}

	@Override
	public void evaluate() {
		Date d1 = new Date();
		System.out.println("Entry: DegreePlusRanker.evaluate():Time:"+d1.getTime());
		preProcessing();
        
		for (int v=0; v<graph.getVertexCount(); v++) {
			int degree_sum = 0;
			for(int k:graph.getNeighbors(v)){
				degree_sum+= graph.degree(k);
			}
			//int value = graph.degree(v);
			nodeValueMap[v] = degree_sum;
		}
		
		postProcessing();
		Date d2 = new Date();
		System.out.println("Exit: DegreePlusRanker.evaluate(): Time:"+d2.getTime()+" Time Taken in seconds = "+(d2.getTime()-d1.getTime())/1000);
	}

}
