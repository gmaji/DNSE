package sna.physica.ranking;
/** Publication:
 * Maji, Giridhar, Amrita Namtirtha, Animesh Dutta, and Mariana Curado Malta. 
 * "Influential spreaders identification in complex networks with improved k-shell hybrid method." 
 * Expert Systems with Applications 144 (2020): 113092.
 *
 * @author Giridhar
 */
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.uci.ics.jung.algorithms.shortestpath.UnweightedShortestPath;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import sna.physica.graph.IndexedGraph;


public class KshellhybridImprovedPlus extends Ranker {
         
         public int r;
	public KshellhybridImprovedPlus(IndexedGraph graph) {
		super(graph);
                
	}
        public KshellhybridImprovedPlus(IndexedGraph graph, int r) {
		super(graph);
                this.r=r;
	}

	@Override
	public void evaluate() {
		
		preProcessing();
                int[] kshellMap = decompose();
                int precision=1000;
                Graph<Integer, Integer> g = new UndirectedSparseGraph<Integer, Integer>();
		for (int vi=0; vi<graph.getVertexCount(); vi++) {
			for (int vj: graph.getNeighbors(vi)) {
				g.addEdge(g.getEdgeCount(), vi, vj);
			}
		}

		//DijkstraDistance<Integer, Integer> dijkstra = 
			//	new DijkstraDistance<Integer, Integer>(g);
                
                UnweightedShortestPath<Integer, Integer> dijkstra1 = 
				new UnweightedShortestPath<Integer, Integer>(g);
                
                
		//dijkstra1.enableCaching(true);
                double[] theta = new double[g.getVertexCount()];
                
                Map<Integer, Integer> shellcount = 
				new HashMap<Integer, Integer>();
                
                Set<Integer> setUniqueNumbers = new HashSet<Integer>();
                for(int x : kshellMap) {
                  setUniqueNumbers.add(x);
                }
                
               // for(Integer x : setUniqueNumbers) {
                   //System.out.println("kshell value ");
                   //System.out.println(x);
               // }
                
                for (Integer ks: setUniqueNumbers) {
                    int count = 0;
                    //System.out.println("under loop");
                   // System.out.println(ks);
                    
                     for (int ks1=0; ks1<kshellMap.length; ks1++) {
		        if(ks.equals(kshellMap[ks1]))
                        {
                            //System.err.println(ks);
                            //System.out.println(kshellMap[ks1]);
			    count=count+1;
                        }
                    }
                    shellcount.put(ks, count);
                     //System.out.println("shell count number");
                    //System.out.println(count);
                }
                
                
                
                 int isolated = 0;
                
                for (int v=0; v<graph.getVertexCount(); v++) {
			int ks = kshellMap[v];
                        //System.out.printf(" \n KshellhybridPlus node %s value %d",graph.getVertex(v),ks);
			Double distance = 0.0;
			for (int core=0; core< kshellMap.length; core++) {
				Number shortest = dijkstra1.getDistance(v,core);
                                if (shortest == null){
                                   // System.err.println("Hitting isolated!!!! -> " + isolated++);  
                                }
                                else
                                {
                                Integer s= shortest.intValue();
                              
				 if (s <= r && s>0) {
					 int denom = r*(r+1);
					 int num = 2*(r-s+1);
					 double mu = (double)num/denom;
					 //System.out.println("mu == "+mu+"  s = "+s+"  r== "+r);
                     distance += ((Math.sqrt(kshellMap[core]*ks))+(mu*graph.degree(core)) )/Math.pow(s,2) ;
                     //distance += ((Math.sqrt(kshellMap[core]+ks))+(0.1*graph.degree(core)) )/Math.pow(s,2);
				   }  
                                }	
			     	
			}
			
                        int ds=(int)(distance* precision);
			theta[v] = distance;
                     
                        
		}
                for (int v=0; v<graph.getVertexCount(); v++) {
			double value = 0;
                        //value=theta[v];
			for (int w: graph.getNeighbors(v)) {
				value += theta[w];
			}
			nodeValueMap[v] = (int)(value*precision);
                       // System.out.printf(" \n KshellhybridPlus node %s  %svalue ",graph.getVertex(v),value);
		}
                
		
		postProcessing();
                //return theta;
	}

	private int[] decompose() {

		Graph<Integer, Integer> g = new UndirectedSparseGraph<Integer, Integer>();
		for (int vi=0; vi<graph.getVertexCount(); vi++) {
			for (int vj: graph.getNeighbors(vi)) {
				g.addEdge(g.getEdgeCount(), vi, vj);
			}
		}
		
		int[] kshellMap = new int[graph.getVertexCount()]; 
		
		int currentKS = 1;
		while (g.getVertexCount() > 0) {
			boolean isRemoved = false;
			Set<Integer> targets = new HashSet<Integer>(g.getVertices());
			for (int vi: targets) {
				int k = g.degree(vi);
				if (k <= currentKS) {
					g.removeVertex(vi);
					isRemoved = true;
					kshellMap[vi] = currentKS;
				}
			}
			if(!isRemoved) currentKS++;
		}
		
		return kshellMap;
	}
   
}
