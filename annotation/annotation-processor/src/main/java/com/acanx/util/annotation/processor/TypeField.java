package com.acanx.util.annotation.processor;

import com.sun.tools.javac.tree.JCTree;

/**
 * JRESTypeField
 *
 * @author ACANX
 * @since 20251123
 */
public class TypeField {
    public String name = "";
    public String type = "";
    public JCTree.JCExpression init = null;

    public TypeField(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public TypeField(String name, String type, JCTree.JCExpression init) {
        this.name = name;
        this.type = type;
        this.init = init;
    }
}
