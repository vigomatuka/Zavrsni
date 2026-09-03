package org.example._4analyser;

import org.example.model.CallEdge;
import org.example.model.MethodNode;
import org.jgrapht.Graph;
import org.jgrapht.traverse.DepthFirstIterator;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class DeadCodeAnalyser {

    public Set<MethodNode> findDeadCode(Graph<MethodNode, CallEdge> graph){
        Set<MethodNode> all = new HashSet<>(graph.vertexSet());
        Set<MethodNode> dead = new HashSet<>(graph.vertexSet());
        Set<MethodNode> entryNodes = new HashSet<>();
        Set<MethodNode> alive = new HashSet<>();

        for (MethodNode node : all){
            if (node.isEntryPoint()) entryNodes.add(node);
        }

        for (MethodNode en : entryNodes){
            Iterator<MethodNode> it = new DepthFirstIterator<>(graph, en);
            while (it.hasNext()){
                alive.add(it.next());
            }
        }

        dead.removeAll(alive);
        return dead;
    }
}
