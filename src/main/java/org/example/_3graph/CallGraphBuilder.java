package org.example._3graph;

import org.example.model.CallEdge;
import org.example.model.MethodNode;
import org.example.model.ParsedMethod;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CallGraphBuilder {

    public Graph<MethodNode, CallEdge> buildGraph(List<ParsedMethod> parsedMethods){
        Graph<MethodNode, CallEdge> graph = new DefaultDirectedGraph<>(CallEdge.class);
        Map<String, MethodNode> map = new HashMap<>();

        //Cvorovi i mapa
        for (ParsedMethod pm : parsedMethods){
            String httpMethod = null;
            if (pm.getMappingPath() != null) {
                for (String ann : pm.getAnnotations()) {
                    String hm = switch (ann) {
                        case "GetMapping" -> "GET";
                        case "PostMapping" -> "POST";
                        case "PutMapping" -> "PUT";
                        case "DeleteMapping" -> "DELETE";
                        case "PatchMapping" -> "PATCH";
                        default -> null;
                    };
                    if (hm != null) {
                        httpMethod = hm;
                        break;
                    }
                }
            }
            String mappingPath = pm.getMappingPath(); //regex za zamijeniti {nesto} sa *
            if (mappingPath != null){
                mappingPath = mappingPath.replaceAll("\\{[^/]+\\}", "*");
            }
            MethodNode node = new MethodNode(pm.getClassName(), pm.getMethodName(), pm.getFileName(), pm.getLineNumber(),
                                            mappingPath, httpMethod , pm.getHttpClientCalls());
            graph.addVertex(node);
            String key = getKey(pm);
            map.put(key, node);
        }

        //Bridovi, rjeseno naivno, treba naknadno dodati symbol solver u ParsedMethod
        //onda se moze ovdje resolve() i dobiti stvarnu metodu
        //ovako se gleda samo prvu poziv metode ako npr 2 klase imaju istoimenu metodu
        //onda kada se gleda koja metoda je pozvana da se provjeri je li ziva se gleda samo prva u mapi
        for (ParsedMethod pm : parsedMethods){
            for (String cm : pm.getCalledMethods()){
                MethodNode source = map.get(getKey(pm));
                MethodNode target = map.get(cm);
                if (target != null) graph.addEdge(source, target);
            }
        }

        //Anotacije
        for (ParsedMethod pm : parsedMethods){
            List<String> annotations = pm.getAnnotations();
            if (annotations.contains("Test") || annotations.contains("Bean") ||
                annotations.contains("Scheduled") || annotations.contains("EventListener") ||
                annotations.contains("PostConstruct") || pm.getMethodName().equals("main")){
                MethodNode node = map.get(getKey(pm));
                if (node != null){
                    node.markAsEntryPoint();
                }
            }
        }

        return graph;
    }

    public String getKey(ParsedMethod pm){
        return pm.getClassName() + "." + pm.getMethodName();
    }

}
