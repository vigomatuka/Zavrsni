package org.example._5report;

import org.example._3graph.MethodNode;

import java.nio.file.Path;
import java.util.Set;

public class ReportGenerator {

    public void generateReport(Set<MethodNode> mrtvi1, Set<MethodNode> mrtvi2, Path repo1, Path repo2){

        System.out.println("\n    Mrtve metode u prvom sustavu: " + repo1.toString());
        System.out.printf("    %-15s %15s%n", "Klasa", "Metoda");
        System.out.println("   -----------------------------");
        for (MethodNode node : mrtvi1){
            System.out.printf("    %-15s %-15s%n", node.getClassName(), node.getMethodName());
        }

        System.out.println("\n    Mrtve metode u drugom sustavu: " + repo2.toString());
        System.out.printf("    %-15s %-15s%n", "Klasa", "Metoda");
        System.out.println("   -----------------------------");
        for (MethodNode node : mrtvi2){
            System.out.printf("    %-15s %-15s%n", node.getClassName(), node.getMethodName());
        }
    }
}
