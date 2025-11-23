package com.acanx.util.incubator.process;


import com.acanx.util.incubator.annotation.ObjectCopier;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.Tag;
import com.sun.tools.javac.util.List;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.lang.model.element.Element;



/**
 * ObjectCopierHandler
 *
 * @author ACANX
 * @since 20251123
 */
public class ObjectCopierHandler extends AbstractAnnotationHandler {
    private boolean copyList = false;
    private static final String SRC_TEMP = "objcopier_src";
    private static final String TARGET_TEMP = "objcopier_target";
    // 添加字段来保存当前元素
    private Element currentElement;

    public ObjectCopierHandler() {
    }

    @Override
    public void doHandlerAnnotation(JCTree tree, Element element, Map<String, Object> context) {
        // 保存当前正在处理的元素
        this.currentElement = element;

        if (tree instanceof JCTree.JCMethodDecl) {
            this.copyList = false;
            JCTree.JCMethodDecl jcMethodDecl = (JCTree.JCMethodDecl) tree;

            // 验证方法参数
            if (jcMethodDecl.getParameters().size() != 2) {
                throw new RuntimeException(String.format("方法 %s 由于有 @ObjectCopier 注解必须只能有两个参数",
                        jcMethodDecl.name.toString()));
            }

            java.util.List<JCTree.JCStatement> expressions = this.getCopyExpressions(element, jcMethodDecl);
            JCTree.JCStatement[] arrays = expressions.toArray(new JCTree.JCStatement[0]);
            JCTree.JCBlock block = this.treeMaker.Block(0L, List.from(arrays));

            // 将拷贝语句插入到方法体开头
            if (jcMethodDecl.body != null) {
                block.stats = block.getStatements().appendList(jcMethodDecl.body.getStatements());
            }
            jcMethodDecl.body = block;
        }

        // 处理完成后清除
        this.currentElement = null;
    }

    protected String[] getFieldMappings(Element element) {
        ObjectCopier copier = element.getAnnotation(ObjectCopier.class);
        return copier != null ? copier.fieldMappings() : new String[0];
    }

    protected String[] getIncludeFields(Element element) {
        ObjectCopier copier = element.getAnnotation(ObjectCopier.class);
        return copier != null ? copier.includeFields() : new String[0];
    }

    protected String[] getExcludeFields(Element element) {
        ObjectCopier copier = element.getAnnotation(ObjectCopier.class);
        return copier != null ? copier.excludeFields() : new String[0];
    }

    protected boolean isNullCheckEnabled(Element element) {
        ObjectCopier copier = element.getAnnotation(ObjectCopier.class);
        return copier != null ? copier.nullCheck() : true;
    }

    protected boolean isCopySuperEnabled(Element element) {
        ObjectCopier copier = element.getAnnotation(ObjectCopier.class);
        return copier != null ? copier.copySuper() : false;
    }

    protected Symbol getSymbol(JCTree.JCVariableDecl var) {
        JCTree.JCExpression varType = var.vartype;
        Symbol symbol = null;

        if (varType instanceof JCTree.JCIdent) {
            symbol = ((JCTree.JCIdent) varType).sym;
        } else if (varType instanceof JCTree.JCFieldAccess) {
            symbol = ((JCTree.JCFieldAccess) varType).sym;
        } else if (varType instanceof JCTree.JCTypeApply) {
            // 处理泛型类型，如 List<T>
            String type = ((JCTree.JCIdent) ((JCTree.JCTypeApply) varType).clazz).sym.name.toString();
            if (StringUtils.equalsIgnoreCase("List", type) && !((JCTree.JCTypeApply) varType).arguments.isEmpty()) {
                JCTree.JCExpression exp = (JCTree.JCExpression) ((JCTree.JCTypeApply) varType).arguments.get(0);
                if (exp instanceof JCTree.JCIdent) {
                    symbol = ((JCTree.JCIdent) exp).sym;
                } else if (exp instanceof JCTree.JCFieldAccess) {
                    symbol = ((JCTree.JCFieldAccess) exp).sym;
                }
            }
            this.copyList = true;
        }

        return symbol;
    }

    protected String getSourceObjectName(JCTree.JCMethodDecl jcMethodDecl) {
        return this.copyList ? SRC_TEMP : ((JCTree.JCVariableDecl) jcMethodDecl.params.get(1)).name.toString();
    }

    protected String getTargetObjectName(JCTree.JCMethodDecl jcMethodDecl) {
        return this.copyList ? TARGET_TEMP : ((JCTree.JCVariableDecl) jcMethodDecl.params.get(0)).name.toString();
    }

    protected java.util.List<JCTree.JCStatement> getCopyExpressions(Element element, JCTree.JCMethodDecl jcMethodDecl) {
        java.util.List<JCTree.JCStatement> expressions = new ArrayList<>();
        JavacElements util = this.env.getElementUtils();

        // 获取源对象和目标对象的符号
        Symbol targetSymbol = this.getSymbol((JCTree.JCVariableDecl) jcMethodDecl.getParameters().get(0));
        Symbol sourceSymbol = this.getSymbol((JCTree.JCVariableDecl) jcMethodDecl.getParameters().get(1));

        if (targetSymbol == null || sourceSymbol == null) {
            return expressions;
        }

        // 获取源对象的类信息
        Symbol.ClassSymbol sourceClass = util.getTypeElement(sourceSymbol.toString());
        if (sourceClass == null) {
            throw new RuntimeException(String.format("无法解析源类 %s", sourceSymbol.toString()));
        }

        // 获取目标对象的类信息
        Symbol.ClassSymbol targetClass = util.getTypeElement(targetSymbol.toString());
        if (targetClass == null) {
            throw new RuntimeException(String.format("无法解析目标类 %s", targetSymbol.toString()));
        }

        // 收集拷贝表达式
        java.util.List<JCTree.JCStatement> copyExpressions = new ArrayList<>();

        // 添加空值检查
        if (this.isNullCheckEnabled(element)) {
            expressions.add(this.createNullCheckStatement((JCTree.JCVariableDecl) jcMethodDecl.getParameters().get(0)));
            expressions.add(this.createNullCheckStatement((JCTree.JCVariableDecl) jcMethodDecl.getParameters().get(1)));
        }

        // 处理List拷贝
        if (this.copyList) {
            this.handleListCopy(jcMethodDecl, targetSymbol, sourceSymbol, expressions, copyExpressions);
        }

        // 获取字段映射配置
        Map<String, String> fieldMappings = this.parseFieldMappings(element);
        Set<String> includeFields = this.arrayToSet(this.getIncludeFields(element));
        Set<String> excludeFields = this.arrayToSet(this.getExcludeFields(element));

        // 获取目标类的所有字段
        Map<String, TypeField> targetFields = this.getClassFields(targetClass, this.isCopySuperEnabled(element));
        Map<String, TypeField> sourceFields = this.getClassFields(sourceClass, this.isCopySuperEnabled(element));

        // 生成字段拷贝语句
        for (Map.Entry<String, TypeField> entry : targetFields.entrySet()) {
            String targetFieldName = entry.getKey();
            TypeField targetField = entry.getValue();

            // 检查是否在排除列表中
            if (!excludeFields.isEmpty() && excludeFields.contains(targetFieldName)) {
                continue;
            }

            // 检查是否在包含列表中（如果包含列表不为空）
            if (!includeFields.isEmpty() && !includeFields.contains(targetFieldName)) {
                continue;
            }

            // 查找对应的源字段
            String sourceFieldName = fieldMappings.getOrDefault(targetFieldName, targetFieldName);
            TypeField sourceField = sourceFields.get(sourceFieldName);

            if (sourceField != null && this.isTypeCompatible(targetField.type, sourceField.type)) {
                JCTree.JCStatement copyStatement = this.generateFieldCopyStatement(
                        targetFieldName, sourceFieldName, targetField.type, jcMethodDecl);
                if (copyStatement != null) {
                    copyExpressions.add(copyStatement);
                }
            }
        }

        if (this.copyList) {
            // 对于List拷贝，将拷贝表达式包装在循环中
            this.wrapListCopyExpressions(jcMethodDecl, expressions, copyExpressions, targetSymbol, sourceSymbol);
        } else {
            expressions.addAll(copyExpressions);
        }

        return expressions;
    }

    private void handleListCopy(JCTree.JCMethodDecl jcMethodDecl, Symbol targetSymbol, Symbol sourceSymbol,
                                java.util.List<JCTree.JCStatement> expressions,
                                java.util.List<JCTree.JCStatement> copyExpressions) {
        // 创建目标List对象
        JCTree.JCExpression targetListType = JavacHandlerUtil.getFieldAccess(targetSymbol.type.toString(),
                this.treeMaker, this.names);
        JCTree.JCNewClass targetListInit = this.treeMaker.NewClass(null, List.nil(), targetListType,
                List.nil(), null);
        JCTree.JCVariableDecl targetVar = this.treeMaker.VarDef(
                this.treeMaker.Modifiers(0L),
                this.names.fromString(TARGET_TEMP),
                targetListType,
                targetListInit
        );
        copyExpressions.add(targetVar);
    }

    private void wrapListCopyExpressions(JCTree.JCMethodDecl jcMethodDecl,
                                         java.util.List<JCTree.JCStatement> expressions,
                                         java.util.List<JCTree.JCStatement> copyExpressions,
                                         Symbol targetSymbol, Symbol sourceSymbol) {
        // 创建源元素变量声明
        JCTree.JCVariableDecl sourceElementVar = this.treeMaker.VarDef(
                this.treeMaker.Modifiers(0L),
                this.names.fromString(SRC_TEMP),
                JavacHandlerUtil.getFieldAccess(sourceSymbol.type.toString(), this.treeMaker, this.names),
                null
        );

        // 创建源List标识
        JCTree.JCIdent sourceListIdent = this.treeMaker.Ident(
                ((JCTree.JCVariableDecl) jcMethodDecl.params.get(1)).name
        );

        // 将拷贝表达式包装成块
        JCTree.JCStatement[] copyArray = copyExpressions.toArray(new JCTree.JCStatement[0]);
        JCTree.JCBlock copyBlock = this.treeMaker.Block(0L, List.from(copyArray));

        // 创建增强for循环
        JCTree.JCEnhancedForLoop forLoop = this.treeMaker.ForeachLoop(
                sourceElementVar, sourceListIdent, copyBlock
        );
        expressions.add(forLoop);

        // 将目标List添加到参数0的List中
        JCTree.JCFieldAccess addAccess = this.treeMaker.Select(
                this.treeMaker.Ident(((JCTree.JCVariableDecl) jcMethodDecl.params.get(0)).name),
                this.names.fromString("addAll")
        );
        JCTree.JCMethodInvocation addInvocation = this.treeMaker.Apply(
                List.nil(),
                addAccess,
                List.of(this.treeMaker.Ident(this.names.fromString(TARGET_TEMP)))
        );
        expressions.add(this.treeMaker.Exec(addInvocation));
    }

    private JCTree.JCStatement createNullCheckStatement(JCTree.JCVariableDecl var) {
        JCTree.JCBinary nullCheckExpr = this.treeMaker.Binary(
                Tag.EQ,
                this.treeMaker.Ident(var),
                this.treeMaker.Literal(TypeTag.BOT, null)
        );
        return this.treeMaker.If(nullCheckExpr, this.treeMaker.Return(null), null);
    }

    protected JCTree.JCStatement generateFieldCopyStatement(String targetFieldName, String sourceFieldName,
                                                            String targetType, JCTree.JCMethodDecl jcMethodDecl) {
        String setterName = "set" + StringUtils.capitalize(targetFieldName);
        String getterPrefix = targetType.toLowerCase().contains("boolean") ? "is" : "get";
        String getterName = getterPrefix + StringUtils.capitalize(sourceFieldName);
        // 创建getter调用
        JCTree.JCMethodInvocation getterCall = this.treeMaker.Apply(
                List.nil(),
                this.treeMaker.Select(
                        this.treeMaker.Ident(this.names.fromString(this.getSourceObjectName(jcMethodDecl))),
                        this.names.fromString(getterName)
                ),
                List.nil()
        );
        // 创建setter调用
        JCTree.JCMethodInvocation setterCall = this.treeMaker.Apply(
                List.nil(),
                this.treeMaker.Select(
                        this.treeMaker.Ident(this.names.fromString(this.getTargetObjectName(jcMethodDecl))),
                        this.names.fromString(setterName)
                ),
                List.of(getterCall)
        );

        // 获取当前处理的元素（方法声明）
        Element currentElement = this.getCurrentProcessingElement();
        if (currentElement != null && this.isNullCheckEnabled(currentElement)) {
            return this.createNullCheckedCopyStatement(getterCall, setterCall, targetType);
        } else {
            return this.treeMaker.Exec(setterCall);
        }
    }

    // 添加辅助方法来获取当前正在处理的元素
    private Element getCurrentProcessingElement() {
        // 由于我们在处理方法注解，element参数已经在doHandlerAnnotation方法中传入
        // 但这里无法直接访问，所以我们需要修改设计

        // 方案1：在handlerAnnotation方法中保存当前element
        // 方案2：通过构造函数或上下文传递
        // 这里我们采用方案1，需要修改类结构
        return this.currentElement;
    }

    private JCTree.JCStatement createNullCheckedCopyStatement(JCTree.JCMethodInvocation getterCall,
                                                              JCTree.JCMethodInvocation setterCall,
                                                              String fieldType) {
        JCTree.JCExpression nullCheck;
        if (this.isPrimitiveType(fieldType)) {
            // 基本类型不需要空值检查
            return this.treeMaker.Exec(setterCall);
        } else if (StringUtils.equals(fieldType, "String")) {
            // String类型检查null和empty
            JCTree.JCBinary notNullCheck = this.treeMaker.Binary(Tag.NE, getterCall,
                    this.treeMaker.Literal(TypeTag.BOT, null));
            JCTree.JCMethodInvocation isEmptyCall = this.treeMaker.Apply(List.nil(),
                    this.treeMaker.Select(getterCall, this.names.fromString("isEmpty")), List.nil());
            JCTree.JCUnary notEmptyCheck = this.treeMaker.Unary(Tag.NOT, isEmptyCall);
            JCTree.JCBinary andCheck = this.treeMaker.Binary(Tag.AND, notNullCheck, notEmptyCheck);
            nullCheck = this.treeMaker.Parens(andCheck);
        } else {
            // 其他引用类型只检查null
            nullCheck = this.treeMaker.Parens(
                    this.treeMaker.Binary(Tag.NE, getterCall, this.treeMaker.Literal(TypeTag.BOT, null))
            );
        }
        return this.treeMaker.If(nullCheck, this.treeMaker.Exec(setterCall), null);
    }

    private boolean isPrimitiveType(String type) {
        return StringUtils.equals(type, "int") || StringUtils.equals(type, "long") ||
                StringUtils.equals(type, "double") || StringUtils.equals(type, "float") ||
                StringUtils.equals(type, "boolean") || StringUtils.equals(type, "short") ||
                StringUtils.equals(type, "byte") || StringUtils.equals(type, "char");
    }

    private boolean isTypeCompatible(String targetType, String sourceType) {
        // 简单的类型兼容性检查
        if (StringUtils.equals(targetType, sourceType)) {
            return true;
        }

        // 处理基本类型和包装类型的兼容
        Map<String, String> wrapperMap = new HashMap<>();
        wrapperMap.put("int", "Integer");
        wrapperMap.put("long", "Long");
        wrapperMap.put("double", "Double");
        wrapperMap.put("float", "Float");
        wrapperMap.put("boolean", "Boolean");
        wrapperMap.put("short", "Short");
        wrapperMap.put("byte", "Byte");
        wrapperMap.put("char", "Character");

        return (wrapperMap.containsKey(targetType) && StringUtils.equals(wrapperMap.get(targetType), sourceType)) ||
                (wrapperMap.containsKey(sourceType) && StringUtils.equals(wrapperMap.get(sourceType), targetType));
    }

    private Map<String, String> parseFieldMappings(Element element) {
        Map<String, String> mappings = new HashMap<>();
        String[] fieldMappings = this.getFieldMappings(element);

        for (String mapping : fieldMappings) {
            if (mapping.contains("=")) {
                String[] parts = mapping.split("=");
                if (parts.length == 2) {
                    mappings.put(parts[0].trim(), parts[1].trim());
                }
            }
        }

        return mappings;
    }

    private Set<String> arrayToSet(String[] array) {
        Set<String> set = new HashSet<>();
        for (String item : array) {
            set.add(item);
        }
        return set;
    }

    private Map<String, TypeField> getClassFields(Symbol.ClassSymbol sym, boolean includeSuper) {
        Map<String, TypeField> fields = new HashMap<>();
        this.collectClassFields(sym, fields, includeSuper);
        return fields;
    }

    private void collectClassFields(Symbol.ClassSymbol sym, Map<String, TypeField> fields, boolean includeSuper) {
        if (sym == null) {
            return;
        }

        // 收集当前类的字段
        Iterator<TypeField> fieldIterator = JavacHandlerUtil.getFields(sym, this.trees).iterator();
        while (fieldIterator.hasNext()) {
            TypeField field = fieldIterator.next();
            fields.put(field.name, field);
        }

        // 如果需要，收集父类的字段
        if (includeSuper) {
            Symbol superClass = sym.getSuperclass().tsym;
            if (superClass instanceof Symbol.ClassSymbol && !superClass.toString().equals("java.lang.Object")) {
                collectClassFields((Symbol.ClassSymbol) superClass, fields, true);
            }
        }
    }

    @Override
    public Class getAnnotationType() {
        return ObjectCopier.class;
    }
}