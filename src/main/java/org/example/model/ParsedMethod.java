package org.example.model;

import java.util.List;

public class ParsedMethod {
    private String methodName;
    private String className;
    private String fileName;
    private int lineNumber;
    private List<String> calledMethods;
    private List<String> annotations;
    private String mappingPath;

    public ParsedMethod(String methodName, String className, String fileName, int lineNumber,
                        List<String> calledMethods, List<String> annotations, String mappingPath){
        this.methodName = methodName;
        this.className = className;
        this.fileName = fileName;
        this.lineNumber = lineNumber;
        this. calledMethods = calledMethods;
        this. annotations = annotations;
        this.mappingPath = mappingPath;
    }

    public String getMethodName(){
        return methodName;
    }

    public String getClassName(){
        return className;
    }

    public String getFileName(){
        return fileName;
    }

    public int getLineNumber(){
        return lineNumber;
    }

    public List<String> getCalledMethods(){
        return calledMethods;
    }

    public List<String> getAnnotations(){
        return annotations;
    }

    public String getMappingPath() {return mappingPath;}
}
