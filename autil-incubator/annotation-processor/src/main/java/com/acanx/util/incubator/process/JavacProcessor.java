package com.acanx.util.incubator.process;


import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.processing.JavacRoundEnvironment;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;



/**
 * JRESJavacProcessor
 *
 * @author ACANX
 * @since 20251123
 */
@SupportedAnnotationTypes({"com.acanx.util.incubator.annotation.*"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class JavacProcessor extends AbstractProcessor {
    private JavacManager manager = null;

    public JavacProcessor() {
    }

    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        JavacProcessingEnvironment env = this.getJavacProcessingEnvironment(processingEnv);
        if (env != null) {
            this.manager = new JavacManager(env);
        }

    }

    public JavacProcessingEnvironment getJavacProcessingEnvironment(Object procEnv) {
        if (procEnv instanceof JavacProcessingEnvironment) {
            return (JavacProcessingEnvironment)procEnv;
        } else {
            for(Class<?> procEnvClass = procEnv.getClass(); procEnvClass != null; procEnvClass = procEnvClass.getSuperclass()) {
                Object delegate = this.tryGetDelegateField(procEnvClass, procEnv);
                if (delegate == null) {
                    delegate = this.tryGetProxyDelegateToField(procEnvClass, procEnv);
                }

                if (delegate == null) {
                    delegate = this.tryGetProcessingEnvField(procEnvClass, procEnv);
                }

                if (delegate != null) {
                    return this.getJavacProcessingEnvironment(delegate);
                }
            }

            this.processingEnv.getMessager().printMessage(Kind.WARNING, "Can't get the delegate of the gradle IncrementalProcessingEnvironment. JRES processor won't work.");
            return null;
        }
    }

    private Object tryGetDelegateField(Class<?> delegateClass, Object instance) {
        try {
            return getField(delegateClass, "delegate").get(instance);
        } catch (Exception var4) {
            return null;
        }
    }

    private Object tryGetProcessingEnvField(Class<?> delegateClass, Object instance) {
        try {
            return getField(delegateClass, "processingEnv").get(instance);
        } catch (Exception var4) {
            return null;
        }
    }

    private Object tryGetProxyDelegateToField(Class<?> delegateClass, Object instance) {
        try {
            InvocationHandler handler = Proxy.getInvocationHandler(instance);
            return getField(handler.getClass(), "val$delegateTo").get(handler);
        } catch (Exception var4) {
            return null;
        }
    }

    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv instanceof JavacRoundEnvironment) {
            this.manager.parseAnnotations(annotations, (JavacRoundEnvironment)roundEnv);
        }

        return false;
    }

    public static Field getField(Class<?> c, String fName) throws NoSuchFieldException {
        Field f = null;
        Class<?> oc = c;

        while(c != null) {
            try {
                f = c.getDeclaredField(fName);
                break;
            } catch (NoSuchFieldException var5) {
                c = c.getSuperclass();
            }
        }

        if (f == null) {
            throw new NoSuchFieldException(oc.getName() + " :: " + fName);
        } else {
            f.setAccessible(true);
            return f;
        }
    }
}

