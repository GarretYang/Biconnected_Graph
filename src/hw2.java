import java.io.File;
import java.io.*;
import java.util.*;

public class hw2 {

	private static Scanner input;
	
	// a class that represent the edges of the graph
	public static class Edge {
		int v;
		int x;
		
		Edge(int v, int x) {
			this.v = v;
			this.x = x;
		}
		
		public void printEdge() {
			System.out.println("{" + this.v + ", " + this.x + "}");
		}
		
		public boolean equals(Edge edg) {
			if(this.v == edg.v && this.x == edg.x) return true;
			if(this.v == edg.x && this.x == edg.v) return true;
			return false;
		}
	}
	
	// a class that stores all relative variables about the graphs
	public static class GraphInfo {
		int totalEdges;
		int totalVertices; 
		int bicoComponentCount;
		int artiPointCount;
		int rootChildCount;
		int[] dfsNum;
		int[] lowValue;
		boolean[] articulation;
		Stack<Edge> edgeStack;
		
		GraphInfo(int totalVertices) {
			this.totalEdges = 0;
			this.totalVertices = totalVertices;
			this.bicoComponentCount = 0;
			this.artiPointCount = 0;
			this.rootChildCount = 0;			
	        this.dfsNum = new int[this.totalVertices];
	        this.lowValue = new int[this.totalVertices];
	        for (int i = 0; i < this.totalVertices; i++) dfsNum[i] = -1;
	        this.articulation = new boolean[this.totalVertices];
	        this.edgeStack = new Stack<Edge>();
		}

		// this method checks whether a root is an articulation point
		// if the root does not have more than one tree edges, then it 
		// is not an articulation point
		public void checkGraphRoot() {
	        if (rootChildCount <= 1) {
	        	this.articulation[0] = false;
	        	this.artiPointCount--;
	        }
		}
		
	}
	
	// this method can take one or more files inputs from the command line 
	public static void main(String[] args) throws FileNotFoundException, IOException{
        if(args.length == 0) {
        	System.out.println("Incorrect File Input");
        	System.exit(1);
        } 
        /*	This portion was responsible to output a txt file that contains runtime measurements
         * 
         *	
      	String dirName = "F:\\ebook\\CSE417\\HW\\HW2\\biconnectivity-tests\\biconnectivity-tests\\tests";
        File dir = new File(dirName);
        File[] allFiles = dir.listFiles();
        
        PrintStream out = new PrintStream(new File("C:\\Users\\y6773\\eclipse-workspace\\BiconnectedGraph\\src\\testout.txt")); 
        PrintStream console = System.out;
        System.setOut(out);
         */
        
        // Take inputs from the commanline
        for (String fileName: args) {
        	File file = new File(fileName);
			input = new Scanner(file);
	                
			// the range for the vertex
	        int totalVertices = Integer.parseInt(input.next());
	        
	        // a class object that records variables of the graph
	        GraphInfo graphInfo = new GraphInfo(totalVertices);
	        
	        // a graph that records or vertexes and their corresponding edges
	        Hashtable<Integer, List<Integer>> graph = createGraph(input,graphInfo);
	        
	        // the runtime for dfs
	        long totalTime = checkDFStime(graphInfo, graph);
	        
	        graphInfo.checkGraphRoot();
	        
	        System.out.println("The number of nodes is: " + graphInfo.totalVertices);
	        System.out.println("The number of edges is: " + graphInfo.totalEdges);
	        System.out.println("The number of biconnected components is: " + graphInfo.bicoComponentCount);
	        System.out.println("The number of articulation points is: " + graphInfo.artiPointCount);        
	        printArtclPoint(graphInfo);
	        System.out.println("The algorithm's run time is " + totalTime + " ns");
	        System.out.println();

	        //System.setOut(console);
	        input.close();
		}
	}
	
	// this method prints the articulation points of the graph
	private static void printArtclPoint(GraphInfo graphInfo) {
        System.out.print("The articulation points are: (");
        int count = 0;
        for (int i = 0; i < graphInfo.articulation.length; i++) {
        	if (graphInfo.articulation[i] == true) {
        		count++;
        		if (count < graphInfo.artiPointCount) {
        			System.out.print(i + ", ");
        		} else if (count == graphInfo.artiPointCount) {
        			System.out.println(i + ")");
        			break;
        		}
        	}
        }		
	}
	
	// this method calls the dfs method and returns the runtime of the algorithm
	// the time measurement excludes the time for reading inputs 
	private static long checkDFStime(GraphInfo graphInfo, Hashtable<Integer, List<Integer>> graph) {
        long startTime = System.nanoTime();
        dfs(0,0,0,graphInfo,graph);
        long endTime   = System.nanoTime();
        
        return endTime - startTime;
	}
	
	// this method use a Hashtable to store the vertices and their corresponding 
	// edges. The Hashtable was later used in the dfs table
	private static Hashtable<Integer, List<Integer>> createGraph(Scanner input, GraphInfo graphInfo) {
		
		Hashtable<Integer, List<Integer>> graph = new Hashtable<Integer, List<Integer>>(); 
        
		while (input.hasNext()) {
        	int front = Integer.parseInt(input.next());
        	int back = Integer.parseInt(input.next());

        	
        	if (!graph.containsKey(front)) {
        		List<Integer> ajacent = new ArrayList<Integer>();
        		ajacent.add(back);
        		graph.put(front, new ArrayList<Integer>(ajacent));
        	} else {
        		List<Integer> ajacent = graph.get(front);
        		ajacent.add(back);
        		graph.put(front, new ArrayList<Integer>(ajacent));       		
        	}
 
        	if (!graph.containsKey(back)) {
        		List<Integer> ajacent = new ArrayList<Integer>();
        		ajacent.add(front);
        		graph.put(back, new ArrayList<Integer>(ajacent));
        	} else {
        		List<Integer> ajacent = graph.get(back);
        		ajacent.add(front);
        		graph.put(back, new ArrayList<Integer>(ajacent));       		
        	}           	
        	
        	//allEdges.add(new Edge(front,back));
        	graphInfo.totalEdges++;
        }		
		
		return graph;
	}
	
	// The actual implementation of the dfs process. This method can
	// find the articulation points and biconnected components of the graph 
	private static void dfs(int v, int parent, int dfsCounter, GraphInfo graphInfo,
							Hashtable<Integer, List<Integer>> graph) {
		graphInfo.dfsNum[v] = dfsCounter++;
		graphInfo.lowValue[v] = graphInfo.dfsNum[v];
		
		List<Integer> adjacentList = graph.get(v);
		
		for (int x: adjacentList) {
			// target is undiscovered
			if (graphInfo.dfsNum[x] == -1) {
				// at root
				if (v == 0) {
					graphInfo.rootChildCount++;
				}
				
				graphInfo.edgeStack.push(new Edge(v,x));
				dfs(x,v,dfsCounter,graphInfo,graph);
				graphInfo.lowValue[v] = Math.min(graphInfo.lowValue[v], graphInfo.lowValue[x]);
				
				if (graphInfo.lowValue[x] >= graphInfo.dfsNum[v]) {
					
					if (!graphInfo.articulation[v]) {
						graphInfo.artiPointCount++;
						graphInfo.articulation[v] = true;
					}
					graphInfo.bicoComponentCount++;
					System.out.println("Biconnected Components " + graphInfo.bicoComponentCount + ":");
					while (!graphInfo.edgeStack.peek().equals(new Edge(v,x))) {
						graphInfo.edgeStack.pop().printEdge();
					}
				
					graphInfo.edgeStack.pop().printEdge();			
				}
			
				// v,x is back edge
			} else if (x != parent) {
				graphInfo.lowValue[v] = Math.min(graphInfo.lowValue[v], graphInfo.dfsNum[x]);
				
				// Avoid adding duplicated back edges to the stack
				if (graphInfo.dfsNum[x] < graphInfo.dfsNum[v])
					graphInfo.edgeStack.push(new Edge(v,x));
			}
		}
	}
}
