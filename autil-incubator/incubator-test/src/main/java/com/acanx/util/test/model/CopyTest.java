package com.acanx.util.test.model;

import java.util.Arrays;
import java.util.List;

/**
 * CopyTest
 *
 * @author ACANX
 * @since 20260514
 */
public class CopyTest {

    public static void main(String[] args) {
        System.out.println("Hello World!");

        CopierMethods copierMethods = new CopierMethods();
        // 原始对象
        List<String> roles = Arrays.asList("admin", "user");
        UserDTO sourceDTO = new UserDTO(null, 25, true, "zhangsan@example.com", "secret123", 1001L, roles);
        // 目标对象
        UserVO targetVO = new UserVO();
        // 复制
        copierMethods.copyBasic(sourceDTO, targetVO);
        System.out.println("Source DTO: " + sourceDTO.toString());
        System.out.println("Target  VO: " + targetVO.toString());
    }
}
