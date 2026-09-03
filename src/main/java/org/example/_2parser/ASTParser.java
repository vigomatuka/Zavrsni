package org.example._2parser;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import org.example.model.ParsedMethod;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ASTParser {

    public List<ParsedMethod> parseFiled(List<Path> files, Path serviceRoot) throws IOException {
        List<ParsedMethod> parsiraneMetode = new ArrayList<>();

        CombinedTypeSolver typeSolver = new CombinedTypeSolver(
                new ReflectionTypeSolver(),
                new JavaParserTypeSolver(serviceRoot.resolve("src/main/java"))
        );
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);
        StaticJavaParser.getParserConfiguration().setSymbolResolver(symbolSolver);

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

                List<String> calledMethods = new ArrayList<>();
                //ne moze se uzeti samo ime metode jer moze biti istoimena metoda od druge klase
                for (MethodCallExpr mce : metoda.findAll(MethodCallExpr.class)){
                    try {
                        ResolvedMethodDeclaration resolved = mce.resolve();
                        calledMethods.add(resolved.declaringType().getClassName() + "." + resolved.getName());
                    }
                    catch (UnsolvedSymbolException | UnsupportedOperationException e) {
                        //solver moze pasti na neke vanjske biblioteke ili neke druge rijetke stvari
                        calledMethods.add(mce.getNameAsString());
                    }
                }
                List<String> annotations = metoda.getAnnotations().stream()
                        .map(AnnotationExpr::getNameAsString)
                        .collect(Collectors.toList());
                String mappingPath = null;

                for (AnnotationExpr ann : metoda.getAnnotations()){
                    String annStr = ann.getNameAsString();
                    if (annStr.endsWith("Mapping")){
                        if (ann instanceof SingleMemberAnnotationExpr sma){
                            mappingPath = sma.getMemberValue().toString().replace("\"", "");
                        }
                        else if (ann instanceof NormalAnnotationExpr nae){ //sa slucaje gdje je uri npr value = "users"
                            for (MemberValuePair pair : nae.getPairs()){
                                String pairName = pair.getNameAsString();
                                if (pairName.equals("value") || pairName.equals("path")){
                                    mappingPath = pair.getValue().toString().replace("\"", "");
                                    break;
                                }
                            }
                        }
                    }
                }

                parsiraneMetode.add(new ParsedMethod(name, className, filenName, lineNumber, calledMethods, annotations, mappingPath));
            }
        }

        return parsiraneMetode;
    }

}
