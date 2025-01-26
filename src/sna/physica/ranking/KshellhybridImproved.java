
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


public class KshellhybridImproved extends Ranker {
         
         public int r;
	public KshellhybridImproved(IndexedGraph graph) {
		super(graph);
                
	}
        public KshellhybridImproved(IndexedGraph graph, int r) {
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
                int[] theta = new int[g.getVertexCount()];
                
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
                                Integer ks_count=shellcount.get(kshellMap[core]);
                               // System.out.printf("ks count %d",ks_count);
                                if (shortest == null){
                                    //distance += 0.0;
                                  //  System.err.println("Hitting isolated!!!! -> " + isolated++);  
                                }
                                else
                                {
                                Integer s= shortest.intValue();
                               // System.out.printf("\n shortest path origin index v=%d    des index=%d origin=%s destination %s distance %s %d",v,core,graph.getVertex(v),graph.getVertex(core),shortest,s);
                                //System.out.printf(" \n source %d destination %d distance %d ",graph.getVertex(v),graph.getVertex(core),shortest);
				 if (s <= r && s>0) {
					//distance += (int)Math.log(graph.getVertexCount());
					//System.err.println("Hitting isolated!!!!  ");
					// int R = 3;
					 int denom = r*(r+1);
					 int num = 2*(r-s+1);
					 					double mu = (double)num/denom;
					 					//System.out.println("mu == "+mu+"  s = "+s+"  r== "+r);
                                        distance += ((Math.sqrt(kshellMap[core]*ks))+(mu*graph.degree(core)) )/Math.pow(s,2) ;
                                      //  distance += Math.sqrt(kshellMap[core]+ks)*(1 /  Math.pow(2, (s-1) )) /ks_count ;  //ks*kshellMap[core]*
                                       // System.out.printf(" \n kshell count %d node %s",ks_count, graph.getVertex(core)); 
				   }  //else {
                                }	//distance += shortest.intValue();
			     	//}
			}
			//int value = distance * (ksMax - ks + 1);
                        int ds=(int)(distance* precision);
			nodeValueMap[v] = ds;
                        //nodeValueMap[v] = ds;
                       // System.out.printf(" \n KHI node %s value %s",graph.getVertex(v),distance); 
                        
		}
               /* for (int v=0; v<graph.getVertexCount(); v++) {
			int value = 0;
                        //value=theta[v];
			for (int w: graph.getNeighbors(v)) {
				value += theta[w];
			}
			nodeValueMap[v] = value;
                        System.out.printf(" \n KshellhybridPlus node %s  %svalue ",graph.getVertex(v),nodeValueMap[v]);
		}
                */
		
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
