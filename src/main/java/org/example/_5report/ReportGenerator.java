package org.example._5report;

import org.example.model.MethodNode;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ReportGenerator {

    public void generateReport(Map<String, Set<MethodNode>> dead,Path repo1, Path repo2){

        Set<String> keys = dead.keySet();
        Set<MethodNode> dead1 = new HashSet<>();
        Set<MethodNode> dead2 = new HashSet<>();

        for (String key : keys){
            if (key == repo1.toString()){
                dead1 = dead.get(key);
            }
            if (key == repo2.toString()){
                dead2 = dead.get(key);
            }
        }

        System.out.println("\n    Mrtve metode u prvom sustavu: " + repo1.toString());
        System.out.printf("    %-15s %-15s %-15s%n", "Klasa", "Metoda", "Linija");
        System.out.println("   -----------------------------");

        for (MethodNode node : dead1){
            System.out.printf("    %-15s %-15s %-5s%n", node.getClassName(), node.getMethodName(), node.getLineNumber());
        }

        System.out.println("\n    Mrtve metode u drugom sustavu: " + repo2.toString());
        System.out.printf("    %-15s %-15s %-15s%n", "Klasa", "Metoda", "Linija");
        System.out.println("   -----------------------------");
        for (MethodNode node : dead2){
            System.out.printf("    %-15s %-15s %-5s%n", node.getClassName(), node.getMethodName(), node.getLineNumber());
        }

    }
}
