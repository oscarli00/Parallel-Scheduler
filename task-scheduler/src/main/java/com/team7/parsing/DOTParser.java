package com.team7.parsing;

import com.paypal.digraph.parser.GraphEdge;
import com.paypal.digraph.parser.GraphNode;
import com.paypal.digraph.parser.GraphParser;
import com.team7.model.Edge;
import com.team7.model.Task;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DOTParser {
    private List<Edge> edges;
    private Map<String, Task> tasks;

    public DOTParser() {
        edges = new ArrayList<>();
        tasks = new HashMap<>();
    }

    public void parse(String filename) throws FileNotFoundException {
        GraphParser parser = new GraphParser(new FileInputStream(filename));
        Map<String, GraphNode> nodeMap = parser.getNodes();
        Map<String, GraphEdge> edgeMap = parser.getEdges();

        for (GraphNode n : nodeMap.values()) {
            String name = n.getId();
            int weight = Integer.parseInt((String) n.getAttribute("Weight"));
            tasks.put(name, new Task(name, weight));
        }

        for (GraphEdge e : edgeMap.values()) {
            String headName = e.getNode2().getId();
            Task head = tasks.get(headName);
            String tailName = e.getNode1().getId();
            Task tail = tasks.get(tailName);
            int weight = Integer.parseInt((String) e.getAttribute("Weight"));
            Edge edge = new Edge(head,tail,weight);
            head.addIngoingEdge(edge);
            tail.addOutgoingEdge(edge);
            edges.add(edge);
        }
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public Map<String, Task> getTasks() {
        return tasks;
    }
}