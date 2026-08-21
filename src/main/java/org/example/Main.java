package org.example;

import org.example._1input.InputManager;

import java.nio.file.Path;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main() throws Exception{

        Path path = Path.of("C:\\Users\\Vigo\\Downloads\\test-input-primjer");

        InputManager inputManager = new InputManager();
        List<Path> files = inputManager.scanForFiles(path);

        for (Path a : files){
            System.out.println(a);
        }
    }
}
