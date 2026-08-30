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

        Path path = Path.of("C:\\Users\\Vigo\\Downloads\\test-input-primjer");

        InputManager inputManager = new InputManager();
        List<Path> files = inputManager.scanForFiles(path);

        ASTParser astParser = new ASTParser();
        List<ParsedMethod> parsedMethods = astParser.parseFiled(files);

        for (Path a : files){
            System.out.println(a);
        }

        for (ParsedMethod a: parsedMethods){
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
