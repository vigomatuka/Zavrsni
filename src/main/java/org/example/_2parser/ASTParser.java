package org.example._2parser;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import org.example.model.ParsedMethod;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ASTParser {

    public List<ParsedMethod> parseFiled(List<Path> files) throws IOException {
        List<ParsedMethod> parsiraneMetode = new ArrayList<>();

        for (Path file : files){
            CompilationUnit cu = StaticJavaParser.parse(file);
            List<MethodDeclaration> metode = cu.findAll(MethodDeclaration.class);
            for(MethodDeclaration metoda : metode){
                String name = metoda.getNameAsString();
                String className = metoda
                        .findAncestor(ClassOrInterfaceDeclaration.class)
                        .map(ClassOrInterfaceDeclaration::getNameAsString)
                        .orElse("UNKNOWN");
                String filenName = file.getFileName().toString();
                int lineNumber = metoda.getBegin().map(p -> p.line).orElse(-1); //-1 je sentinel vrijednost
                List<String> calledMethods = metoda.findAll(MethodCallExpr.class).stream()
                        .map(MethodCallExpr::getNameAsString)
                        .collect(Collectors.toList()); //collect sakuplja rezultat u strukturu, a Colelctor je pomocna klasa
                List<String> annotations = metoda.getAnnotations().stream()
                        .map(AnnotationExpr::getNameAsString)
                        .collect(Collectors.toList());
                parsiraneMetode.add(new ParsedMethod(name, className, filenName, lineNumber, calledMethods, annotations));
            }
        }

        return parsiraneMetode;
    }

}
