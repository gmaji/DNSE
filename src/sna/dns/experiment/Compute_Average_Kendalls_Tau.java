package sna.dns.experiment;

import java.io.File;
import java.io.PrintStream;

import sna.physica.graph.IndexedGraph;
import sna.physica.ranking.EpidemicRanker;
import sna.physica.ranking.LSS_Ullah_Ranker;
import sna.physica.ranking.RankComparator;

public class Compute_Average_Kendalls_Tau {
/***
 * It gives the Average Tau value when beta varies from beta_th to beta_th + n*delta with an increase of delta every time.
 * It compares avg tau for all selected datasets. (data source : http://www-personal.umich.edu/~mejn/netdata/)
 * @param args
 * @throws Exception
 */
	public static void main(String[] args) throws Exception {
		
		long startTime = System.currentTimeMillis();
String[][] networks = {
//   {"Network",     "dataset_file",    "beta_th",   "mu",         "beta_exp"},
//	 {"Brightkite",	"brightkite.txt",   "0.016",     "0.40",        "0.31"}, // V: 58228 E:214078
//	 {"Douban",	    "douban.txt",	    "0.027",     "0.40",        "0.31"}, // V:154908  E:183831 (undirected) E:367662(directed)
//	 {"NetScience",	"netscience.txt",	"0.144",     "0.4",	   	    "0.15"}, //correct nodes:1589, E:2742; beta no- change
//	 {"C.elegans",	"celegans.txt",		"0.038",	 "0.40",   	    "0.06"}, // V:306 E:2148 after removing duplicate edges and undirected V:297 E: 2148
//	 {"E-mail",		"email.txt",		"0.007",	 "0.40",		"0.15"}, //Nodes: 36692 Edges: 367662
//	 {"Blogs",		"pollblog.txt",		"0.012",	 "0.40",		"0.15"},//V: 1490 E:16718
//	 {"Advogato",	"advogato.txt",		"0.012",     "0.9",         "0.05"}, //V:6541, E:51127, beta no change
//	 {"CA-AstroPh",	"ca_astroph.txt",	"0.015",	 "0.01",		"0.02"}, // Undirected, Un-weighted; V: 18772, E:198050 ; correct beta no change
//	 {"CA-CondMat2005",	"condmat05.txt","0.037",	 "0.4",         "0.07"},// V: 40421 E:175692
//	 {"CA-CondMat",	"condmat.txt",	    "0.077",	 "0.4",         "0.07"},// V: 16726 E:47594 undirected
//	 {"Amazon MDS",	"amazon_mds.txt", 	"0.087",	 "0.4",		    "0.15"}, //  Undirected, Un-weighted, No-loop ; V: 925872 E:334863
//	 {"Zachary",	"zachary.txt", 		"0.129",	 "0.4",         "0.05"},//  Undirected, Un-weighted, No-loop ; V:34 E:78
//	 {"Hamster", "hamster.txt","0.022",	 "0.1",		 "0.05",        "0.15"},// V:1858 E: 12534 beta no change
//	 {"US-Airport",  "usairport.txt", 	"0.009",	 "0.4",         "0.16"}, // not undirected; V: 1574; E: 17215
//	 {"US-Airline-97","usairline97.txt","0.023",	 "0.4",         "0.05"}, //correct data; V:332  E:2126; beta-no change
//	 {"Jazz",	    "jazz.txt", 	    "0.026",	 "0.4",		    "0.05"}, // Undirected, Un-weighted, correct data; V:198 E:2742
//	 {"Dolphins",	"dolphins.txt",		"0.147",	 "0.4",		    "0.05"}, // Undirected, Un-weighted; V:62  E:159
//	 {"DBLP-CITE",	"dblp_cite.txt",	"0.023",	 "0.1",		    "0.05"}, //correct data; V:12591 E:49743
//	 {"DBLP-CoAuthor","dblp_ca.txt",    "0.046",	 "0.1",		    "0.05"}, //  Undirected, Un-weighted, V:317080 E:1049866; beta_th
//	 {"EURO-Road",	"euroroad.txt",	    "0.333",	 "0.4",         "0.05"}, // V: 1174; E:1417
//	 {"High-School","highschool.txt",   "0.107",	 "0.4",         "0.05"},    // V: 70; E:274   
//	 {"Foodweb",	"foodweb.txt",	    "0.023",	 "0.4",         "0.05"}, // V: 183; E: 2434
//	 {"Macaques",	"macaques.txt",	    "0.026",	 "0.4",         "0.05"}, // V: 65; E:1169      
//	 {"Football",	"football.txt",	    "0.093",	 "0.4",		    "0.35"},// Undirected un weighted V: 115;  E:613
//	 {"Zebra",	    "zebra.txt",	    "0.091",	 "0.4",		    "0.35"},// V:27;  E:111
//	 {"Odlis",	    "odlis.txt",	    "0.014",	 "0.9",		    "0.35"},// V:2909;  E:18241; beta no change
//   {"Powergrid",	"powergrid.txt", 	"0.258",	 "0.4",		    "0.30"}, // Undirected, Un-weighted, correct data V:4941  E:6594
//	 {"PGP",	    "pgp.txt",	        "0.053",	 "0.4",		    "0.11"},// V: 10680  E: 24316
	 {"Toy_DNS",	"toy_dns.txt",	    "0.276",	 "0.4",		    "0.30"},  
		};


		for (int i=0; i < networks.length; i++) {

			System.err.println(networks[i][0]);
			String filename = "dataset/undirected/" + networks[i][1];
			IndexedGraph graph = IndexedGraph.readGraph(filename);
			double beta_th = Double.parseDouble(networks[i][2]);
			
			PrintStream out = new PrintStream(new File("results/dns/tau.beta.lss."+networks[i][1]));
			/*		
			DegreeRanker degreeRanker = new DegreeRanker(graph);
			degreeRanker.evaluate();
			
			DegreePlusRanker degreePlusRanker = new DegreePlusRanker(graph);
			degreePlusRanker.evaluate();
	
			
			KShellRanker kshellRanker = new KShellRanker(graph);
			kshellRanker.evaluate();
			
			KShellPlusRanker kshellplusRanker = new KShellPlusRanker(graph);
			kshellplusRanker.evaluate();
			
			PotentialEdgeWtKShellRanker kshellW = new PotentialEdgeWtKShellRanker(graph);
			kshellW.evaluate();
			
			double lambda1 = 0;
		    WtKsDegreeNeighborhood_Giridhar ksW_GM = new WtKsDegreeNeighborhood_Giridhar(graph,lambda1, 1000);
			ksW_GM.evaluate();
			
			
			double lambda = 0.7;
			MDDRanker mddRanker = new MDDRanker(graph, lambda);
			mddRanker.evaluate();

			ImprovedRanker improvedRanker = new ImprovedRanker(graph);
			improvedRanker.evaluate();
		
			BetweennessRanker betweennessRanker = new BetweennessRanker(graph);
			betweennessRanker.evaluate();
	
		    ClosenessRanker closenessRanker = new ClosenessRanker(graph);
			closenessRanker.evaluate();
		  
			
			double alpha=0.15; // setting alpha=0.15 is same as setting damping factor = 0.85 = (1-alpha)
			PageRankRanker pageRanker = new PageRankRanker(graph, alpha);
			pageRanker.evaluate();
			
		   // PageRank with alpha=0.0 yield same score as EigenVectorRanker
			EigenVectorRanker  evRanker = new EigenVectorRanker(graph);
			evRanker.evaluate();
			
			
		//	DirectionalNodeStrengthEntropy dns2Ranker = new DirectionalNodeStrengthEntropy(graph, 10000);
		//	dns2Ranker.evaluate();
			
			*/
			LSS_Ullah_Ranker lss = new LSS_Ullah_Ranker(graph);
			lss.evaluate();
			
		//out.printf("beta \t  K     \t    KS   \t KS+   \t KSW  \t KSW_GM  \t MDD \t Theta  \t LSS \t DNSE   \n");
		out.printf("beta  \t  LSS   \n");
			for (int t=1; t<=10; t++) {
				
				double beta = beta_th * (1+ (0.1*t));
				
				int iteration = 1000;
				if (graph.getEdgeCount() > 10000) {
					iteration = 200;
				}
				//System.err.println("beta = "+beta);
				EpidemicRanker epidemicRanker = new EpidemicRanker(graph, beta, iteration);
				epidemicRanker.evaluate();

				out.printf("%1.3f\t", beta);
			//	out.printf("%1.6f\t ", RankComparator.getTau(epidemicRanker, degreeRanker));
			//	out.printf("%1.6f\t ", RankComparator.getTau(epidemicRanker, degreePlusRanker));
				//out.printf("%1.6f \t", RankComparator.getTau(epidemicRanker, kshellRanker));
				//out.printf("%1.6f\t ", RankComparator.getTau(epidemicRanker, kshellplusRanker));
				//out.printf("%1.6f \t", RankComparator.getTau(epidemicRanker, kshellW));
				//out.printf("%1.6f\t ", RankComparator.getTau(epidemicRanker, ksW_GM));
				//out.printf("%1.6f\t ", RankComparator.getTau(epidemicRanker, mddRanker));
				//out.printf("%1.6f\t ", RankComparator.getTau(epidemicRanker, improvedRanker));
				
			//	out.printf("%1.6f \t", RankComparator.getTau(epidemicRanker, betweennessRanker));
			//	out.printf("%1.6f \t", RankComparator.getTau(epidemicRanker, closenessRanker));
			//	out.printf("%1.6f \t", RankComparator.getTau(epidemicRanker, pageRanker));
			//	out.printf("%1.6f \t", RankComparator.getTau(epidemicRanker, evRanker));
				out.printf("%1.6f \t", RankComparator.getTau(epidemicRanker, lss));
			//	out.printf("%1.6f \t", RankComparator.getTau(epidemicRanker, lgc));
			//	out.printf("%1.6f \t", RankComparator.getTau(epidemicRanker, dns2Ranker));
				out.println();
			}
			
			out.close();
		}
		
		long endTime = System.currentTimeMillis();
		System.out.printf("Elapsed Time : %d secs", (endTime-startTime)/1000);

	}

}
