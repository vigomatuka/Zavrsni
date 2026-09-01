package org.example._3graph;

import org.jgrapht.graph.DefaultEdge;

public class CallEdge extends DefaultEdge {
    private boolean crossService = false;

    private boolean isCrossService(){
        return crossService;
    }
    private void markAsCrossService(){
        this.crossService = true;
    }
}
