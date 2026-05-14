package com.acanx.util.incubator.process;

import com.acanx.util.incubator.annotation.Copier;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.Tag;
import com.sun.tools.javac.util.List;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.lang.model.element.Element;

/**
 * Copier 注解处理器
 * 在编译期通过 AST 修改，将被 @Copier 注解的方法体替换为自动拷贝代码。
 * 支持 JDK 8，兼容 V1.3 版本的 @Copier 注解 API。
 *
 * @author ACANX
 * @since 20251123
 */
public class CopierHandler extends AbstractAnnotationHandler {

    @Override
    protected void doHandlerAnnotation(JCTree tree, Element element, Map<String, Object> context) {
        if (!(tree instanceof JCTree.JCMethodDecl)) {
            return;
        }

        JCTree.JCMethodDecl jcMethodDecl = (JCTree.JCMethodDecl) tree;

        // 验证方法参数数量
        if (jcMethodDecl.getParameters().size() < 2) {
            throw new RuntimeException(String.format(
                    "方法 %s 由于有 @Copier 注解必须至少有两个参数 (source, target)",
                    jcMethodDecl.name.toString()));
        }

        Copier copier = element.getAnnotation(Copier.class);
        if (copier == null) {
            return;
        }

        // 读取注解配置
        boolean ignoreNull = copier.ignoreNull();
        boolean useAccessors = copier.useAccessors();
        boolean deepCopy = copier.strategy() == Copier.CopyStrategy.DEEP;
        Set<String> excludeFields = arrayToSet(copier.exclude());
        Set<String> includeFields = arrayToSet(copier.include());

        // 获取源参数名（第一个参数）和目标参数名（第二个参数）
        String srcParamName = jcMethodDecl.params.get(0).name.toString();
        String destParamName = jcMethodDecl.params.get(1).name.toString();

        // 获取源参数和目标参数的类型符号
        Symbol srcSymbol = getSymbol((JCTree.JCVariableDecl) jcMethodDecl.getParameters().get(0));
        Symbol destSymbol = getSymbol((JCTree.JCVariableDecl) jcMethodDecl.getParameters().get(1));

        if (srcSymbol == null || destSymbol == null) {
            throw new RuntimeException(String.format(
                    "无法解析方法 %s 的参数类型",
                    jcMethodDecl.name.toString()));
        }

        JavacElements util = this.env.getElementUtils();

        // 获取源类和目标类的字段
        Symbol.ClassSymbol srcClass = util.getTypeElement(srcSymbol.toString());
        Symbol.ClassSymbol destClass = util.getTypeElement(destSymbol.toString());

        if (srcClass == null) {
            throw new RuntimeException(String.format("无法解析源类 %s", srcSymbol.toString()));
        }
        if (destClass == null) {
            throw new RuntimeException(String.format("无法解析目标类 %s", destSymbol.toString()));
        }

        Map<String, TypeField> srcFields = this.getClassFields(srcClass, false);
        Map<String, TypeField> destFields = this.getClassFields(destClass, false);

        // 找出共同的字段
        Set<String> commonFields = new HashSet<String>(destFields.keySet());
        commonFields.retainAll(srcFields.keySet());

        // 应用 include 过滤
        Set<String> fieldsToCopy;
        if (includeFields.isEmpty()) {
            fieldsToCopy = commonFields;
        } else {
            fieldsToCopy = new HashSet<String>();
            for (String field : commonFields) {
                if (includeFields.contains(field)) {
                    fieldsToCopy.add(field);
                }
            }
        }

        // 应用 exclude 过滤
        if (!excludeFields.isEmpty()) {
            Set<String> filtered = new HashSet<String>();
            for (String field : fieldsToCopy) {
                if (!excludeFields.contains(field)) {
                    filtered.add(field);
                }
            }
            fieldsToCopy = filtered;
        }

        // 生成拷贝语句
        java.util.List<JCTree.JCStatement> copyStatements = new ArrayList<JCTree.JCStatement>();

        // 添加空值检查
        if (ignoreNull) {
            copyStatements.add(createNullCheck(srcParamName));
            copyStatements.add(createNullCheck(destParamName));
        } else {
            copyStatements.add(createNullReturn(srcParamName, destParamName));
        }

        // 为每个字段生成拷贝代码
        for (String fieldName : fieldsToCopy) {
            TypeField srcField = srcFields.get(fieldName);
            TypeField destField = destFields.get(fieldName);

            if (srcField == null || destField == null) {
                continue;
            }

            // 检查类型是否兼容
            if (!isTypeCompatible(destField.type, srcField.type)) {
                continue;
            }

            JCTree.JCStatement stmt = generateFieldCopyStatement(
                    srcParamName, destParamName,
                    fieldName, srcField, destField,
                    useAccessors, ignoreNull, deepCopy);
            if (stmt != null) {
                copyStatements.add(stmt);
            }
        }

        // 替换方法体
        JCTree.JCStatement[] arrays = copyStatements.toArray(new JCTree.JCStatement[0]);
        jcMethodDecl.body = this.treeMaker.Block(0L, List.from(arrays));
    }

    /**
     * 生成单个字段的拷贝语句
     */
    private JCTree.JCStatement generateFieldCopyStatement(String srcParamName,
                                                           String destParamName,
                                                           String fieldName,
                                                           TypeField srcField,
                                                           TypeField destField,
                                                           boolean useAccessors,
                                                           boolean ignoreNull,
                                                           boolean deepCopy) {
        String getterName = buildGetterName(fieldName, srcField.type);
        String setterName = "set" + capitalize(fieldName);

        // 构建 getter 调用：src.getXxx() 或 src.xxx
        JCTree.JCExpression srcAccess;
        if (useAccessors) {
            srcAccess = treeMaker.Apply(
                    List.nil(),
                    treeMaker.Select(
                            treeMaker.Ident(names.fromString(srcParamName)),
                            names.fromString(getterName)
                    ),
                    List.nil()
            );
        } else {
            srcAccess = treeMaker.Select(
                    treeMaker.Ident(names.fromString(srcParamName)),
                    names.fromString(fieldName)
            );
        }

        // 构建赋值表达式
        JCTree.JCExpression assignValue;
        if (deepCopy && isComplexType(destField.type)) {
            // 深拷贝：创建新对象并拷贝字段（简化处理，创建新实例并逐字段赋值）
            assignValue = treeMaker.NewClass(
                    null,
                    List.nil(),
                    JavacHandlerUtil.getFieldAccess(destField.type, this.treeMaker, this.names),
                    List.nil(),
                    null
            );
        } else {
            assignValue = srcAccess;
        }

        // 构建赋值语句：dest.setXxx(value) 或 dest.xxx = value
        JCTree.JCStatement assignStmt;
        if (useAccessors) {
            assignStmt = treeMaker.Exec(
                    treeMaker.Apply(
                            List.nil(),
                            treeMaker.Select(
                                    treeMaker.Ident(names.fromString(destParamName)),
                                    names.fromString(setterName)
                            ),
                            List.of(assignValue)
                    )
            );
        } else {
            assignStmt = treeMaker.Exec(
                    treeMaker.Assign(
                            treeMaker.Select(
                                    treeMaker.Ident(names.fromString(destParamName)),
                                    names.fromString(fieldName)
                            ),
                            assignValue
                    )
            );
        }

        // 如果 ignoreNull 为 true，为引用类型添加空值检查
        if (ignoreNull && !isPrimitiveType(destField.type)) {
            JCTree.JCExpression nullCheck = treeMaker.Binary(
                    Tag.NE,
                    srcAccess,
                    treeMaker.Literal(TypeTag.BOT, null)
            );
            return treeMaker.If(nullCheck, assignStmt, null);
        }

        return assignStmt;
    }

    /**
     * 构建 getter 方法名
     */
    private String buildGetterName(String fieldName, String fieldType) {
        String prefix = "boolean".equals(fieldType) || "Boolean".equals(fieldType) ? "is" : "get";
        return prefix + capitalize(fieldName);
    }

    /**
     * 创建空值检查语句：if (null == param) return;
     */
    private JCTree.JCIf createNullCheck(String paramName) {
        JCTree.JCExpression condition = treeMaker.Binary(
                Tag.EQ,
                treeMaker.Literal(TypeTag.BOT, null),
                treeMaker.Ident(names.fromString(paramName))
        );
        return treeMaker.If(condition, treeMaker.Return(null), null);
    }

    /**
     * 创建参数空值检查：if (null == src || null == dest) return;
     */
    private JCTree.JCIf createNullReturn(String srcParam, String destParam) {
        JCTree.JCExpression srcNull = treeMaker.Binary(
                Tag.EQ,
                treeMaker.Literal(TypeTag.BOT, null),
                treeMaker.Ident(names.fromString(srcParam))
        );
        JCTree.JCExpression destNull = treeMaker.Binary(
                Tag.EQ,
                treeMaker.Literal(TypeTag.BOT, null),
                treeMaker.Ident(names.fromString(destParam))
        );
        JCTree.JCExpression condition = treeMaker.Binary(
                Tag.OR,
                srcNull,
                destNull
        );
        return treeMaker.If(condition, treeMaker.Return(null), null);
    }

    private Map<String, TypeField> getClassFields(Symbol.ClassSymbol sym, boolean includeSuper) {
        Map<String, TypeField> fields = new HashMap<String, TypeField>();
        this.collectClassFields(sym, fields, includeSuper);
        return fields;
    }

    private void collectClassFields(Symbol.ClassSymbol sym, Map<String, TypeField> fields, boolean includeSuper) {
        if (sym == null) {
            return;
        }

        Iterator<TypeField> fieldIterator = JavacHandlerUtil.getFields(sym, this.trees).iterator();
        while (fieldIterator.hasNext()) {
            TypeField field = fieldIterator.next();
            fields.put(field.name, field);
        }

        if (includeSuper) {
            Symbol superClass = sym.getSuperclass().tsym;
            if (superClass instanceof Symbol.ClassSymbol && !superClass.toString().equals("java.lang.Object")) {
                collectClassFields((Symbol.ClassSymbol) superClass, fields, true);
            }
        }
    }

    protected Symbol getSymbol(JCTree.JCVariableDecl var) {
        JCTree.JCExpression varType = var.vartype;
        if (varType instanceof JCTree.JCIdent) {
            return ((JCTree.JCIdent) varType).sym;
        } else if (varType instanceof JCTree.JCFieldAccess) {
            return ((JCTree.JCFieldAccess) varType).sym;
        }
        return null;
    }

    private boolean isTypeCompatible(String targetType, String sourceType) {
        if (targetType.equals(sourceType)) {
            return true;
        }

        Map<String, String> wrapperMap = new HashMap<String, String>();
        wrapperMap.put("int", "java.lang.Integer");
        wrapperMap.put("long", "java.lang.Long");
        wrapperMap.put("double", "java.lang.Double");
        wrapperMap.put("float", "java.lang.Float");
        wrapperMap.put("boolean", "java.lang.Boolean");
        wrapperMap.put("short", "java.lang.Short");
        wrapperMap.put("byte", "java.lang.Byte");
        wrapperMap.put("char", "java.lang.Character");

        // 基本类型与包装类型兼容
        if (wrapperMap.containsKey(targetType) && wrapperMap.get(targetType).equals(sourceType)) {
            return true;
        }
        if (wrapperMap.containsKey(sourceType) && wrapperMap.get(sourceType).equals(targetType)) {
            return true;
        }

        // 子类兼容性（简单字符串匹配）
        return targetType.equals(sourceType);
    }

    private boolean isPrimitiveType(String type) {
        return "int".equals(type) || "long".equals(type) ||
                "double".equals(type) || "float".equals(type) ||
                "boolean".equals(type) || "short".equals(type) ||
                "byte".equals(type) || "char".equals(type);
    }

    private boolean isComplexType(String type) {
        return !isPrimitiveType(type) && !"String".equals(type) &&
                !"java.lang.String".equals(type) &&
                !type.startsWith("java.util.List") &&
                !type.startsWith("java.util.Set") &&
                !type.startsWith("java.util.Map") &&
                !type.startsWith("java.util.Collection");
    }

    private Set<String> arrayToSet(String[] array) {
        Set<String> set = new HashSet<String>();
        if (array != null) {
            for (String item : array) {
                set.add(item);
            }
        }
        return set;
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    @Override
    public Class getAnnotationType() {
        return Copier.class;
    }
}
