package com.acanx.util.print;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TablePrinterTest {

    @Test
   public void mainTest() {

        // 1. 简单对齐示例
        String[] names = {"张三", "李四", "王小明", "John Smith", "Tom"};
        String[] roles = {"工程师", "设计师", "产品经理", "Developer", "Tester"};
        int[] ages = {28, 32, 35, 30, 26};

        System.out.println("=== 简单对齐示例 ===");
        for (int i = 0; i < names.length; i++) {
            String nameAligned = StringAlignUtil.alignString(names[i], 12, -1);
            String roleAligned = StringAlignUtil.alignString(roles[i], 10, 1);
            String ageAligned = StringAlignUtil.centerString(String.valueOf(ages[i]), 6);
            System.out.println("|" + nameAligned + "|" + roleAligned + "|" + ageAligned + "|");
        }

        // 2. 表格打印示例
        System.out.println("\n=== 表格打印示例 ===");
        TablePrinter table = new TablePrinter("姓名", "职位", "年龄", "工资");

        table.addRow("张三", "高级工程师", 35, "¥28,500");
        table.addRow("李四", "UI设计师", 28, "¥22,000");
        table.addRow("王小明", "产品总监", 42, "¥45,800");
        table.addRow("John Smith", "Tech Lead", 38, "$12,500");
        table.addRow("Emily Chen", "测试工程师", 29, "¥18,500");
        table.addRow("欧阳修文", "架构师", 45, "¥52,000");

        table.print();
    }
}