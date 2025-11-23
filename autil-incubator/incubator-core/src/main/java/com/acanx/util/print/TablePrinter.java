//package com.acanx.util.print;
//
//import com.acanx.annotation.Alpha;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * TablePrinter   将数组或者对象集合打印在表格中
// *
// * 要求界面使用等宽字体  例如Windows控制台  IDEA控制台显示存在问题，需要使用特定的支持中文、英文特殊符号等宽的字体，并且控制台适配
// *
// * @author ACANX
// * @since 20250814
// */
//@Alpha
//public class TablePrinter {
//    private final List<String[]> rows = new ArrayList<>();
//    private final List<Integer> columnWidths = new ArrayList<>();
//    private String[] headers;
//
//    public TablePrinter(String... headers) {
//        this.headers = headers;
//        for (String header : headers) {
//            columnWidths.add(getMaxWidth(header));
//        }
//    }
//
//    public void addRow(Object... data) {
//        if (data.length != headers.length) {
//            throw new IllegalArgumentException("列数不匹配");
//        }
//
//        String[] row = new String[data.length];
//        for (int i = 0; i < data.length; i++) {
//            row[i] = data[i] != null ? data[i].toString() : "";
//            // 更新列宽
//            int width = StringAlignUtil.getDisplayWidth(row[i]);
//            if (width > columnWidths.get(i)) {
//                columnWidths.set(i, width);
//            }
//        }
//        rows.add(row);
//    }
//
//    private int getMaxWidth(String str) {
//        return StringAlignUtil.getDisplayWidth(str);
//    }
//
//    public void print() {
//        printHeader();
//        printRows();
//    }
//
//    private void printHeader() {
//        printDivider();
//        printRow(headers, true);
//        printDivider();
//    }
//
//    private void printRows() {
//        for (String[] row : rows) {
//            printRow(row, false);
//        }
//        printDivider();
//    }
//
//    private void printRow(String[] row, boolean isHeader) {
//        StringBuilder sb = new StringBuilder("|");
//        for (int i = 0; i < row.length; i++) {
//            String aligned = StringAlignUtil.alignString(row[i], columnWidths.get(i), -1);
//            sb.append(" ").append(aligned).append(" |");
//        }
//        System.out.println(sb);
//    }
//
//    private void printDivider() {
//        StringBuilder sb = new StringBuilder("+");
//        for (int width : columnWidths) {
//            sb.append("-".repeat(width + 2)).append("+");
//        }
//        System.out.println(sb);
//    }
//
//
//}