package com.acanx.util.incubator.process;

/**
 * JRESJavacHandlerUtil
 *
 * @author ACANX
 * @since 20251123
 */

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.Scope;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Names;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.apache.commons.lang3.StringUtils;

public class JavacHandlerUtil {
    public static ObjectMapper mapper = new ObjectMapper();

    public JavacHandlerUtil() {
    }

    public static String underlineToCamel(String param) {
        if (param != null && !"".equals(param.trim())) {
            int len = param.length();
            StringBuilder sb = new StringBuilder(len);

            for(int i = 0; i < len; ++i) {
                char c = param.charAt(i);
                if (c == '_') {
                    ++i;
                    if (i < len) {
                        sb.append(Character.toUpperCase(param.charAt(i)));
                    }
                } else {
                    sb.append(c);
                }
            }

            return sb.toString();
        } else {
            return "";
        }
    }

    public static String toCamelCase(String str, boolean firstLower) {
        str = underlineToCamel(str);
        if (str.length() > 1) {
            char ch = str.charAt(1);
            if (Character.isUpperCase(ch)) {
                firstLower = true;
            }
        }

        if (!firstLower) {
            str = Character.toUpperCase(str.charAt(0)) + str.substring(1);
        } else {
            str = Character.toLowerCase(str.charAt(0)) + str.substring(1);
        }

        return str;
    }

    public static void handle(JCTree node, String annoType, Map<String, Object> context) {
        if (!context.containsKey(annoType)) {
            context.put(annoType, new ArrayList());
        }

        List<JCTree> list = (List)context.get(annoType);
        list.add(node);
        context.put(annoType, list);
    }

    public static boolean isHandled(JCTree node, String annoType, Map<String, Object> context) {
        if (!context.containsKey(annoType)) {
            return false;
        } else {
            List<JCTree> list = (List)context.get(annoType);
            return list.contains(node);
        }
    }

    public static List<JCTree.JCVariableDecl> getFields(JCTree.JCClassDecl cls) {
        List<JCTree.JCVariableDecl> list = new ArrayList();
        if (cls == null) {
            return Collections.emptyList();
        } else {
            Iterator<JCTree> it = cls.defs.iterator();

            while(it.hasNext()) {
                JCTree next = (JCTree)it.next();
                if (next instanceof JCTree.JCVariableDecl) {
                    JCTree.JCVariableDecl var = (JCTree.JCVariableDecl)next;
                    if ((var.mods.flags & 8L) == 0L) {
                        list.add(var);
                    }
                }
            }

            return list;
        }
    }

    public static List<TypeField> getFields(Symbol.ClassSymbol sym, JavacTrees trees) {
        List<TypeField> list = new ArrayList<>();
        JCTree.JCClassDecl cls = trees.getTree(sym);

        if (cls != null) {
            // 从语法树中获取字段
            Iterator<JCTree.JCVariableDecl> var4 = getFieldsFromTree(cls).iterator();
            while (var4.hasNext()) {
                JCTree.JCVariableDecl var = var4.next();
                // 检查不是静态字段 (8L = STATIC modifier flag)
                if ((var.mods.flags & 8L) == 0L) {
                    String fieldType = getRealType(var.vartype);
                    list.add(new TypeField(var.name.toString(), fieldType, var.init));
                }
            }
        } else {
            // 备用方案：使用符号迭代器
            try {
                // 尝试使用 JDK 8 的 members() 方法
                Iterable<Symbol> members = (Iterable<Symbol>) sym.members();
                for (Symbol member : members) {
                    if (member instanceof Symbol.VarSymbol) {
                        Symbol.VarSymbol varSymbol = (Symbol.VarSymbol) member;
                        // 排除静态字段
                        if (!varSymbol.isStatic()) {
                            String fieldType = varSymbol.type.toString();
                            list.add(new TypeField(varSymbol.name.toString(), fieldType));
                        }
                    }
                }
            } catch (Exception e) {
                // 如果上述方法失败，使用反射或其他方式
                list.addAll(getFieldsViaReflection(sym));
            }
        }
        return list;
    }

    private static List<TypeField> getFieldsViaReflection(Symbol.ClassSymbol sym) {
        List<TypeField> list = new ArrayList<>();
        try {
            // 获取 members 字段
            Field membersField = Symbol.ClassSymbol.class.getDeclaredField("members");
            membersField.setAccessible(true);
            Scope membersScope = (Scope) membersField.get(sym);
            // 使用反射遍历 Scope
            Field tableField = Scope.class.getDeclaredField("table");
            tableField.setAccessible(true);
            Object[] table = (Object[]) tableField.get(membersScope);

            for (Object entry : table) {
                if (entry != null) {
                    // 遍历链表结构
                    Object current = entry;
                    while (current != null) {
                        try {
                            Field symField = current.getClass().getDeclaredField("sym");
                            symField.setAccessible(true);
                            Symbol member = (Symbol) symField.get(current);
                            if (member instanceof Symbol.VarSymbol) {
                                Symbol.VarSymbol varSymbol = (Symbol.VarSymbol) member;
                                if (!varSymbol.isStatic()) {
                                    String fieldType = varSymbol.type.toString();
                                    list.add(new TypeField(varSymbol.name.toString(), fieldType));
                                }
                            }
                            // 移动到下一个节点
                            Field nextField = current.getClass().getDeclaredField("next");
                            nextField.setAccessible(true);
                            current = nextField.get(current);
                        } catch (Exception e) {
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            // 反射失败，返回空列表
        }
        return list;
    }

    // 从类声明中提取字段的辅助方法
    private static List<JCTree.JCVariableDecl> getFieldsFromTree(JCTree.JCClassDecl cls) {
        List<JCTree.JCVariableDecl> fields = new ArrayList<>();

        for (JCTree def : cls.defs) {
            if (def instanceof JCTree.JCVariableDecl) {
                JCTree.JCVariableDecl field = (JCTree.JCVariableDecl) def;
                fields.add(field);
            }
        }
        return fields;
    }



    public static JCTree.JCExpression getFieldAccess(String pkg, TreeMaker treeMaker, Names names) {
        String[] strs = StringUtils.split(pkg, ".");
        JCTree.JCExpression e = null;
        String[] var5 = strs;
        int var6 = strs.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            String str = var5[var7];
            if (e == null) {
                e = treeMaker.Ident(names.fromString(str));
            } else {
                e = treeMaker.Select((JCTree.JCExpression)e, names.fromString(str));
            }
        }

        return (JCTree.JCExpression)e;
    }

    public static String getRealType(JCTree.JCExpression varType) {
        String type = varType.toString();
        if (varType instanceof JCTree.JCIdent) {
            Symbol symbol = ((JCTree.JCIdent)varType).sym;
            return symbol.name.toString();
        } else {
            return type.toString();
        }
    }

    public static char computeValue(char[] source) {
//        char value = true;
//        char backslash = true;
        char value;
        if ((value = source[1]) != '\\') {
            return value;
        } else {
            char digit;
            switch (digit = source[2]) {
                case '"':
                    value = '"';
                    break;
                case '\'':
                    value = '\'';
                    break;
                case '\\':
                    value = '\\';
                    break;
                case 'b':
                    value = '\b';
                    break;
                case 'f':
                    value = '\f';
                    break;
                case 'n':
                    value = '\n';
                    break;
                case 'r':
                    value = '\r';
                    break;
                case 't':
                    value = '\t';
                    break;
                default:
                    int number = ScannerHelper.getNumericValue(digit);
//                    char singlequotes = true;
                    int sourceIndex = 3;
                    if ((digit = source[sourceIndex]) != '\'') {
                        number = number * 8 + ScannerHelper.getNumericValue(digit);
                        sourceIndex = 4;
                        if ((digit = source[sourceIndex]) != '\'') {
                            number = number * 8 + ScannerHelper.getNumericValue(digit);
                        }

                        value = (char)number;
                    } else {
                        value = (char)number;
                    }
            }

            return value;
        }
    }

    public static boolean typeEqual(String type1, String type2) {
        int index1 = StringUtils.lastIndexOf(type1, ".");
        if (index1 > -1) {
            type1 = StringUtils.substring(type1, index1 + 1);
        }

        int index2 = StringUtils.lastIndexOf(type2, ".");
        if (index2 > -1) {
            type2 = StringUtils.substring(type2, index2 + 1);
        }

        return StringUtils.equalsIgnoreCase(type1, type2);
    }

    public static boolean containsMethod(JCTree.JCClassDecl type, JCTree.JCMethodDecl method) {
        try {
            String name = parseMethodSign(method);
            Iterator var3 = type.defs.iterator();

            while(var3.hasNext()) {
                JCTree child = (JCTree)var3.next();
                if (child instanceof JCTree.JCMethodDecl) {
                    JCTree.JCMethodDecl mtd = (JCTree.JCMethodDecl)child;
                    if (mtd != method && StringUtils.equals(name, parseMethodSign(mtd))) {
                        return true;
                    }
                }
            }

            return false;
        } catch (Exception var6) {
            return false;
        }
    }

    private static String parseMethodSign(JCTree.JCMethodDecl md) {
        StringJoiner sign = new StringJoiner(",", md.name.toString() + "(", ")");
        if (md.params == null) {
            return sign.toString();
        } else {
            Iterator var2 = md.params.iterator();

            while(var2.hasNext()) {
                JCTree.JCVariableDecl param = (JCTree.JCVariableDecl)var2.next();
                String paramName = parseParamType(param.vartype);
                sign.add(paramName);
            }

            return sign.toString();
        }
    }

    private static String parseParamType(JCTree.JCExpression varType) {
        Symbol symbol = null;
        if (varType instanceof JCTree.JCIdent) {
            return ((JCTree.JCIdent)varType).name.toString();
        } else if (varType instanceof JCTree.JCFieldAccess) {
            return ((JCTree.JCFieldAccess)varType).name.toString();
        } else if (varType instanceof JCTree.JCArrayTypeTree) {
            JCTree.JCExpression type = ((JCTree.JCArrayTypeTree)varType).elemtype;
            return parseParamType(type) + "[]";
        } else {
            if (varType instanceof JCTree.JCTypeApply) {
                String type = ((JCTree.JCIdent)((JCTree.JCTypeApply)varType).clazz).sym.name.toString();
                if (((JCTree.JCTypeApply)varType).arguments.isEmpty()) {
                    System.err.println("无法解析类型: " + varType);
                    return varType.toString();
                }

                String listTypeName = "List";
                String mapTypeName = "Map";
                if (StringUtils.equalsIgnoreCase("List", type)) {
                    symbol = getSymbol(symbol, (JCTree.JCExpression)((JCTree.JCTypeApply)varType).arguments.get(0));
                    if (symbol == null) {
                        System.err.println("无法解析类型: " + varType);
                        return varType.toString();
                    }

                    return String.format("List<%s>", symbol.name.toString());
                }

                if (StringUtils.equalsIgnoreCase("Map", type)) {
                    symbol = getSymbol(symbol, (JCTree.JCExpression)((JCTree.JCTypeApply)varType).arguments.get(0));
                    Symbol symbol2 = getSymbol(symbol, (JCTree.JCExpression)((JCTree.JCTypeApply)varType).arguments.get(1));
                    if (symbol != null && symbol2 != null) {
                        return String.format("Map<%s, %s>", symbol.name.toString(), symbol2.name.toString());
                    }

                    System.err.println("无法解析类型: " + varType);
                    return varType.toString();
                }
            }

            return varType.toString();
        }
    }

    private static Symbol getSymbol(Symbol symbol, JCTree.JCExpression exp) {
        if (exp instanceof JCTree.JCIdent) {
            symbol = ((JCTree.JCIdent)exp).sym;
        } else if (exp instanceof JCTree.JCFieldAccess) {
            symbol = ((JCTree.JCFieldAccess)exp).sym;
        }

        return symbol;
    }

    public static String readFromFile(File file) {
        StringBuilder builder = new StringBuilder();
        if (file.exists()) {
            BufferedReader reader = null;

            try {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));

                for(String line = reader.readLine(); line != null; line = reader.readLine()) {
                    if (builder.length() > 0) {
                        builder.append("\n");
                    }

                    builder.append(line);
                }
            } catch (Exception var12) {
                Exception e = var12;
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException var11) {
                        IOException e = var11;
                        e.printStackTrace();
                    }
                }

            }
        }

        return builder.toString();
    }

    public static JsonNode pharseJson(String str) {
        try {
            return mapper.readTree(str);
        } catch (IOException var2) {
            IOException e = var2;
            System.err.println(String.format("解析json字符串%s失败", str));
            e.printStackTrace();
            return null;
        }
    }

    public static String getMemberValue(String name, JsonNode object) {
        JsonNode element = object.get(name);
        return element != null && !element.isNull() && !element.isMissingNode() ? element.asText() : "";
    }

    public static Integer getCharValue(Object obj) {
        if (obj == null) {
            return null;
        } else {
//            int real = false;

            int real;
            try {
                real = Integer.parseInt(obj.toString());
            } catch (Exception var3) {
                real = (Character)obj;
            }

            return real;
        }
    }

    public static boolean isSimpleType(String str) {
        boolean flag = true;
        switch (str.trim()) {
            default:
                flag = false;
            case "boolean":
            case "byte":
            case "char":
            case "short":
            case "int":
            case "long":
            case "float":
            case "double":
                return flag;
        }
    }

    static {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
}

