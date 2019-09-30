import java.io.File;
import java.io.IOException;
import java.util.*;

public class BiconnectedGraphs {

	private static Scanner input;
	private static List<Edge> allEdges;
	private static Hashtable<Integer, List<Integer>> graph;
    private static int bicoComponentCount;
    private static int artiPointCount;
    private static int rootChildCount;
	
	public static class Edge {
		private int v;
		private int x;
		
		Edge(int v, int x) {
			this.v = v;
			this.x = x;
		}
		
		public void printEdge() {
			System.out.println("{" + this.v + ", " + this.x + "}");
		}
		
		public int front() {
			return this.v;
		}
		
		public int back() {
			return this.x;
		}
		
		public boolean equals(Edge edg) {
			if(this.v == edg.front() && this.x == edg.back()) return true;
			if(this.v == edg.back() && this.x == edg.front()) return true;
			return false;
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// line 13-24 reference from
		// https://stackoverflow.com/questions/40822533/input-a-text-file-through-command-line-in-java
        if(args.length == 0) {
            System.out.println("File name not specified.");
            System.exit(1);
        }

        try {
            File file = new File(args[0]);
            input = new Scanner(file);
        } catch (IOException ioException) {
            System.err.println("Cannot open file.");
            System.exit(1);
        }
        
        // the range for the vertex
        int totalVertices = Integer.parseInt(input.next());
        // the edge numbers
        int totalEdges = 0;
        // a list that stores all the edges
        allEdges = new ArrayList<Edge>(); 
        // a graph that records or vertexes and their corresponding edges
        graph = new Hashtable<Integer, List<Integer>>();
        
        while (input.hasNext()) {
        	int front = Integer.parseInt(input.next());
        	int back = Integer.parseInt(input.next());
        	
        	allEdges.add(new Edge(front,back));
        	totalEdges++;
        }
        
        for (Edge currentEdge: allEdges) {
        	int front = currentEdge.front();
        	int back = currentEdge.back();
        	
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
        }
        
        
        
        int[] dfsNum = new int[totalVertices];
        for (int i = 0; i < dfsNum.length; i++) dfsNum[i] = -1;
        int[] lowValue = new int[totalVertices];
        Stack<Edge> edgeStack = new Stack();
        boolean[] articulation = new boolean[totalVertices];
        
        long startTime = System.nanoTime();
        dfs(0,dfsNum,lowValue,0,0,edgeStack,articulation);
        long endTime   = System.nanoTime();
        long totalTime = endTime - startTime;
        
        if (rootChildCount <= 1) {
        	articulation[0] = false;
        	artiPointCount--;
        }
        
        System.out.println("The number of nodes is: " + totalVertices);
        System.out.println("The number of edges is: " + totalEdges);
        System.out.println("The number of biconnected components is: " + bicoComponentCount);
        System.out.println("The number of articulation points is: " + artiPointCount);        
        System.out.print("The articulation points are: (");
        int count = 0;
        for (int i = 0; i < articulation.length; i++) {
        	if (articulation[i] == true) {
        		count++;
        		if (count < artiPointCount) {
        			System.out.print(i + ", ");
        		} else if (count == artiPointCount) {
        			System.out.println(i + ")");
        			break;
        		}
        	}
        }
        System.out.println("The algorithm's run time is " + totalTime + " ns");
        
        
        input.close();
	}
		
	public static void dfs(int v, int[] dfsNum, int[] lowValue,
						   int parent, int dfsCounter, Stack<Edge> edgeStack,
						   boolean[] articulation) {
		dfsNum[v] = dfsCounter++;
		lowValue[v] = dfsNum[v];
		List<Integer> adjacentList = graph.get(v);
		
		for (int x: adjacentList) {
			// target is undiscovered
			if (dfsNum[x] == -1) {
				// at root
				if (v == 0) {
					rootChildCount++;
				}
				
				edgeStack.push(new Edge(v,x));
				dfs(x,dfsNum,lowValue,v,dfsCounter,edgeStack,articulation);
				lowValue[v] = Math.min(lowValue[v], lowValue[x]);
				
				if (lowValue[x] >= dfsNum[v]) {
					
					if (!articulation[v]) {
						artiPointCount++;
						articulation[v] = true;
					}
					bicoComponentCount++;
					System.out.println("Biconnected Components " + bicoComponentCount + ":");
					while (!edgeStack.peek().equals(new Edge(v,x))) {
						edgeStack.pop().printEdge();
					}
				
					edgeStack.pop().printEdge();			
				}
			
				// v,x is back edge
			} else if (x != parent) {
				lowValue[v] = Math.min(lowValue[v], dfsNum[x]);
				
				// Avoid adding duplicated back edges to the stack
				if (dfsNum[x] < dfsNum[v])
					edgeStack.push(new Edge(v,x));
			}
		}
	}
}
