package org.example._1input;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class InputManager {

    public List<Path> scanForFiles(Path path) throws IOException{
        if (!Files.exists(path)){
            throw new IllegalArgumentException("Putanja ne postoji: " + path);
        }
        if (!Files.isDirectory(path)){
            throw new IllegalArgumentException("Putanja nije direktorij: " + path);
        }

        List<Path> files = new ArrayList<>();
        try(Stream<Path> stream = Files.walk(path)){
            stream.filter(p -> p.toString().endsWith(".java"))
                    .forEach(files::add);
        }

        return files;
    }
}
