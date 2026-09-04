package org.example._2parser;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import org.example.model.HttpClientCall;
import org.example.model.ParsedMethod;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
                String methodName = metoda.getNameAsString();
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

                //Mapping path metode
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
                //Mapping path klase
                String classPath = "";
                ClassOrInterfaceDeclaration classDecl = metoda.findAncestor(ClassOrInterfaceDeclaration.class).orElse(null);
                if (classDecl != null){ //teoretski se može dogoditi da metoda nema klasu ako je noesto record ili enum
                    for (AnnotationExpr ann : classDecl.getAnnotations()){
                        if (ann.getNameAsString().equals("RequestMapping")){
                            if (ann instanceof SingleMemberAnnotationExpr sma){
                                classPath = sma.getMemberValue().toString().replace("\"", "");
                            } else if (ann instanceof NormalAnnotationExpr nae){
                                for (MemberValuePair pair : nae.getPairs()){
                                    String pairName = pair.getNameAsString();
                                    if (pairName.equals("value") || pairName.equals("path")){
                                        classPath = pair.getValue().toString().replace("\"", "");
                                    }
                                }
                            }
                        }
                    }
                }
                if (mappingPath != null){
                    mappingPath = classPath + mappingPath;
                    mappingPath = mappingPath.replaceAll("\\{[^/]+\\}", "*");
                }

                //HttpClientCalls
                List<HttpClientCall> httpClientCalls = new ArrayList<>();
                for (MethodCallExpr mce : metoda.findAll(MethodCallExpr.class)){
                    if (mce.getScope().isEmpty()) continue; //getscope je ono prije . dakle restTemplate u restTemplate.getForObject()
                    ResolvedType scopeType;
                    try{
                        scopeType = mce.getScope().get().calculateResolvedType();
                    } catch (Exception e) {
                        //e.printStackTrace();
                        continue;
                    }
                    if (!scopeType.describe().equals("org.springframework.web.client.RestTemplate")){
                        continue;
                    }
                    String calledMethodName = mce.getNameAsString();
                    if (!Set.of("getForObject", "getForEntity", "postForObject", "postForEntity", "put", "delete").contains(calledMethodName)){
                        continue;
                    }
                    Expression urlArg = mce.getArgument(0);
                    String url = extractUrl(urlArg, classDecl);

                    String httpMethod = methodNametoHttpMethod(calledMethodName);
                    httpClientCalls.add(new HttpClientCall(httpMethod, url));
                }

                parsiraneMetode.add(new ParsedMethod(methodName, className, filenName, lineNumber,
                        calledMethods, annotations, mappingPath, httpClientCalls));
            }
        }

        return parsiraneMetode;
    }

    private String extractUrl(Expression urlArg, ClassOrInterfaceDeclaration classDecl){
        String url = "*"; //sa * ne puca konkatenacija, a sa null puca, zato je ovdje *
        if (urlArg instanceof StringLiteralExpr){
            url = urlArg.asStringLiteralExpr().asString();
        }
        if (urlArg instanceof BinaryExpr){
            BinaryExpr binary = urlArg.asBinaryExpr();

            if (binary.getOperator().equals(BinaryExpr.Operator.PLUS)){ //inace se koristi == jer je Operator enumm ali nema veze
                Expression left = binary.getLeft();
                Expression right = binary.getRight();

                String leftUrl = extractUrl(left, classDecl);
                String rightUrl = extractUrl(right, classDecl);
                url = leftUrl + rightUrl;
            }
        }
        if (urlArg instanceof NameExpr ne && classDecl != null){
            for (FieldDeclaration field : classDecl.getFields()){
                for (VariableDeclarator vd : field.getVariables()){
                    if (vd.getNameAsString().equals(ne.getNameAsString()) &&
                        vd.getInitializer().isPresent() &&
                        vd.getInitializer().get() instanceof StringLiteralExpr sle){
                        url = sle.asString();
                    }
                }
            }
        }
        if (url.matches("https?://.*")){
            url = url.replaceFirst("https?://[^/]+", "");
        }
        return url;
    }

    private String methodNametoHttpMethod(String methodName){
        return switch (methodName){
            case "getForObject", "getForEntity" -> "GET";
            case "postForObject", "postForEntity" -> "POST";
            case "put" -> "PUT";
            case "delete" -> "DELETE";
            default -> "UNKNOWN"; //za exchange jer mi ga se nije dalo radit
        };
    }

}
