package com.acanx.util.print;

import com.acanx.annotation.Alpha;

import java.util.ArrayList;
import java.util.List;

/**
 * TablePrinter   将数组或者对象集合打印在表格中
 *
 * 要求界面使用等宽字体  例如Windows控制台  IDEA控制台显示存在问题，需要使用特定的支持中文、英文特殊符号等宽的字体，并且控制台适配
 *
 * @author ACANX
 * @since 20250814
 */
@Alpha
public class TablePrinter {
    private final List<String[]> rows = new ArrayList<>();
    private final List<Integer> columnWidths = new ArrayList<>();
    private String[] headers;

    /**
     * 构造一个TablePrinter对象
     * @param headers 表格的列标题，可变参数形式传入
     */
    public TablePrinter(String... headers) {
        this.headers = headers;
        for (String header : headers) {
            columnWidths.add(getMaxWidth(header));
        }
    }

    /**
     * 向表格中添加一行数据
     *
     * @param data 可变参数，表示要添加到新行中的数据项数组
     */
    public void addRow(Object... data) {
        if (data.length != headers.length) {
            throw new IllegalArgumentException("列数不匹配");
        }

        String[] row = new String[data.length];
        for (int i = 0; i < data.length; i++) {
            row[i] = data[i] != null ? data[i].toString() : "";
            // 更新列宽
            int width = StringAlignUtil.getDisplayWidth(row[i]);
            if (width > columnWidths.get(i)) {
                columnWidths.set(i, width);
            }
        }
        rows.add(row);
    }

        /**
     * 获取字符串的最大显示宽度
     * @param str 要计算宽度的字符串
     * @return 字符串的显示宽度
     */
    private int getMaxWidth(String str) {
        return StringAlignUtil.getDisplayWidth(str);
    }

    /**
     * 打印表格内容
     */
    public void print() {
        printHeader();
        printRows();
    }

    /**
     * 打印表格头部信息
     */
    private void printHeader() {
        // 打印表格头部
        printDivider();
        printRow(headers, true);
        printDivider();
    }


       /**
     * 打印所有数据行
     * 遍历rows集合中的每一行数据，调用printRow方法打印，并在最后打印分隔线
     */
    private void printRows() {
        // 遍历并打印每一行数据
        for (String[] row : rows) {
            printRow(row, false);
        }
        // 打印底部分隔线
        printDivider();
    }

    /**
     * 打印单行数据
     * @param row 要打印的行数据数组
     * @param isHeader 标识是否为表头行
     */
    private void printRow(String[] row, boolean isHeader) {
        // 构建行字符串，包含每列数据和分隔符
        StringBuilder sb = new StringBuilder("|");
        for (int i = 0; i < row.length; i++) {
            // 对字符串进行对齐处理后添加到行中
            String aligned = StringAlignUtil.alignString(row[i], columnWidths.get(i), -1);
            sb.append(" ").append(aligned).append(" |");
        }
        System.out.println(sb);
    }

    /**
     * 打印表格分隔线
     * 根据各列宽度生成相应的分隔线并输出
     */
    private void printDivider() {
        // 构建分隔线字符串
        StringBuilder sb = new StringBuilder("+");
        for (int width : columnWidths) {
            // 根据列宽重复添加横线字符
            sb.append("-".repeat(width + 2)).append("+");
        }
        System.out.println(sb);
    }



}