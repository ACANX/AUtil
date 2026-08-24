package com.acanx.util.json;

import com.acanx.meta.model.test.json.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JacksonFileUtil 行为快照测试
 *
 * <p>文件级便利类快照（见 Docs/DevProposal/Jackson3Migration.md 阶段一）。
 * 注意：JacksonFileUtil 内部经 JSONUtil 门面调用全局 Provider
 * （本模块测试 classpath 携带 Jackson 3，auto 模式下为 Jackson3Provider）。</p>
 */
class JacksonFileUtilTest {

    private static final LocalDateTime CREATE_TIME = LocalDateTime.of(2023, 1, 1, 12, 0, 0, 123_456_000);

    @TempDir
    Path tempDir;

    private List<User> buildUsers() {
        List<User> users = new ArrayList<>();
        users.add(new User(11, "Alice", CREATE_TIME));
        users.add(new User(12, "Bob", CREATE_TIME.plusHours(1)));
        return users;
    }

    @Test
    void writeArrayToLocalFile() throws IOException {
        // 数组数据写入文件（美化输出）
        Path file = tempDir.resolve("users.json");
        JacksonFileUtil.writeArrayToLocalFile(file.toString(), buildUsers());
        assertTrue(Files.exists(file), "写入的文件应存在");
        String content = Files.readString(file, StandardCharsets.UTF_8);
        assertTrue(content.contains("user_id"), "输出应为下划线风格");
        assertTrue(content.contains("Alice"));
        assertTrue(content.contains("\n"), "输出应为美化格式");
    }

    @Test
    void parseArrayFromFile() throws IOException {
        // 将本地 JSON 文件反序列化为集合
        String json = "[{\"user_id\":11,\"user_name\":\"Alice\",\"create_time\":\"2023-01-01T12:00:00.123456\"},"
                + "{\"user_id\":12,\"user_name\":\"Bob\",\"create_time\":\"2023-01-01T13:00:00.123456\"}]";
        Path file = tempDir.resolve("list.json");
        Files.writeString(file, json, StandardCharsets.UTF_8);
        List<User> users = JacksonFileUtil.parseArray(file.toString(), User.class);
        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals("Alice", users.get(0).getUserName());
        assertEquals(CREATE_TIME, users.get(0).getCreateTime());
    }

    @Test
    void listToMap() {
        // 通用 List 转 Map（按 key 提取器）
        List<User> users = buildUsers();
        Map<Integer, User> map = JacksonFileUtil.listToMap(users, User::getUserId);
        assertEquals(2, map.size());
        assertEquals("Alice", map.get(11).getUserName());
        assertEquals("Bob", map.get(12).getUserName());
    }

    @Test
    void listToMapNullKeySkipped() {
        // key 提取器返回 null 的元素应被跳过
        List<User> users = new ArrayList<>();
        users.add(new User(null, "NoId", CREATE_TIME));
        users.add(new User(12, "Bob", CREATE_TIME));
        Map<Integer, User> map = JacksonFileUtil.listToMap(users, User::getUserId);
        assertEquals(1, map.size());
        assertEquals("Bob", map.get(12).getUserName());
    }

    @Test
    void listToMapNullList() {
        // null 入参返回空 Map
        Map<Integer, User> map = JacksonFileUtil.listToMap(null, User::getUserId);
        assertNotNull(map);
        assertTrue(map.isEmpty());
    }

    @Test
    void parseArrayToMap() throws IOException {
        // JSON 文件数组 → HashMap（按 key 提取器）
        String json = "[{\"user_id\":11,\"user_name\":\"Alice\",\"create_time\":\"2023-01-01T12:00:00.123456\"},"
                + "{\"user_id\":12,\"user_name\":\"Bob\",\"create_time\":\"2023-01-01T13:00:00.123456\"}]";
        Path file = tempDir.resolve("map.json");
        Files.writeString(file, json, StandardCharsets.UTF_8);
        Map<Integer, User> map = JacksonFileUtil.parseArrayToMap(file.toString(), User.class, User::getUserId);
        assertEquals(2, map.size());
        assertEquals("Alice", map.get(11).getUserName());
        assertEquals("Bob", map.get(12).getUserName());
    }

    @Test
    void findJsonFilesInDirectory() throws IOException {
        // 递归查找目录下的 JSON 文件
        Path sub = Files.createDirectories(tempDir.resolve("sub"));
        Path a = tempDir.resolve("a.json");
        Path b = sub.resolve("b.json");
        Path c = tempDir.resolve("c.txt");
        Files.writeString(a, "{}");
        Files.writeString(b, "{}");
        Files.writeString(c, "not json");
        List<String> jsonFiles = new ArrayList<>();
        JacksonFileUtil.findJsonFilesInDirectory(tempDir.toFile(), tempDir.toString(), jsonFiles);
        assertEquals(2, jsonFiles.size());
        assertTrue(jsonFiles.stream().anyMatch(f -> f.endsWith("a.json")));
        assertTrue(jsonFiles.stream().anyMatch(f -> f.endsWith("sub/b.json")));
    }

    @Test
    void writeContentToLocalTempFile() {
        // 写入本地临时文件并返回 File
        File file = JacksonFileUtil.writeContentToLocalTempFile("snapshot", buildUsers());
        assertNotNull(file);
        assertTrue(file.exists());
        assertTrue(file.getName().startsWith("TempFilesnapshot"));
        assertTrue(file.getName().endsWith(".json"));
        file.delete();
    }
}
