package sna.dns.experiment;

import java.io.File;
import java.io.PrintStream;

import sna.physica.graph.IndexedGraph;
import sna.physica.ranking.DegreeRanker;
import sna.physica.ranking.EigenVectorRanker;
import sna.physica.ranking.KShellPlusRanker;
import sna.physica.ranking.KShellRanker;
import sna.physica.ranking.LGC_Ullah_Ranker;
import sna.physica.ranking.LSS_Ullah_Ranker;
import sna.physica.ranking.MDDRanker;
import sna.physica.ranking.PageRankRanker;

public class Compute_ccdf {
	
	public static void main(String[] args) throws Exception {
		
		long startTime = System.currentTimeMillis();

		String[][] networks = {
			//   {"Network", 	"dataset_file",    "beta_th",   "mu",         "beta_exp" },
//				 {"Brightkite",	"brightkite.txt",	"0.016",    "0.40",         "0.31"}, // V: 58228 E:214078
//				 {"Douban",	    "douban.txt",	    "0.027",    "0.40",         "0.31"}, // V:154908  E:183831 (undirected) E:367662(directed)
//				 {"NetScience",	"netscience.txt",	"0.144",    "0.4",	   	    "0.15"}, //correct nodes:1589, E:2742; beta no- change
//				 {"C.elegans",	"celegans.txt",		"0.038",	"0.40",   	    "0.06"}, // V:306 E:2148 after removing duplicate edges and undirected V:297 E: 2148
//				 {"E-mail",		"email.txt",		"0.007",	"0.40",		    "0.15"}, //Nodes: 36692 Edges: 367662
//				 {"Pol-Blogs",	"pollblog.txt",		"0.012",	 "0.40",		"0.15"},//V: 1490 E:16718
//				 {"Advogato",	"advogato.txt",		"0.012",     "0.9",         "0.05"}, //V:6541, E:51127, beta no change
//				 {"CA-AstroPh",	"ca_astroph.txt",	"0.015",	 "0.01",		"0.02"}, // Undirected, Un-weighted; V: 18772, E:198050 ; correct beta no change
//				 {"CA-CondMat2005",	"condmat05.txt","0.037",	 "0.4",         "0.07"},// V: 40421 E:175692
//				 {"CA-CondMat",	"condmat.txt",	    "0.077",	 "0.4",         "0.07"},// V: 16726 E:47594 undirected
//				 {"Amazon MDS",	"amazon_mds.txt", 	"0.087",	 "0.4",		    "0.15"}, //  Undirected, Un-weighted, No-loop ; V: 925872 E:334863
//				 {"Zachary",	"zachary.txt", 		"0.129",	 "0.4",         "0.05"},//  Undirected, Un-weighted, No-loop ; V:34 E:78
//				 {"Powergrid",	"powergrid.txt", 	"0.258",	 "0.4",		    "0.30"}, // Undirected, Un-weighted, V:4941  E:6594
//				 {"Hamster", 	"hamster.txt","0.022",	 "0.1",		    "0.05"}, // V:1858 E: 12534 beta no change
//				 {"US-Airport",  "usairport.txt", 	"0.009",	 "0.4",         "0.16"}, // not undirected; V: 1574; E: 17215
//				 {"US-Airline-97","usairline97.txt","0.023",	 "0.4",         "0.05"}, //correct data; V:332  E:2126; beta-no change
//				 {"Jazz",	    "jazz.txt", 	    "0.026",	 "0.4",		    "0.05"}, // Undirected, Un-weighted, correct data; V:198 E:2742
//				 {"Dolphins",	"dolphins.txt",		"0.147",	 "0.4",		    "0.05"}, // Undirected, Un-weighted; V:62  E:159
//				 {"DBLP-CITE",	"dblp_cite.txt",	"0.023",	 "0.1",		    "0.05"}, //correct data; V:12591 E:49743
//				 {"DBLP-CoAuthor","dblp_ca.txt",    "0.046",	 "0.1",		    "0.05"}, //  Undirected, Un-weighted, V:317080 E:1049866; beta_th
//				 {"EURO-Road",	"euroroad.txt",	    "0.333",	 "0.4",         "0.05"}, // V: 1174; E:1417
//				 {"High-School","highschool.txt",   "0.107",	 "0.4",         "0.05"},    // V: 70; E:274   
//				 {"Foodweb",	"foodweb.txt",	    "0.023",	 "0.4",         "0.05"}, // V: 183; E: 2434
//				 {"Macaques",	"macaques.txt",	    "0.026",	 "0.4",         "0.05"}, // V: 65; E:1169      
//				 {"PGP",	    "pgp.txt",	        "0.053",	 "0.4",		    "0.11"},// V: 10680  E: 24316
//				 {"Football",	"football.txt",	    "0.093",	 "0.4",		    "0.35"},// Undirected un weighted V: 115;  E:613
//				 {"Zebra",	    "zebra.txt",	    "0.091",	 "0.4",		    "0.35"},// V:27;  E:111
//				 {"Odlis",	    "odlis.txt",	    "0.014",	 "0.9",		    "0.35"},// V:2909;  E:18241; beta no change
//				 {"Test",	    "test.txt",	        "0.31",		"0.4",		    "0.35"},// V:13;  E:18
				 {"Toy_DNS",	"toy_dns.txt",	    "0.276",	 "0.4",		    "0.30"},  
				};


		for (int i=0; i < networks.length; i++) {

			System.err.println(networks[i][0]);
			String filename = "dataset/undirected/" + networks[i][1];
			IndexedGraph graph = IndexedGraph.readGraph(filename);

			PrintStream out = new PrintStream(new File("results/ccdf.lss."+networks[i][1]));
			
			/*
			DegreeRanker degreeRanker = new DegreeRanker(graph);
			degreeRanker.evaluate();
	
			KShellRanker kshellRanker = new KShellRanker(graph);
			kshellRanker.evaluate();
			
			KShellPlusRanker kshellplusRanker = new KShellPlusRanker(graph);
			kshellplusRanker.evaluate();
	       
			double lambda = 0.7;
			MDDRanker mddRanker = new MDDRanker(graph, lambda);
			mddRanker.evaluate();

			PotentialEdgeWtKShellRanker potEdgeWt = new PotentialEdgeWtKShellRanker(graph);
			potEdgeWt.evaluate();
			
			ImprovedRanker improvedRanker = new ImprovedRanker(graph);
			improvedRanker.evaluate();

			double lambda1 = 0;
			WtKsDegreeNeighborhood_Giridhar ksW_GM = new WtKsDegreeNeighborhood_Giridhar(graph,lambda1, 1000);
			ksW_GM.evaluate();
	
	 */
			double alpha=0.15; // setting alpha=0.15 is same as setting damping factor = 0.85 = (1-alpha)
			PageRankRanker pageRanker = new PageRankRanker(graph, alpha);
			pageRanker.evaluate();
			
		   // PageRank with alpha=0.0 yield same score as EigenVectorRanker
			EigenVectorRanker  evRanker = new EigenVectorRanker(graph);
			evRanker.evaluate();
			
			//double delta=0.5;// as per original paper https://doi.org/10.1038/s41598-023-30308-5
		//	EHCCRanker ehcc = new EHCCRanker(graph,delta, 1000 );
		//	ehcc.evaluate();
			
			LSS_Ullah_Ranker lss = new LSS_Ullah_Ranker(graph);
			lss.evaluate();
			
	//		LGC_Ullah_Ranker lgc = new LGC_Ullah_Ranker(graph);
	//		lgc.evaluate();
			
	//		DirectionalNodeStrengthV2 dns2Ranker = new DirectionalNodeStrengthV2(graph, lambda, 10000);
	//		dns2Ranker.evaluate();
			
//			BetweennessRanker betweennessRanker = new BetweennessRanker(graph);
//			betweennessRanker.evaluate();
//	
//			ClosenessRanker closenessRanker = new ClosenessRanker(graph);
//			closenessRanker.evaluate();
	
//			double cpks, cpdeg, cpkm, cpimp, cpcore, cpext, cpbet, cpclo;
//			cpks = cpdeg = cpkm = cpimp = cpcore = cpext = cpbet = cpclo = 0.0;

			double cpdeg, cpks, cpksplus, cpmdd, cpksw, cpimp, cpkswgm;
			double cplgc,cplss,cpdnsv2;
			double cppr, cpevc, cpehcc;
			cppr = cpevc = cpehcc = 0.0;
			cpks = cpdeg = cpmdd = cpimp = cpksplus = cpksw = cpkswgm = cplgc = cplss = cpdnsv2 = 0.0;
			//out.println("rank k ks ks+ ksw kswgm mdd theta dnsv2");
			out.println("rank \t PR \t EigenV \t LSS");
			double N = graph.getVertexCount();
			for (int rank=1; rank<N; rank++) {
				//cpdeg += degreeRanker.getRankVolume(rank)/N;
			//	cpks += kshellRanker.getRankVolume(rank)/N;
			//	cpksplus += kshellplusRanker.getRankVolume(rank)/N;
				//cpksw += potEdgeWt.getRankVolume(rank)/N;
				//cpkswgm += ksW_GM.getRankVolume(rank)/N;
			//	cpmdd += mddRanker.getRankVolume(rank)/N;
				//cpimp += improvedRanker.getRankVolume(rank)/N;
				//cpcore += (double)corenessRanker.getRankVolume(rank)/N;
				//cpext += (double)coreplusRanker.getRankVolume(rank)/N;
				cppr += (double)pageRanker.getRankVolume(rank)/N;
				cpevc += (double)evRanker.getRankVolume(rank)/N;
			//	cpehcc += (double)ehcc.getRankVolume(rank)/N;
				cplss += (double)lss.getRankVolume(rank)/N;
			//	cplgc += (double)lgc.getRankVolume(rank)/N;
				//cpdnsv2 += dns2Ranker.getRankVolume(rank)/N;
//				cpbet += (double)betweennessRanker.getRankVolume(rank)/N;
//				cpclo += (double)closenessRanker.getRankVolume(rank)/N;
				
				out.printf("%d \t", rank);
			//	out.printf("%1.6f \t", 1.0-cpdeg);
			//	out.printf("%1.6f \t", 1.0-cpks);
			//	out.printf("%1.6f \t", 1.0-cpksplus);
				//out.printf("%1.6f ", 1.0-cpksw);
				//out.printf("%1.6f ", 1.0-cpkswgm);
			//	out.printf("%1.6f \t", 1.0-cpmdd);
				out.printf("%1.6f \t", 1.0-cppr);
				out.printf("%1.6f \t", 1.0-cpevc);
			//	out.printf("%1.6f \t", 1.0-cpehcc);
				//out.printf("%1.6f ", 1.0-cpimp);
				out.printf("%1.6f ", 1.0-cplss);
			//	out.printf("%1.6f ", 1.0-cplgc);
				//out.printf("%1.6f ", 1.0-cpdnsv2);
				
				out.println();
			}
			out.close();		
		}
		
		long endTime = System.currentTimeMillis();
		System.out.printf("Elapsed Time : %d secs", (endTime-startTime)/1000);
	}
	
}
