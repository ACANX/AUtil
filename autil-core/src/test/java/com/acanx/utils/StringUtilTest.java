package com.acanx.utils;

import com.acanx.util.StringUtil;
import org.junit.jupiter.api.Test;

/** 
* StringUtil Tester. 
* 
* @author <Authors name> 
* @since <pre>一月 5, 2019</pre> 
* @version 1.0 
*/
public class StringUtilTest {
    /**
     *
     */
    @Test
    public void isEmptyTest(){
        System.out.println(StringUtil.isEmpty(" "));  // false
        System.out.println(StringUtil.isEmpty(""));   // true
        System.out.println(StringUtil.isEmpty(null)); // true
    }


    /**
     *
     */
    @Test
    public void isBlankTest(){
        System.out.println(StringUtil.isBlank(" "));  // true
        System.out.println(StringUtil.isBlank(""));   // true
        System.out.println(StringUtil.isBlank("             ")); // true
        System.out.println(StringUtil.isBlank(" 111    ")); // false
        System.out.println(StringUtil.isBlank(null)); // true
        System.out.println(StringUtil.isBlank("             121")); // false
        System.out.println(StringUtil.isBlank("121            ")); // false
    }

    /**
     * Method: generateRandomStr(int length)
     */
    @Test
    public void generateRandomStrTest() throws Exception {

    }


    /**
     *
     */
    @Test
    public void getStrSeqTest() {
        for (int i = 0; i < 100; i++) {
            System.out.println(StringUtil.getStrSeq("-", i));
        }
    }

    @Test
    void underlineToCamelCaseTest() {
        String str = "user_name";
        System.out.println(StringUtil.underlineToCamelCase(str));
    }

    @Test
    void camelCaseToUnderlineTest() {
        String str = "userName";
        System.out.println(StringUtil.camelToUnderlineCase(str));
        String str2 = "JSONName";
        System.out.println(StringUtil.camelToUnderlineCase(str2));
    }


    @Test
    void snakeToCamelCaseTest(){
        String line = "are_you_OK";
        // 下划线转驼峰（大驼峰）
        // 预期结果：AreYouOK
        String camel = StringUtil.snakeToCamelCase(line, true);
        System.out.println(camel);

        // 下划线转驼峰（小驼峰）
        // 预期结果：areYouOK
        camel = StringUtil.snakeToCamelCase(line);
        System.out.println(camel);

        // 驼峰转下划线
        // 预期结果：are_you_o_k
        System.out.println(StringUtil.camelToSnakeCase(camel));
    }
}
