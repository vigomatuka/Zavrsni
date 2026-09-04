package org.example._4analyser;

import org.example.model.CallEdge;
import org.example.model.HttpClientCall;
import org.example.model.MethodNode;
import org.jgrapht.Graph;
import org.jgrapht.traverse.DepthFirstIterator;

import java.util.*;

public class DeadCodeAnalyser {

    public Map<String, Set<MethodNode>> findDeadCode(Graph<MethodNode, CallEdge> graph1, String repo1,
                                                     Graph<MethodNode, CallEdge> graph2, String repo2){
        Set<MethodNode> all1 = new HashSet<>(graph1.vertexSet());
        Set<MethodNode> all2 = new HashSet<>(graph2.vertexSet());
        Set<MethodNode> all = new HashSet<>(all1);
        all.addAll(all2);

        Set<MethodNode> entryNodes = new HashSet<>();
        for (MethodNode node : all){
            if (node.isEntryPoint()) entryNodes.add(node);
        }

        Map<String, MethodNode> endpointIndex = new HashMap<>();
        for (MethodNode node : all){
            if (node.getHttpMethod() != null){
                String key = node.getHttpMethod() + " " + node.getMappingPath();
                endpointIndex.put(key, node);
            }
        }

        Set<MethodNode> alive = new HashSet<>();
        Queue<MethodNode> toProcess = new ArrayDeque<>(entryNodes);
        while (!toProcess.isEmpty()){
            MethodNode current = toProcess.poll();
            if (alive.contains(current)) continue;

            Graph<MethodNode, CallEdge> graph = all1.contains(current) ? graph1 : graph2;
            Iterator<MethodNode> it = new DepthFirstIterator<>(graph, current);
            while (it.hasNext()){
                MethodNode node = it.next();
                alive.add(node);

                if (!node.getHttpClientCalls().isEmpty()){

                    for (HttpClientCall call : node.getHttpClientCalls()) {
                        String key = call.httpMethod() + " " + call.url();
                        MethodNode endpoint = endpointIndex.get(key);
                        if (endpoint != null && !alive.contains(endpoint)) {
                            toProcess.add(endpoint);
                        }
                    }
                }

            }
        }

        Set<MethodNode> dead1 = new HashSet<>(all1);
        dead1.removeAll(alive);
        Set<MethodNode> dead2 = new HashSet<>(all2);
        dead2.removeAll(alive);

        return Map.of(repo1, dead1, repo2, dead2);
    }
}
