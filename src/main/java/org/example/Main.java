package org.example;

import org.example._1input.InputManager;
import org.example._2parser.ASTParser;
import org.example.model.ParsedMethod;

import java.nio.file.Path;
import java.util.List;

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
        List<ParsedMethod> parsedMethods1 = astParser.parseFiled(files1);
        List<ParsedMethod> parsedMethods2 = astParser.parseFiled(files2);


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
            System.out.println();
        }

        for (ParsedMethod a: parsedMethods2){
            System.out.println(a.getMethodName());
            System.out.println(a.getClassName());
            System.out.println(a.getFileName());
            System.out.println(a.getLineNumber());
            System.out.println(a.getCalledMethods());
            System.out.println(a.getAnnotations());
            System.out.println();
        }



    }
}
