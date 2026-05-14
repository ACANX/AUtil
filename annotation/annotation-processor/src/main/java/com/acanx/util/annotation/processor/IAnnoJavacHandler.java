package com.acanx.util.annotation.processor;

import com.sun.tools.javac.tree.JCTree;

import java.lang.annotation.Annotation;
import java.util.Map;
import javax.lang.model.element.Element;
/**
 * IJRESAnnoJavacHandler
 *
 * @author ACANX
 * @since 20251123
 */
public interface IAnnoJavacHandler {
    String KEY_TREES = "trees";
    String KEY_PROCESS_ENV = "processEnv";
    String KEY_TREE_MAKER = "treeMaker";
    String KEY_NAMES = "names";
    int LOWEREST = 6;
    int LOWER = 5;
    int LOW = 4;
    int NORMAL = 3;
    int HIGH = 2;
    int HIGHER = 1;
    int HIGHEST = 0;

    void handlerAnnotation(JCTree var1, Element var2, Map<String, Object> var3);

    Class<? extends Annotation> getAnnotationType();

    default int getLevel() {
        return 3;
    }
}
