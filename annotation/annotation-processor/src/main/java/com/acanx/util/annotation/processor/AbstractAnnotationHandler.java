package com.acanx.util.annotation.processor;

import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Names;

import javax.lang.model.element.Element;
import java.util.Map;

/**
 * AbstractAnnotationHandler
 *
 * @author ACANX
 * @since 20251123
 */
abstract class AbstractAnnotationHandler implements IAnnoJavacHandler {
    protected TreeMaker treeMaker;
    protected JavacProcessingEnvironment env;
    protected Names names;
    protected JavacTrees trees;

    AbstractAnnotationHandler() {
    }

    public final void handlerAnnotation(JCTree tree, Element element, Map<String, Object> context) {
        this.treeMaker = (TreeMaker)context.get("treeMaker");
        this.env = (JavacProcessingEnvironment)context.get("processEnv");
        this.names = (Names)context.get("names");
        this.trees = (JavacTrees)context.get("trees");
        this.doHandlerAnnotation(tree, element, context);
    }

    protected abstract void doHandlerAnnotation(JCTree var1, Element var2, Map<String, Object> var3);
}
