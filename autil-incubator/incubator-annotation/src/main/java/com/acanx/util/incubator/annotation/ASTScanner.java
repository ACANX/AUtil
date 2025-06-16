package com.acanx.util.incubator.annotation;//package com.acanx.util.object.copy;
//
//import com.sun.tools.javac.code.Symbol;
//import com.sun.tools.javac.code.Type;
//import com.sun.tools.javac.model.JavacElements;
//import com.sun.tools.javac.tree.JCTree;
//import com.sun.tools.javac.tree.TreeMaker;
//import com.sun.tools.javac.tree.TreeScanner;
//import com.sun.tools.javac.util.List;
//import com.sun.tools.javac.util.Names;
//
//import javax.lang.model.element.ElementKind;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Map;
//import java.util.Set;
//
///**
// * ASTScanner
// *
// * @author ACANX
// * @date 2025-06-14
// * @since 202506
// */
//public class ASTScanner extends TreeScanner {
//    private final TreeMaker maker;
//    private final JavacElements elements;
//    private final Names names;
//
//    ASTScanner(TreeMaker maker, JavacElements elements, Names names) {
//        this.maker = maker;
//        this.elements = elements;
//        this.names = names;
//    }
//    // JDK 8 兼容方法
//    @SuppressWarnings("unused")
//    public void scan(JCTree tree, Void ignored) {
//        if (tree != null) {
//            tree.accept(this);
//        }
//    }
//
//    // JDK 11 兼容的扫描方法
//    @Override
//    public void visitTopLevel(JCTree.JCCompilationUnit tree) {
//        super.visitTopLevel(tree);
//        // 扫描顶级元素
//        for (JCTree def : tree.defs) {
//            if (def instanceof JCTree.JCClassDecl) {
//                visitClassDef((JCTree.JCClassDecl) def);
//            }
//        }
//    }
//
//    @Override
//    public void visitClassDef(JCTree.JCClassDecl tree) {
//        super.visitClassDef(tree);
//        // 扫描类中的所有方法
//        for (JCTree member : tree.defs) {
//            if (member instanceof JCTree.JCMethodDecl) {
//                processMethod((JCTree.JCMethodDecl) member);
//            }
//        }
//    }
//
//    private void processMethod(JCTree.JCMethodDecl method) {
//        // 获取方法上的所有MetaObjectCopy注解
//        List<MetaObjectCopy> annotations = getMetaObjectCopyAnnotations(method);
//        if (!annotations.isEmpty()) {
//            // 合并所有注解配置
//            boolean copyNulls = annotations.stream().anyMatch(MetaObjectCopy::copyNulls);
//            Set<String> ignoreFields = new HashSet<>();
//            Map<String, String> fieldMappings = new HashMap<>();
//            for (MetaObjectCopy ann : annotations) {
//                ignoreFields.addAll(Arrays.asList(ann.ignoreFields()));
//                for (MetaObjectCopy.FieldMapping mapping : ann.fieldMappings()) {
//                    fieldMappings.put(mapping.target(), mapping.source());
//                }
//            }
//            // 替换方法体
//            method.body = createMethodBody(
//                    method.params.get(0),
//                    method.params.get(1),
//                    copyNulls,
//                    ignoreFields,
//                    fieldMappings
//            );
//        }
//    }
//
//    private List<MetaObjectCopy> getMetaObjectCopyAnnotations(JCTree.JCMethodDecl method) {
//        List<MetaObjectCopy> annotations = List.nil();
//        for (JCTree.JCAnnotation ann : method.mods.annotations) {
//            Type type = ann.type;
//            if (type != null) {
//                String annotationName = type.toString();
//                if (annotationName.equals(MetaObjectCopy.class.getName())) {
//                    // 解析注解属性
//                    annotations.add(parseAnnotation(ann));
//                } else if (annotationName.equals(MetaObjectCopy.List.class.getName())) {
//                    // 处理多个注解
//                    for (JCTree.JCExpression expr : ann.args) {
//                        if (expr instanceof JCTree.JCAnnotation) {
//                            annotations.add(parseAnnotation((JCTree.JCAnnotation) expr));
//                        }
//                    }
//                }
//            }
//        }
//        return annotations;
//    }
//
//    private MetaObjectCopy parseAnnotation(JCTree.JCAnnotation ann) {
//        // 简化处理 - 实际应解析所有属性
//        return new MetaObjectCopy() {
//            @Override
//            public boolean copyNulls() {
//                return false; // 简化实现
//            }
//
//            @Override
//            public String[] ignoreFields() {
//                return new String[0]; // 简化实现
//            }
//
//            @Override
//            public FieldMapping[] fieldMappings() {
//                return new FieldMapping[0]; // 简化实现
//            }
//
//            @Override
//            public Class<? extends java.lang.annotation.Annotation> annotationType() {
//                return MetaObjectCopy.class;
//            }
//        };
//    }
//
//    private JCTree.JCBlock createMethodBody(
//            JCTree.JCVariableDecl sourceParam,
//            JCTree.JCVariableDecl targetParam,
//            boolean copyNulls,
//            Set<String> ignoreFields,
//            Map<String, String> fieldMappings
//    ) {
//        List<JCTree.JCStatement> statements = com.sun.tools.javac.util.List.nil();
//
//        // 1. 空值检查
//        statements = statements.append(createNullCheck(sourceParam, targetParam));
//        // 2. 获取源和目标类型
//        Symbol.ClassSymbol sourceType = (Symbol.ClassSymbol) sourceParam.vartype.type.tsym;
//        Symbol.ClassSymbol targetType = (Symbol.ClassSymbol) targetParam.vartype.type.tsym;
//        // 3. 生成字段拷贝语句
//        statements = statements.appendList(createFieldCopyStatements(
//                sourceParam,
//                targetParam,
//                sourceType,
//                targetType,
//                copyNulls,
//                ignoreFields,
//                fieldMappings
//        ));
//        return maker.Block(0, statements);
//    }
//
//    private JCTree.JCStatement createNullCheck(JCTree.JCVariableDecl source, JCTree.JCVariableDecl target) {
//        // if (source == null || target == null) return;
//        return maker.If(
//                maker.Binary(
//                        JCTree.Tag.OR,
//                        maker.Binary(
//                                JCTree.Tag.EQ,
//                                maker.Ident(source.name),
//                                maker.Literal(null)
//                        ),
//                        maker.Binary(
//                                JCTree.Tag.EQ,
//                                maker.Ident(target.name),
//                                maker.Literal(null)
//                        )
//                ),
//                maker.Block(0, List.of(maker.Return(null))),
//                null
//        );
//    }
//
//    private List<JCTree.JCStatement> createFieldCopyStatements(
//            JCTree.JCVariableDecl sourceParam,
//            JCTree.JCVariableDecl targetParam,
//            Symbol.ClassSymbol sourceType,
//            Symbol.ClassSymbol targetType,
//            boolean copyNulls,
//            Set<String> ignoreFields,
//            Map<String, String> fieldMappings) {
//        List<JCTree.JCStatement> statements = com.sun.tools.javac.util.List.nil();
//        // 获取目标字段列表
//        Map<String, Symbol.VarSymbol> targetFields = getAccessibleFields(targetType);
//        for (Map.Entry<String, Symbol.VarSymbol> entry : targetFields.entrySet()) {
//            String targetFieldName = entry.getKey();
//            Symbol.VarSymbol targetField = entry.getValue();
//            // 检查是否忽略该字段
//            if (ignoreFields.contains(targetFieldName)) {
//                continue;
//            }
//            // 获取源字段名（考虑自定义映射）
//            String sourceFieldName = fieldMappings.getOrDefault(targetFieldName, targetFieldName);
//            // 获取源字段
//            Symbol.VarSymbol sourceField = getField(sourceType, sourceFieldName);
//            if (sourceField == null) {
//                continue; // 源对象没有该字段
//            }
//            // 生成字段拷贝语句
//            JCTree.JCStatement copyStmt = createSingleFieldCopy(
//                    sourceParam,
//                    targetParam,
//                    sourceField,
//                    targetField,
//                    copyNulls
//            );
//            if (copyStmt != null) {
//                statements = statements.append(copyStmt);
//            }
//        }
//        return statements;
//    }
//
//    private Map<String, Symbol.VarSymbol> getAccessibleFields(Symbol.ClassSymbol clazz) {
//        Map<String, Symbol.VarSymbol> fields = new HashMap<>();
//        // 获取所有字段（包括继承的）
//        for (Symbol member : elements.getAllMembers(clazz)) {
//            if (member.getKind().equals(ElementKind.FIELD)){
//                Symbol.VarSymbol field = (Symbol.VarSymbol) member;
//                fields.put(field.getSimpleName().toString(), field);
//            }
//        }
//        return fields;
//    }
//
//    private Symbol.VarSymbol getField(Symbol.ClassSymbol clazz, String fieldName) {
//        for (Symbol member : elements.getAllMembers(clazz)) {
//            if (member.getKind().equals(ElementKind.FIELD) &&
//                    member.getSimpleName().toString().equals(fieldName)) {
//                return (Symbol.VarSymbol) member;
//            }
//        }
//        return null;
//    }
//
//    private JCTree.JCStatement createSingleFieldCopy(
//            JCTree.JCVariableDecl sourceParam,
//            JCTree.JCVariableDecl targetParam,
//            Symbol.VarSymbol sourceField,
//            Symbol.VarSymbol targetField,
//            boolean copyNulls) {
//        try {
//            // 1. 构建getter调用
//            String getterName = getAccessorName(sourceField.getSimpleName().toString(), "get");
//            JCTree.JCExpression getterCall = maker.Apply(
//                    com.sun.tools.javac.util.List.nil(),
//                    maker.Select(maker.Ident(sourceParam.name), names.fromString(getterName)),
//                    com.sun.tools.javac.util.List.nil()
//            );
//            // 2. 构建setter调用
//            String setterName = getAccessorName(targetField.getSimpleName().toString(), "set");
//            JCTree.JCExpression setterCall = maker.Apply(
//                    com.sun.tools.javac.util.List.nil(),
//                    maker.Select(maker.Ident(targetParam.name), names.fromString(setterName)),
//                    List.of(getterCall)
//            );
//            // 3. 基本类型直接赋值
//            if (targetField.type.isPrimitive()) {
//                return maker.Exec(setterCall);
//            }
//            // 4. 非基本类型处理
//            if (copyNulls) {
//                // 允许空值 - 直接赋值
//                return maker.Exec(setterCall);
//            } else {
//                // 不允许空值 - 添加空值检查
//                JCTree.JCExpression notNullCheck = maker.Binary(
//                        JCTree.Tag.NE,
//                        getterCall,
//                        maker.Literal(null)
//                );
//                return maker.If(
//                        notNullCheck,
//                        maker.Exec(setterCall),
//                        null
//                );
//            }
//        } catch (Exception e) {
//            // getter/setter不存在
//            return null;
//        }
//    }
//
//    private String getAccessorName(String fieldName, String prefix) {
//        return prefix + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
//    }
//}
//
