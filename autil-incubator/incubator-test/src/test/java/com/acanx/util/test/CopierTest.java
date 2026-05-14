package com.acanx.util.test;

import com.acanx.util.test.model.CopierMethods;
import com.acanx.util.test.model.UserDTO;
import com.acanx.util.test.model.UserVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @Copier 注解集成测试
 * 验证编译期注解处理器是否正确生成了拷贝代码。
 * <p>
 * 这些测试直接调用被注解的方法，如果注解处理器工作正常，
 * 方法体应该已经被替换为自动生成的拷贝逻辑。
 */
public class CopierTest {

    private CopierMethods copierMethods;
    private UserDTO sourceDTO;

    @BeforeEach
    void setUp() {
        copierMethods = new CopierMethods();
        List<String> roles = Arrays.asList("admin", "user");
        sourceDTO = new UserDTO("张三", 25, true, "zhangsan@example.com", "secret123", 1001L, roles);
    }

    @Test
    void testCopyBasic() {
        UserVO targetVO = new UserVO();
        copierMethods.copyBasic(sourceDTO, targetVO);

        assertEquals("张三", targetVO.getName());
        assertEquals(25, targetVO.getAge());
        assertTrue(targetVO.isActive());
        assertEquals("zhangsan@example.com", targetVO.getEmail());
        assertEquals("secret123", targetVO.getPassword());
        assertEquals(1001L, targetVO.getId());
        assertNotNull(targetVO.getRoles());
        assertEquals(2, targetVO.getRoles().size());
    }

    @Test
    void testCopyBasicNullSource() {
        UserVO targetVO = new UserVO();
        targetVO.setName("original");
        // source 为 null 时不应抛出异常，target 保持原值
        copierMethods.copyBasic(null, targetVO);
        assertEquals("original", targetVO.getName());
    }

    @Test
    void testCopyBasicNullTarget() {
        // target 为 null 时不应抛出异常
        assertDoesNotThrow(() -> copierMethods.copyBasic(sourceDTO, null));
    }

    @Test
    void testCopyIgnoreNull() {
        UserVO targetVO = new UserVO();
        targetVO.setName("existingName");
        targetVO.setPassword("existingPassword");

        // source 中 name 有值，password 也有值
        copierMethods.copyIgnoreNull(sourceDTO, targetVO);
        assertEquals("张三", targetVO.getName());
        assertEquals("secret123", targetVO.getPassword());
    }

    @Test
    void testCopyIgnoreNullWithNullSourceField() {
        UserDTO dtoWithNull = new UserDTO();
        dtoWithNull.setAge(30);
        // name 为 null

        UserVO targetVO = new UserVO();
        targetVO.setName("保留的名字");

        copierMethods.copyIgnoreNull(dtoWithNull, targetVO);
        // name 为 null，不应覆盖目标的值
        assertEquals("保留的名字", targetVO.getName());
        // age 有值，应该被拷贝
        assertEquals(30, targetVO.getAge());
    }

    @Test
    void testCopyExcludePassword() {
        UserVO targetVO = new UserVO();
        copierMethods.copyExcludePassword(sourceDTO, targetVO);

        assertEquals("张三", targetVO.getName());
        assertEquals(25, targetVO.getAge());
        // password 应被排除，不拷贝
        assertNull(targetVO.getPassword());
    }

    @Test
    void testCopyNameAndAge() {
        UserVO targetVO = new UserVO();
        copierMethods.copyNameAndAge(sourceDTO, targetVO);

        // 只拷贝 name 和 age
        assertEquals("张三", targetVO.getName());
        assertEquals(25, targetVO.getAge());
        // 其他字段不应被拷贝
        assertNull(targetVO.getEmail());
        assertNull(targetVO.getPassword());
        assertNull(targetVO.getId()); // Long 包装类型默认值为 null
    }

    @Test
    void testCopyIgnoreNullExclude() {
        UserVO targetVO = new UserVO();
        copierMethods.copyIgnoreNullExclude(sourceDTO, targetVO);

        assertEquals("张三", targetVO.getName());
        assertEquals(25, targetVO.getAge());
        assertEquals("zhangsan@example.com", targetVO.getEmail());
        // password 和 id 被排除
        assertNull(targetVO.getPassword());
        assertNull(targetVO.getId());
    }

    @Test
    void testCopyShallow() {
        UserVO targetVO = new UserVO();
        copierMethods.copyShallow(sourceDTO, targetVO);

        assertEquals("张三", targetVO.getName());
        assertEquals(25, targetVO.getAge());
        assertTrue(targetVO.isActive());
    }
}
