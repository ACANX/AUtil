package com.acanx.util.json;

import com.acanx.meta.model.test.json.model.SecurityMetaData;
import com.alibaba.fastjson2.JSON;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FastJSON2UtilTest {


    @Test
    void toJSONStringDefault() {
        SecurityMetaData securityMetaData = new SecurityMetaData();
        securityMetaData.setSymbol("NVDA");
        securityMetaData.setSecurityName("英伟达");
        securityMetaData.setMarket("NSDQ");
        securityMetaData.setSecurityType("Stock");
        securityMetaData.setCurrPb(23.54);
        securityMetaData.setCurrPe(5.21);
        securityMetaData.setRegion("US");
        securityMetaData.setYearIpo(2005);
        securityMetaData.setCurrPrice(135.26);
        String jsonStr = JSON.toJSONString(securityMetaData);
        System.out.println(jsonStr);
    }

    @Test
    void toJSONString() {
        SecurityMetaData securityMetaData = new SecurityMetaData();
        securityMetaData.setSymbol("NVDA");
        securityMetaData.setSecurityName("英伟达");
        securityMetaData.setMarket("NSDQ");
        securityMetaData.setSecurityType("Stock");
        securityMetaData.setCurrPb(23.54);
        securityMetaData.setCurrPe(5.21);
        securityMetaData.setRegion("US");
        securityMetaData.setYearIpo(2005);
        securityMetaData.setCurrPrice(135.26);
        String jsonStr =FastJSON2Util.toJSONStringSnake(securityMetaData);
        System.out.println(jsonStr);
        String json = "{\"curr_pb\":23.54,\"curr_pe\":5.21,\"curr_price\":135.26,\"market\":\"NSDQ\",\"region\":\"US\",\"security_name\":\"英伟达\",\"security_type\":\"Stock\",\"symbol\":\"NVDA\",\"year_ipo\":2005}";
        System.out.println(json);
        Assertions.assertTrue(json.equals(jsonStr));
    }

    @Test
    void toJSONStringPrettyFormat() {
        SecurityMetaData securityMetaData = new SecurityMetaData();
        securityMetaData.setSymbol("NVDA");
        securityMetaData.setSecurityName("英伟达");
        securityMetaData.setMarket("NSDQ");
        securityMetaData.setSecurityType("Stock");
        securityMetaData.setCurrPb(23.54);
        securityMetaData.setCurrPe(5.21);
        securityMetaData.setRegion("US");
        securityMetaData.setYearIpo(2005);
        securityMetaData.setCurrPrice(135.26);
        String jsonStr =FastJSON2Util.toJSONStringPrettyFormat(securityMetaData);
        System.out.println(jsonStr);
        String json = "{\n" +
                "\t\"curr_pb\":23.54,\n" +
                "\t\"curr_pe\":5.21,\n" +
                "\t\"curr_price\":135.26,\n" +
                "\t\"market\":\"NSDQ\",\n" +
                "\t\"region\":\"US\",\n" +
                "\t\"security_name\":\"英伟达\",\n" +
                "\t\"security_type\":\"Stock\",\n" +
                "\t\"symbol\":\"NVDA\",\n" +
                "\t\"year_ipo\":2005\n" +
                "}";

    }

    @Test
    void toJSONStringForStorage() {
        SecurityMetaData securityMetaData = new SecurityMetaData();
        securityMetaData.setSymbol("NVDA");
        securityMetaData.setSecurityName("英伟达");
        securityMetaData.setMarket("NSDQ");
        securityMetaData.setSecurityType("Stock");
        securityMetaData.setCurrPb(23.54);
        securityMetaData.setCurrPe(5.21);
        securityMetaData.setRegion("US");
        securityMetaData.setYearIpo(2005);
        securityMetaData.setCurrPrice(135.26);
        String jsonStr =FastJSON2Util.toJSONStringForStorage(securityMetaData);
        System.out.println(jsonStr);
        String json = "{\"currPb\":23.54,\"currPe\":5.21,\"currPrice\":135.26,\"market\":\"NSDQ\",\"region\":\"US\",\"securityName\":\"英伟达\",\"securityType\":\"Stock\",\"symbol\":\"NVDA\",\"yearIpo\":2005}";
        Assertions.assertTrue(json.equals(jsonStr));
    }

    @Test
    void parseObject() {
        String json = "{\"currPb\":23.54,\"currPe\":5.21,\"currPrice\":135.26,\"market\":\"NSDQ\",\"region\":\"US\",\"securityName\":\"英伟达\",\"securityType\":\"Stock\",\"symbol\":\"NVDA\",\"yearIpo\":2005}";
        SecurityMetaData securityMetaData = FastJSON2Util.parseObject(json, SecurityMetaData.class);
        System.out.println(securityMetaData.toString());
    }

    @Test
    void parseArray() {
        String json = "[{\"currPb\":23.54,\"currPe\":5.21,\"currPrice\":135.26,\"market\":\"NSDQ\",\"region\":\"US\",\"securityName\":\"英伟达\",\"securityType\":\"Stock\",\"symbol\":\"NVDA\",\"yearIpo\":2005},{\"currPb\":43.54,\"currPe\":15.11,\"currPrice\":235.26,\"market\":\"NSDQ\",\"region\":\"US\",\"securityName\":\"苹果\",\"securityType\":\"Stock\",\"symbol\":\"APPL\",\"yearIpo\":1983}]";
        List<SecurityMetaData> list = FastJSON2Util.parseArray(json, SecurityMetaData.class);
        for (SecurityMetaData item : list) {
            System.out.println(item.toString());
        }
    }
}