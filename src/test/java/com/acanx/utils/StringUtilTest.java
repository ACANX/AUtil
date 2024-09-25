package com.acanx.utils;

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
     * Method: generateRandomStr(int length)
     */
    @Test
    public void testGenerateRandomStr() throws Exception {

    }


    @Test
    public void getStrSeq() {
        for (int i = 0; i < 100; i++) {
            System.out.println(StringUtil.getStrSeq("-", i));
        }
    }

}
