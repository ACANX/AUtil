package com.acanx.util.incubator.process;

import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.processing.JavacRoundEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Names;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * JRESJavacManager
 *
 * @author ACANX
 * @since 20251123
 */
public class JavacManager {

    private Map<String, IAnnoJavacHandler> handlerMap = new HashMap();
    private JavacTrees trees;
    private TreeMaker treeMaker;
    private Names names;
    private JavacProcessingEnvironment env;
    private Map<String, Object> context = new HashMap();

    public JavacManager(JavacProcessingEnvironment env) {
        this.trees = JavacTrees.instance(env);
        this.env = env;
        this.treeMaker = TreeMaker.instance(env.getContext());
        this.names = Names.instance(env.getContext());
        this.init();
    }

    private void init() {
        this.context.clear();
        this.context.put("trees", this.trees);
        this.context.put("processEnv", this.env);
        this.context.put("treeMaker", this.treeMaker);
        this.context.put("names", this.names);

        // 注册其他处理器
//        this.addAnnotationHandler(new JRESSetterHandler());
//        this.addAnnotationHandler(new JRESGetterHandler());
//        this.addAnnotationHandler(new JRESCopyHandler());
//        this.addAnnotationHandler(new JRESCopyForceHandler());
//        this.addAnnotationHandler(new JRESFieldHandler());
//        this.addAnnotationHandler(new JRESToStringHandler());
//        this.addAnnotationHandler(new JRESEqualsAndHashCodeHandler());
//        this.addAnnotationHandler(new JRESDataHandler());

        // 注册ObjectCopier处理器
        this.addAnnotationHandler(new ObjectCopierHandler());
    }


    public void addAnnotationHandler(IAnnoJavacHandler handler) {
        this.handlerMap.put(handler.getAnnotationType().getCanonicalName(), handler);
    }

    public IAnnoJavacHandler getHandler(String type) {
        return (IAnnoJavacHandler)this.handlerMap.get(type);
    }


    public void parseAnnotations(Set<? extends TypeElement> annotations, JavacRoundEnvironment roundEnv) {
        List<IAnnoJavacHandler> handlerList = new ArrayList();
        Iterator var4 = annotations.iterator();
        while(var4.hasNext()) {
            TypeElement anno = (TypeElement)var4.next();
            String name = anno.toString();
            IAnnoJavacHandler handler = this.getHandler(name);
            if (handler != null) {
                handlerList.add(handler);
            }
        }

        handlerList.sort(new Comparator<IAnnoJavacHandler>() {
            public int compare(IAnnoJavacHandler o1, IAnnoJavacHandler o2) {
                int level1 = o1.getLevel();
                int level2 = o2.getLevel();
                return level1 - level2;
            }
        });
        var4 = handlerList.iterator();

        while(var4.hasNext()) {
            final IAnnoJavacHandler handler = (IAnnoJavacHandler)var4.next();
            Set<? extends Element> set = roundEnv.getElementsAnnotatedWith(handler.getAnnotationType());
            Iterator var12 = set.iterator();

            while(var12.hasNext()) {
                final Element element = (Element)var12.next();
                final JCTree jcTree = this.trees.getTree(element);
                jcTree.accept(new TreeTranslator() {
                    public void visitMethodDef(JCTree.JCMethodDecl arg0) {
                        if (jcTree.equals(arg0)) {
                            handler.handlerAnnotation(arg0, element, JavacManager.this.context);
                        }

                        super.visitMethodDef(arg0);
                    }

                    public void visitClassDef(JCTree.JCClassDecl arg0) {
                        if (jcTree.equals(arg0)) {
                            handler.handlerAnnotation(arg0, element, JavacManager.this.context);
                        }

                        super.visitClassDef(arg0);
                    }

                    public void visitVarDef(JCTree.JCVariableDecl arg0) {
                        if (jcTree.equals(arg0)) {
                            handler.handlerAnnotation(arg0, element, JavacManager.this.context);
                        }

                        super.visitVarDef(arg0);
                    }
                });
            }
        }

    }
}
