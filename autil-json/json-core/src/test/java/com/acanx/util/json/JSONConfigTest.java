package com.acanx.util.json;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JSONConfig 配置对象测试（见 Docs/DevProposal/JsonFeatureProposal.md §8.1）
 *
 * <p>覆盖：增量覆盖语义（未设置项为 null）、builder 链式设置、Feature 开关、自定义扩展点、不可变性。</p>
 */
class JSONConfigTest {

    @Test
    void emptyConfigLeavesFieldsNull() {
        JSONConfig config = JSONConfig.builder().build();
        assertNull(config.getNaming());
        assertNull(config.getOutput());
        assertNull(config.getNullStrategy());
        assertNull(config.getDateFormat());
        assertNull(config.getFieldMapping());
        assertNull(config.getEnumStyle());
        assertNull(config.getUnknownFieldHandling());
        assertNull(config.getUnknownEnumValue());
        assertNull(config.getIndent());
    }

    @Test
    void builderChainedSettings() {
        JSONConfig config = JSONConfig.builder()
                .naming(NamingStyle.SNAKE_CASE)
                .output(OutputFormat.PRETTY, 4)
                .nullStrategy(NullStrategy.SKIP)
                .dateFormat("yyyy-MM-dd HH:mm:ss")
                .fieldMapping(FieldMapping.EXACT)
                .enumStyle(EnumStyle.TO_STRING)
                .unknownFieldHandling(UnknownFieldHandling.FAIL)
                .unknownEnumValue(UnknownEnumValue.DEFAULT)
                .build();
        assertEquals(NamingStyle.SNAKE_CASE, config.getNaming());
        assertEquals(OutputFormat.PRETTY, config.getOutput());
        assertEquals(4, config.getIndent());
        assertEquals(NullStrategy.SKIP, config.getNullStrategy());
        assertEquals("yyyy-MM-dd HH:mm:ss", config.getDateFormat());
        assertEquals(FieldMapping.EXACT, config.getFieldMapping());
        assertEquals(EnumStyle.TO_STRING, config.getEnumStyle());
        assertEquals(UnknownFieldHandling.FAIL, config.getUnknownFieldHandling());
        assertEquals(UnknownEnumValue.DEFAULT, config.getUnknownEnumValue());
    }

    @Test
    void outputSingleArgLeavesIndentUnset() {
        JSONConfig config = JSONConfig.builder().output(OutputFormat.PRETTY).build();
        assertEquals(OutputFormat.PRETTY, config.getOutput());
        assertNull(config.getIndent());
    }

    @Test
    void serializeFeatureToggle() {
        JSONConfig config = JSONConfig.builder()
                .enable(SerializeFeature.SORT_MAP_KEYS, SerializeFeature.ESCAPE_NON_ASCII)
                .disable(SerializeFeature.ESCAPE_NON_ASCII)
                .build();
        assertTrue(config.isSerializeEnabled(SerializeFeature.SORT_MAP_KEYS));
        assertFalse(config.isSerializeEnabled(SerializeFeature.ESCAPE_NON_ASCII));
        assertFalse(config.isSerializeEnabled(SerializeFeature.WRITE_CLASS_NAME));
    }

    @Test
    void deserializeFeatureToggle() {
        JSONConfig config = JSONConfig.builder()
                .enable(DeserializeFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
                .build();
        assertTrue(config.isDeserializeEnabled(DeserializeFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES));
        assertFalse(config.isDeserializeEnabled(DeserializeFeature.SUPPORT_AUTO_TYPE));
    }

    @Test
    void customFeatureExtensionPoint() {
        JSONConfig config = JSONConfig.builder()
                .customFeature("key1", "value1")
                .customFeature("key2", 42)
                .build();
        assertEquals("value1", config.getCustomFeature().get("key1"));
        assertEquals(42, config.getCustomFeature().get("key2"));
        assertFalse(config.getCustomFeature().isEmpty());
    }

    @Test
    void defaultDateFormatConstant() {
        assertEquals("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", JSONConfig.DEFAULT_DATE_FORMAT);
    }
}
