package com.acanx.util.properties;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OrderedProperties {

    private final List<Entry> entries = new ArrayList<>();
    private final Map<String, Entry> propertyMap = new HashMap<>();


    /**
     *   加载.properties配置文件
     * @param file
     * @throws IOException
     */
    public void load(File file) throws IOException {
        load(new FileInputStream(file));
    }

    /**
     *   加载.properties配置文件
     * @param in
     * @throws IOException
     */
    public void load(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        String line;
        while ((line = reader.readLine()) != null) {
            String trimmed = line.trim();
            if (trimmed.startsWith("#") || trimmed.startsWith("!")) {
                entries.add(new Entry(Entry.Type.COMMENT, line));
            } else if (trimmed.isEmpty()) {
                entries.add(new Entry(Entry.Type.BLANK, line));
            } else {
                parsePropertyLine(line);
            }
        }
    }


    /**
     * 获取属性值的方法
     * @param key
     * @return
     */
    public String getProperty(String key) {
        Entry entry = propertyMap.get(key);
        return (entry != null) ? entry.getValue() : null;
    }

    /**
     * 获取所有键（可选功能）
     *
     * @return
     */
    public List<String> getKeys() {
        List<String> keys = new ArrayList<>();
        for (Entry entry : entries) {
            if (entry.getType() == Entry.Type.PROPERTY) {
                keys.add(entry.getKey());
            }
        }
        return keys;
    }


    private void parsePropertyLine(String line) {
        int sepIdx = -1;
        // 查找第一个分隔符（=或:）
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '=' || c == ':') {
                sepIdx = i;
                break;
            }
        }
        if (sepIdx == -1) { // 无分隔符视为键
            addProperty(line.trim(), "", "=");
        } else {
            String keyPart = line.substring(0, sepIdx);
            String sep = line.substring(sepIdx, sepIdx + 1);
            String valuePart = line.substring(sepIdx + 1);
            // 提取分隔符周围空格
            int keyEnd = sepIdx;
            while (keyEnd > 0 && Character.isWhitespace(line.charAt(keyEnd - 1))) keyEnd--;
            int valStart = sepIdx + 1;
            while (valStart < line.length() && Character.isWhitespace(line.charAt(valStart))) valStart++;
            String separator = line.substring(keyEnd, valStart);
            addProperty(keyPart.trim(), valuePart.trim(), separator);
        }
    }


    private void addProperty(String key, String value, String separator) {
        Entry entry = new Entry(key, value, separator);
        entries.add(entry);
        propertyMap.put(key, entry);
    }


    public void setProperty(String key, String value) {
        if (propertyMap.containsKey(key)) {
            propertyMap.get(key).setValue(value);
        } else {
            addProperty(key, value, "="); // 新增属性默认用=分隔
        }
    }


    public void store(OutputStream out) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.ISO_8859_1));
        for (Entry entry : entries) {
            writer.write(entry.toString());
            writer.newLine();
        }
        writer.flush();
    }



    /**
     * 将属性按键排序后写入文件，非属性条目（注释、空行）保留原始顺序
     */
    public void resortAndStore(File propFile) throws IOException {
        // 分离非属性条目和属性条目
        List<Entry> nonPropertyEntries = entries.stream()
                .filter(e -> e.getType() != Entry.Type.PROPERTY)
                .collect(Collectors.toList());
        List<Entry> sortedProperties = entries.stream()
                .filter(e -> e.getType() == Entry.Type.PROPERTY)
                .sorted(Comparator.comparing(Entry::getKey))
                .collect(Collectors.toList());

        // 合并条目：非属性在前，排序后的属性在后
        List<Entry> sortedEntries = new ArrayList<>();
        sortedEntries.addAll(nonPropertyEntries);
        sortedEntries.addAll(sortedProperties);

        // 写入文件
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(propFile), StandardCharsets.UTF_8));
        for (Entry entry : sortedEntries) {
            writer.write(entry.toString());
            writer.newLine();
        }
        writer.flush();
    }

}
