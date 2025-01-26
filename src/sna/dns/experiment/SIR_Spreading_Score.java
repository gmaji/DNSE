package sna.dns.experiment;

import java.io.File;
import java.io.PrintStream;

import sna.physica.graph.IndexedGraph;
import sna.physica.ranking.EpidemicRanker;

public class SIR_Spreading_Score {
	
	public static void main(String[] args) throws Exception {
		
     long startTime = System.currentTimeMillis();

String[][] networks = {
//		{"Network",     "dataset_file",     "beta_th",   "mu",         "beta_exp"},
//		{"Schematic",	"schematic.txt",	"0.30",      "0.40",        "0.31"},
//		{"NetScience",	"netscience.txt",	"0.144",     "0.4",	   		"0.15"}, //correct nodes:1589, E:2742
//		{"C.elegans",	"celegans.txt",		"0.05",	     "0.40",   		"0.06"},
//		{"E-mail",		"email.txt",		"0.10",		 "0.40",		"0.15"},
//		{"Blogs",		"blogs.txt",		"0.10",		 "0.40",		"0.15"},
//		{"AS",			"as.txt",			"0.10",		 "0.40",		"0.15"},
//		{"P2P",			"p2p.txt",			"0.10",		 "0.40",		"0.15"},
//		{"Advogato",	"advogato.txt",		"0.012",     "0.9",         "0.05"}, //V:6541, E:51127
//		{"ODLIS",		"odlis.txt",		"0.014",     "0.1",         "0.02"}, //V:2909, E:18246
//		{"CA-AstroPh",	"astroph.txt",		"0.015",	 "0.01",		"0.02"}, // V: 18772, E:198110 ; need to correct edges.
//		{"CA-CondMat",	"condmat.txt",		"0.06",		 "0.4",         "0.07"},
//		{"Enron",		"enron.txt",		"0.007",	 "0.01",        "0.01"},// V:33696, E: 180811; need to check with dataset
//		{"Amazon",		"amazon.txt", 		"0.10",		 "0.4",		    "0.15"},
//		{"Zachary",		"zachary.txt", 		"0.02",		 "0.4",         "0.05"},
//		{"Powergrid",	"powergrid.txt", 	"0.258",	 "0.4",		    "0.3"}, //correct data V:
//		{"Hamster",	    "hamster.txt", 	    "0.022",	 "0.1",		    "0.05"}, // correct data V:1858 E: 12534
//		{"US-Airport",  "usairport.txt", 	"0.15",		 "0.4",         "0.16"},
//		{"US-Airline-97","usair97.txt", 	"0.023",	 "0.4",         "0.05"}, //correct data; V:332  E:2126
//		{"Jazz",	    "jazz.txt", 	    "0.026",	 "0.4",		    "0.05"}, //correct data; V:198 E:2742
//		{"Dolphins",	"dolphins.txt",		"0.03",		 "0.4",		    "0.05"},
//		{"DBLP-CITE",	"dblp-cite.txt",	"0.023",	 "0.1",		    "0.05"}, //correct data; V:12591 E:49743
//		{"EURO-Road",	"euroroad.txt",	    "0.05",		 "0.4",         "0.05"},
//		{"High-School",	"highschool.txt",	"0.05",		 "0.4",         "0.05"},       
//		{"Foodweb",	"maayan-foodweb.txt",	"0.05",		 "0.4",         "0.05"},
//		{"Macaques",	"macaques.txt",	    "0.05",		 "0.4",         "0.05"},        
//		{"PGP",	        "pgp.txt",	        "0.10",		 "0.4",		    "0.11"},
//		{"Test",	    "test.txt",	        "0.31",		 "0.4",		    "0.35"},
		{"Toy_DNS",	    "toy_dns.txt",	    "0.276",	 "0.4",		    "0.30"},   
};
		//PrintStream out = new PrintStream(new File("physica/fig.1.sir.txt"));

		for (int i=0; i<networks.length; i++) {
			String outfilename = "results/"+networks[i][0]+".fig.1.sir.txt";
			PrintStream out = new PrintStream(new File(outfilename));
			String filename = "dataset//undirected/" + networks[i][1];
			IndexedGraph graph = IndexedGraph.readGraph(filename);

			double beta = Double.parseDouble(networks[i][2]);
			int iteration = 10000;
			EpidemicRanker epidemicRanker = new EpidemicRanker(graph, beta, iteration);
			epidemicRanker.evaluate();

			out.printf("rank\t");
			out.printf("Node\t");
			out.printf("sigma\t");
			out.println();
			for (int order=1; order<=graph.getVertexCount(); order++) {
				String v = epidemicRanker.getNodeByRankOrder(order);
				System.err.println(v);
				out.printf("%d\t\t", order);
				out.printf("%s\t\t", v);
				out.printf("%1.2f\t", (double)epidemicRanker.getNodeValue(v)/iteration);
				out.println();
			}
			out.close();

			out.println();
			out.close();
		}
		//out.close();
		
		long endTime = System.currentTimeMillis();
		System.out.printf("Elapsed Time : %d secs", (endTime-startTime)/1000);
	}
	
}
