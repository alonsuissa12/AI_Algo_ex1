
import java.util.*;

public class build_net {


    public static class Graph {
        private Map<
                element, Map<String,List<Edge>>
                >adj;

        public Graph() {
            this.adj = new HashMap<>();
        }
        public Graph(List<element> elements) {
            this.adj = new HashMap<>();
            addVertexes(elements);

        }

        public void addVertexes( List<element> elements ) {
            for (element e : elements) { // add all elements to the graph
                HashMap<String,List<Edge> > edges = new HashMap<>();
                edges.put("to_me", new ArrayList<>());
                edges.put("from_me", new ArrayList<>());
                adj.putIfAbsent(e, edges);
                }
            
            for (element e : elements) { // add all edges
                for (String[] key : e.getDictionary().keySet()) {
                    for (String parent_name : key){
                        element parent = null;
                        for (element el : elements) { // find the parent element
                            if (el.getName().equals(parent_name)) {
                                parent = el;
                                break;
                            }
                        }
                        if (parent != null) {
                            addEdge(parent, e);
                        } else {
                            System.out.println("Parent not found: " + parent_name);
                        }
                    }
                }
            }
        }

        public void addEdge(element from, element to) {
            add_to_me(from, to);
            add_from_me(from, to);
//            adj.get(from).add(new Edge(to));
        }
        
        public void add_to_me(element from, element me) {
            if (adj.containsKey(me)) {
                adj.get(me).get("to_me").add(new Edge(from, me));
            } else {
                System.out.println("Vertex " + from + " not found.");
            }
        }
        
        public void add_from_me(element me, element to) {
            
            if (adj.containsKey(me)) {
                adj.get(me).get("from_me").add(new Edge(me, to));
            } else {
                System.out.println("Vertex " + to + " not found.");
            }
        }

        public void printGraph() {
            for (element node : adj.keySet()) {
                System.out.print("Vertex " + node.getName() + ": ");
                Map<String, List<Edge>> edgesMap = adj.get(node);
                if (edgesMap != null) {
                    for (Map.Entry<String, List<Edge>> entry : edgesMap.entrySet()) {
                        System.out.print(entry.getKey() + ": ");
                        if(Objects.equals(entry.getKey(), "to_me")){
                            for (Edge edge : entry.getValue()) {
                                System.out.print("( " +  node.getName()  + " <-" + edge.from.getName() + " ) ");
                            }
                        }
                        else{
                            for (Edge edge : entry.getValue()) {
                                System.out.print("( " +  node.getName()  + " ->" + edge.to.getName() + " ) ");
                            }
                        }
                    }
                }
                System.out.println();
            }
        }

        private static class Edge {
            element to;
            element from;

            public Edge(element from, element to) {
                this.to = to;
                this.from = from;
            }
        }
    }


}



