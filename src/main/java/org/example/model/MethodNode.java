package org.example.model;

import java.util.List;
import java.util.Objects;

public class MethodNode {
    private String className;
    private String methodName;
    private boolean entryPoint = false;
    private String fileName;
    private int lineNumber;
    private String mappingPath; //jest
    private String httpMethod; //jest
    private List<HttpClientCall> httpClientCalls; //salje

    public MethodNode(String className, String methodName, String fileName, int lineNumber,
                      String mappingPath, String httpMethod, List<HttpClientCall> httpClientCalls){
        this.className = className;
        this.methodName = methodName;
        this.fileName = fileName;
        this.lineNumber = lineNumber;
        this.mappingPath = mappingPath;
        this.httpMethod = httpMethod;
        this.httpClientCalls = httpClientCalls;
    }

    public String getClassName(){
        return className;
    }
    public String getMethodName(){
        return methodName;
    }

    public boolean isEntryPoint(){ return entryPoint; }
    public void markAsEntryPoint(){
        this.entryPoint = true;
    }

    public String getFileName(){
        return fileName;
    }
    public int getLineNumber(){
        return lineNumber;
    }
    public String getMappingPath() {return mappingPath;}
    public String getHttpMethod() {return httpMethod; }
    public List<HttpClientCall> getHttpClientCalls() {return httpClientCalls;}

    @Override
    public int hashCode(){
        return Objects.hash(className, methodName);
    }
    @Override
    public boolean equals(Object o){ //potreban za pozadinske provjere kod staranja cvorova i stavljanja u mapu
        if (this == o) return true;
        if (!(o instanceof MethodNode other)) return false; //sa svaki slucaj
        // moze biti bez other sa: MethodNode other = (MethodNode) o;
        return className.equals(other.className) && methodName.equals(other.methodName);
    }
}
