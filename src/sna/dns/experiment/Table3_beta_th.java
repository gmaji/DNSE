package sna.dns.experiment;
import java.io.File;
import java.io.PrintStream;
import java.util.Set;
import java.util.TreeSet;

import sna.physica.graph.IndexedGraph;
public class Table3_beta_th {
	
	public static void main(String[] args) throws Exception {
		
		long startTime = System.currentTimeMillis();

		String[][] networks = {
//				{"Schematic",	"schematic.txt",	"0.30"},
//				{"KarateClub",	"karate.txt",		"0.15"},
//				{"Dolphins",	"dolphins.txt",		"0.15"},
//				{"Jazz",		"jazz.txt",			"0.05"},
//				{"NetScience",	"netscience.txt",	"0.15"},
//				{"C.elegans",	"celegans.txt",		"0.05"},
//				{"E-mail",		"email.txt",		"0.10"},
//				{"Blogs",		"blogs.txt",		"0.10"},
//				{"PowerGrid",	"power.txt",		"0.30"},
//				{"AS",			"as.txt",			"0.10"},
//				{"P2P",			"p2p.txt",			"0.10"},
//				{"PGP",			"pgp.txt",			"0.10"},
//				{"CA-AstroPh",	"astroph.txt",		"0.02"},
//				{"CA-CondMat",	"condmat.txt",		"0.06"},
//				{"Enron",		"enron.txt",		"0.02"},
//				{"DBLP",		"dblp.txt",			"0.05"},
//				{"Amazon",		"amazon.txt", 		"0.10"},
				{"Toy_DNS",		"toy_dns.txt", 		"0.10"},//beta_th=0.276
		};

		PrintStream out = new PrintStream(new File("results/beta_th.txt"));
		out.printf("Network\t");
		out.printf("<k>\t");
		out.printf("<k^2>\t");
		out.printf("b_th\t");

		out.println();

		for (int i=0; i < networks.length; i++) {

			String filename = "dataset/undirected/" + networks[i][1];
			IndexedGraph graph = IndexedGraph.readGraph(filename);
			System.err.println(filename);
			
			int n = graph.getVertexCount();
			int m = graph.getEdgeCount();
			double kavg = 0;
			double ksqrd = 0;
			for (int v=0; v<graph.getVertexCount(); v++) {
				kavg += graph.degree(v);
				ksqrd += graph.degree(v)*graph.degree(v);
			}
			kavg /= n;
			ksqrd /= n;
			double betaThreshold = kavg / ksqrd;
			
			out.printf("%s\t", networks[i][0]);
			out.printf("%1.2f\t", kavg);
			out.printf("%1.2f\t", ksqrd);
			out.printf("%1.3f\t", betaThreshold);
	
			out.close();		
		long endTime = System.currentTimeMillis();
		System.out.printf("Elapsed Time : %d secs", (endTime-startTime)/1000);
	}
	}
}
