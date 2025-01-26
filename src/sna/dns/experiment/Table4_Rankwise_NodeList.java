package sna.dns.experiment;
/**
 * Code to print list of nodes for each rank
 */
import java.io.File;
import java.io.PrintStream;
import java.util.Set;
import java.util.TreeSet;

import sna.physica.graph.IndexedGraph;
import sna.physica.ranking.DegreeRanker;
import sna.physica.ranking.DirectionalNodeStrengthEntropy;
import sna.physica.ranking.EpidemicRanker;
import sna.physica.ranking.KShellRanker;
import sna.physica.ranking.LSS_Ullah_Ranker;
import sna.physica.ranking.Ranker;

public class Table4_Rankwise_NodeList {
	
	public static void main(String[] args) throws Exception {
		
		long startTime = System.currentTimeMillis();

		String[][] networks = {
//		                {"Network",     "dataset_file",     "beta_th",   "mu",         "beta_exp"},
//						{"Schematic",	"schematic.txt",	"0.30",      "0.40",        "0.31"},
//						{"NetScience",	"netscience.txt",	"0.144",     "0.4",	   		"0.15"}, //correct nodes:1589, E:2742
//						{"C.elegans",	"celegans.txt",		"0.05",	     "0.40",   		"0.06"},
//						{"E-mail",		"email.txt",		"0.10",		 "0.40",		"0.15"},
//						{"Blogs",		"blogs.txt",		"0.10",		 "0.40",		"0.15"},
//						{"AS",			"as.txt",			"0.10",		 "0.40",		"0.15"},
//						{"P2P",			"p2p.txt",			"0.10",		 "0.40",		"0.15"},
//						{"Advogato",	"advogato.txt",		"0.012",     "0.9",         "0.05"}, //V:6541, E:51127
//						{"ODLIS",		"odlis.txt",		"0.014",     "0.1",         "0.02"}, //V:2909, E:18246
//						{"CA-AstroPh",	"astroph.txt",		"0.015",	 "0.01",		"0.02"}, // V: 18772, E:198110 ; need to correct edges.
//						{"CA-CondMat",	"condmat.txt",		"0.06",		 "0.4",         "0.07"},
//						{"Enron",		"enron.txt",		"0.007",	 "0.01",        "0.01"},// V:33696, E: 180811; need to check with dataset
//						{"Amazon",		"amazon.txt", 		"0.10",		 "0.4",		    "0.15"},
//						{"Zachary",		"zachary.txt", 		"0.02",		 "0.4",         "0.05"},
//						{"Powergrid",	"powergrid.txt", 	"0.258",	 "0.4",		    "0.3"}, //correct data V:
//					    {"Hamster",	    "hamster.txt", 	    "0.022",	 "0.1",		    "0.05"}, // correct data V:1858 E: 12534
//					    {"US-Airport",  "usairport.txt", 	"0.15",		 "0.4",         "0.16"},
//						{"US-Airline-97","usair97.txt", 	"0.023",	 "0.4",         "0.05"}, //correct data; V:332  E:2126
//						{"Jazz",	    "jazz.txt", 	    "0.026",	 "0.4",		    "0.05"}, //correct data; V:198 E:2742
//					    {"Dolphins",	"dolphins.txt",		"0.03",		 "0.4",		    "0.05"},
//					    {"DBLP-CITE",	"dblp-cite.txt",	"0.023",	 "0.1",		    "0.05"}, //correct data; V:12591 E:49743
//					    {"EURO-Road",	"euroroad.txt",	    "0.05",		 "0.4",         "0.05"},
//					    {"High-School",	"highschool.txt",	"0.05",		 "0.4",         "0.05"},       
//					    {"Foodweb",	"maayan-foodweb.txt",	"0.05",		 "0.4",         "0.05"},
//					    {"Macaques",	"macaques.txt",	    "0.05",		 "0.4",         "0.05"},        
//					    {"PGP",	        "pgp.txt",	        "0.10",		 "0.4",		    "0.11"},
//						{"Comsnet-Toy-1","comsnet_toy_connected.txt",	 "0.23",		 "0.4",		    "0.25"},
//						{"Comsnet-Toy-2","comsnet_toy_disconnected.txt", "0.23",		 "0.4",		    "0.25"},
//						{"Toy-kshi",	"toy_kshi.txt",		"0.244",	 "0.4",		     "0.35"},
						{"Toy_DNS",	     "toy_dns.txt",	    "0.276",	 "0.4",		     "0.30"},
					    
		};
		
		
		//PrintStream out = new PrintStream(new File("physica/table.01.txt"));

		for (int i=0; i<networks.length; i++) {
			String outfilename = "results/"+networks[i][0]+".table.01.x.txt";
			PrintStream out = new PrintStream(new File(outfilename));
			String filename = "dataset/undirected/" + networks[i][1];
			
			IndexedGraph graph = IndexedGraph.readGraph(filename);
			double _beta_exp = Double.parseDouble(networks[i][4]);
			int iteration = 10000;
		
			EpidemicRanker epidemicRanker = new EpidemicRanker(graph, _beta_exp, iteration);
			epidemicRanker.evaluate();
			
			DegreeRanker degreeRanker = new DegreeRanker(graph);
			degreeRanker.evaluate();
	
			KShellRanker kshellRanker = new KShellRanker(graph);
			kshellRanker.evaluate();
		/*
			int r =3;
			
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
			double delta=0.5;
			
	*/
			LSS_Ullah_Ranker lss = new LSS_Ullah_Ranker(graph);
			lss.evaluate();
			
			DirectionalNodeStrengthEntropy dns2Ranker = new DirectionalNodeStrengthEntropy(graph, 1000);
			dns2Ranker.evaluate();
	
			
			
			out.printf("rank \t");
			out.printf("SIR \t");
			out.printf("k \t");
			out.printf("ks \t");
		//	out.printf("ks+ \t");
		//    out.printf("ksW \t");
		//	out.printf("ksW_GM \t");
		//	out.printf("MDD \t");
		//	out.printf("Theta \t");
			out.printf("LSS \t");
			out.printf("DNSE \t"); 
			
			
			out.println();
			for (int rank=1; rank<=graph.getVertexCount(); rank++) {
				out.printf("%d  \t", rank);
				String v = epidemicRanker.getNodeByRankOrder(rank);
				out.printf("%s\t", v);
				printNodes(rank, out, degreeRanker);
				printNodes(rank, out, kshellRanker);
			//	printNodes(rank, out, kshellplusRanker);
			//	printNodes(rank, out, kshellW);
			//	printNodes(rank, out, ksW_GM);
			//	printNodes(rank, out, mddRanker);
			//	printNodes(rank, out, improvedRanker);
				printNodes(rank, out, lss);
				printNodes(rank, out, dns2Ranker);
				
				out.println();
			}
			//out.close();

			out.println();
			out.close();
		}
	//	out.close();
		
		long endTime = System.currentTimeMillis();
		System.out.printf("\n Elapsed Time : %d secs", (endTime-startTime)/1000);
	}
	
	public static void printNodes(int rank, PrintStream out, Ranker ranker) {
		Set<String> nodes = ranker.getRankNodeSet(rank);
		if (nodes == null) {
			out.printf("-");
		}
		else {
			int last = nodes.size();
			int count = 0;
			for (String v: new TreeSet<String>(nodes)) {
				out.printf("%s", v); count++;
				if (count != last) out.printf(","); 
			}
		}
		out.printf("\t");
	}

}
