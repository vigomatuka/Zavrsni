package org.example._3graph;

import java.util.Objects;

public class MethodNode {
    private String className;
    private String methodName;
    private boolean entryPoint = false;

    public MethodNode(String className, String methodName){
        this.className = className;
        this.methodName = methodName;
    }

    public String getClassName(){
        return className;
    }
    public String getMethodName(){
        return methodName;
    }
    public boolean isEntryPoint(){
        return entryPoint;
    }
    public void markAsEntryPoint(){
        this.entryPoint = true;
    }
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
