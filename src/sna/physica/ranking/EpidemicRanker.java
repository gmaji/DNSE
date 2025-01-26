package sna.physica.ranking;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import sna.physica.graph.IndexedGraph;

public class EpidemicRanker extends Ranker {

	public static final int SUSCEPTIBLE = 0;

	public static final int INFECTED = 1;

	public static final int RECOVERED = 2;

	double beta;
	double gamma;
	
	int iteration;
	
	int[] state;
	
	public EpidemicRanker(IndexedGraph graph, double beta, int iteration) {
		super(graph);
		this.beta = beta;
		this.gamma = 1;
		this.iteration = iteration;
		this.state = new int[graph.getVertexCount()];
	}

	public EpidemicRanker(IndexedGraph graph, double beta, double gamma, int iteration) {
		super(graph);
		this.beta = beta;
		this.gamma = gamma;
		this.iteration = iteration;
		this.state = new int[graph.getVertexCount()];
	}
	
	@Override
	public void evaluate() {
		Date d1 = new Date();
		System.out.println("Entry: EpidemicRanker.evaluate():Time:"+d1.getTime());
		preProcessing();
		System.out.println("PreProcessed");
		
		for (int v=0; v<graph.getVertexCount(); v++) {
			int value = 0;
			for (int t=0; t<iteration; t++) {
				value += sigma(v);
			}
			nodeValueMap[v] = value;
		}
		
		postProcessing();
		System.out.println("PostProcessed");
		Date d2 = new Date();
		System.out.println("Exit: EpidemicRanker.evaluate(): Time:"+d2.getTime()+" Time Taken in seconds = "+(d2.getTime()-d1.getTime())/1000);
	}

	public void evaluate(Set<Integer> nodeSet) {

		preProcessing();

		for (int v=0; v<graph.getVertexCount(); v++) {
			if (!nodeSet.contains(v)) continue;
			int value = 0;
			for (int t=0; t<iteration; t++) {
				value += sigma(v);
			}
			nodeValueMap[v] = value;
		}
		
		postProcessing();
	}
	
	private int sigma(int v) {

		for (int i=0; i<state.length; i++) {
			state[i] = SUSCEPTIBLE;
		}
		state[v] = INFECTED;

		int infectedCount = 1;
		boolean isNewlyInfected = true;
		while (isNewlyInfected) {
			isNewlyInfected = false;
			for (int x=0; x<state.length; x++) {
				if (state[x] != INFECTED) continue;
				int[] neighbors = graph.getNeighbors(x);
				for (int j=0; j<neighbors.length; j++) {
					int y = neighbors[j];
					if (state[y] != SUSCEPTIBLE) continue;
					if (Math.random() < beta) {
						state[y] = INFECTED;
						isNewlyInfected = true;
						infectedCount++;
					}
				}
				if (Math.random() < gamma) {
					state[x] = RECOVERED;
				}
			}
		}
		
		return infectedCount;
	}
	
	public List<Double> compute_Affected_Scale(Set<Integer> seedNodeSet, int iteration, int T) {
		Map<Integer,Map<Integer,Double>> f_t_t = new HashMap<Integer,Map<Integer,Double>>();
		List<Double> f_t_results =new ArrayList<Double>();
		for(int i=0; i<iteration ; i++) {
			f_t_t.put(i,sigma_F_t(seedNodeSet));
		}
		for(int t=0;t< T; t++) {
			double tmp=0;
			for(int k=0;k<iteration; k++) {
				 tmp += f_t_t.get(k).get(t);	
			}
			f_t_results.add(tmp/iteration);
			System.out.println("time:"+t+"  F(t): "+tmp/iteration);
		}
		return f_t_results;
	}
	private Map<Integer,Double> sigma_F_t(Set<Integer> vSet) {
	/**
	 * This calculates the affected scale at time t as F(t) = N_I(t)+N_R(t)/N; 
	 * N = network size = |V|
	 * N_I(t) = number of Infected nodes at time step t
	 * N_R(t) = number of Recovered nodes at time step t
	 * For each time step t, results will be taken simulating 1000 times for computing F(t).
	 */
		Map<Integer,Double> f_t = new HashMap<Integer,Double>();
		int timeStep = 0;
		int N = graph.getVertexCount();
		
		Set<Integer> infectedNodeSet = new HashSet<Integer>();
		Set<Integer> infectedNodeSetTmp = new HashSet<Integer>();
		for (int i=0; i<state.length; i++) {
			state[i] = SUSCEPTIBLE;
		}
		
		infectedNodeSet = vSet;
		infectedNodeSetTmp = infectedNodeSet;
		for(Integer v: vSet) {
			state[v] = INFECTED;
		}
		
		int infectedCount = vSet.size();
		int recoveredCount = 0;
		while(infectedCount > 0 ) {
			timeStep++;
			infectedNodeSet = infectedNodeSetTmp;
			for (int x: infectedNodeSet) {
				if (state[x] != INFECTED) continue; // redundant line
				int[] neighbors = graph.getNeighbors(x);
				for (int j=0; j<neighbors.length; j++) {
					int y = neighbors[j];
					if (state[y] != SUSCEPTIBLE) continue;
					if (Math.random() < beta) {
						state[y] = INFECTED;
						//isNewlyInfected = true;
						infectedNodeSetTmp.add(y);
						infectedCount++;
					}
				}
				if (Math.random() < gamma) {
					state[x] = RECOVERED;
					infectedNodeSetTmp.remove(x);
					infectedCount--;
					recoveredCount++;
				}
			}
			double ft = 0.0;
			ft = (double)(infectedCount + recoveredCount)/N;
			f_t.put(timeStep, ft);
			
		}
		
		return f_t;
	}
	
	public double evaluateCI(Set<Integer> nodeSet) {
/**
 * This method calculates the total number of Infected nodes with given seed nodeset 
 * excluding any overlap i.e. not counting same node twice.
 */
			int value = 0;
			for (int t=0; t<iteration; t++) {
				value += sigmaCI(nodeSet);
		  }
		  return (double)value/iteration;
	}
	
	private int sigmaCI(Set<Integer> nodeSet) {

		for (int i=0; i<state.length; i++) {
			state[i] = SUSCEPTIBLE;
		}
		Set<Integer> infectedNodeSet = new HashSet<Integer>();
		
		int infectedCount =nodeSet.size();
			for(Integer v:nodeSet){
				//System.out.println("in the nodeSet-> v:::"+v);
				state[v] = INFECTED;
				infectedNodeSet.add(v);
			}
			boolean isNewlyInfected = true;
			while (isNewlyInfected) {
				isNewlyInfected = false;
				for (int x=0; x<state.length; x++) {
					if (state[x] != INFECTED) continue;
					int[] neighbors = graph.getNeighbors(x);
					for (int j=0; j<neighbors.length; j++) {
						int y = neighbors[j];
						if (state[y] != SUSCEPTIBLE) continue;
						if (Math.random() < beta) {
							state[y] = INFECTED;
							//System.out.println("Node# :"+y+" Node name: "+graph.getVertex(y)+" becomes Infected");
							infectedNodeSet.add(y);
							isNewlyInfected = true;
							infectedCount++;
						}
					}
					state[x] = RECOVERED;
				}
			}
		
		//System.out.println("infectedCount: = "+infectedCount);
		//System.out.println("infectedNodeSet Size: = "+infectedNodeSet.size());
		return infectedCount;
	}
	
	
	
	public void load(String filename) throws Exception {
		
		preProcessing();
		
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		while (reader.ready()) {
			String line = reader.readLine().trim();
			if (line.length() == 0) continue;
			if (line.startsWith("#")) continue;
			StringTokenizer st = new StringTokenizer(line, " \t");
			String v = st.nextToken().trim();
			int value = Integer.parseInt(st.nextToken().trim());
			nodeValueMap[graph.getIndex(v)] = value;
		}
		reader.close();

		postProcessing();

	}

	public double getNodeValueDouble(String v) {
		return (double)nodeValueMap[graph.getIndex(v)]/iteration;
	}

	public double getNodeValueDouble(int v) {
		return (double)nodeValueMap[v]/iteration;
	}
}
