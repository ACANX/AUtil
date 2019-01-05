package com.acanx.utils; 

import org.junit.Test; 
import org.junit.Before; 
import org.junit.After; 

/** 
* StringUtil Tester. 
* 
* @author <Authors name> 
* @since <pre>一月 5, 2019</pre> 
* @version 1.0 
*/ 
public class StringUtilTest { 

@Before
public void before() throws Exception { 
} 

@After
public void after() throws Exception { 
} 

/** 
* 
* Method: generateRandomStr(int length) 
* 
*/ 
@Test
public void testGenerateRandomStr() throws Exception {
    for (int i = 0; i <100 ; i++) {
        System.out.println(StringUtil.getRandomStr(i));
    }
}


    @Test
    public void getStrSeq() {
        for (int i = 0; i <100 ; i++) {
            System.out.println(StringUtil.getStrSeq("-",i));
        }
    }
}
