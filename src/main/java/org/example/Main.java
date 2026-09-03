package org.example;

import org.example._1input.InputManager;
import org.example._2parser.ASTParser;
import org.example.model.CallEdge;
import org.example._3graph.CallGraphBuilder;
import org.example.model.MethodNode;
import org.example._4analyser.DeadCodeAnalyser;
import org.example._5report.ReportGenerator;
import org.example.model.ParsedMethod;
import org.jgrapht.Graph;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main() throws Exception{

        Path repo1 = Path.of("C:\\Users\\Vigo\\Downloads\\test-input-primjer\\servis-a-users");
        Path repo2 = Path.of("C:\\Users\\Vigo\\Downloads\\test-input-primjer\\servis-b-orders");

        InputManager inputManager = new InputManager();
        List<Path> files1 = inputManager.scanForFiles(repo1);
        List<Path> files2 = inputManager.scanForFiles(repo2);

        ASTParser astParser = new ASTParser();
        List<ParsedMethod> parsedMethods1 = astParser.parseFiled(files1, repo1);
        List<ParsedMethod> parsedMethods2 = astParser.parseFiled(files2, repo2);

        CallGraphBuilder cgb = new CallGraphBuilder();
        Graph<MethodNode, CallEdge> graph1 = cgb.buildGraph(parsedMethods1);
        Graph<MethodNode, CallEdge> graph2 = cgb.buildGraph(parsedMethods2);

        DeadCodeAnalyser dca = new DeadCodeAnalyser();
        Set<MethodNode> dead1 = dca.findDeadCode(graph1);
        Set<MethodNode> dead2 = dca.findDeadCode(graph2);

        ReportGenerator rg = new ReportGenerator();
        rg.generateReport(dead1, dead2, repo1, repo2);

        /*
        for (Path a : files1){
            System.out.println(a);
        }

        for (Path a : files2){
            System.out.println(a);
        }
        for (ParsedMethod a: parsedMethods1){
            System.out.println(a.getMethodName());
            System.out.println(a.getClassName());
            System.out.println(a.getFileName());
            System.out.println(a.getLineNumber());
            System.out.println(a.getCalledMethods());
            System.out.println(a.getAnnotations());
            System.out.println(a.getMappingPath());
            System.out.println();
        }
        for (ParsedMethod a: parsedMethods2){
            System.out.println(a.getMethodName());
            System.out.println(a.getClassName());
            System.out.println(a.getFileName());
            System.out.println(a.getLineNumber());
            System.out.println(a.getCalledMethods());
            System.out.println(a.getAnnotations());
            System.out.println(a.getMappingPath());
            System.out.println();
        }

        System.out.println("Čvorovi:");
        for (MethodNode node : graph1.vertexSet()){
            System.out.println(node.getClassName() + "." + node.getMethodName());
        }
        System.out.println("\nBridovi");
        for (CallEdge edge : graph1.edgeSet()){
            MethodNode source = graph1.getEdgeSource(edge);
            MethodNode target = graph1.getEdgeTarget(edge);
            System.out.println(
                    source.getClassName() + "." + source.getMethodName()
                    + " --> "
                    + target.getClassName() + "." + target.getMethodName()
            );
        }

        System.out.println("\nMrtvi");
        for (MethodNode a : dead1){
            System.out.println(a.getClassName() + "." + a.getMethodName());
        }

         */


    }
}
