package com.acanx.util.json;

import com.acanx.annotation.Alpha;

/**
 * JSON 配置异常
 *
 * <p>用于 {@link NullStrategy#THROW} 等增强语义：配置要求的行为无法满足时抛出。
 * 继承 {@link RuntimeException}，调用方按需捕获。</p>
 *
 * @author ACANX
 * @since 1.3.0
 */
@Alpha
public class JSONConfigException extends RuntimeException {

    /**
     * 构造异常
     *
     * @param message 异常信息
     */
    public JSONConfigException(String message) {
        super(message);
    }

    /**
     * 构造异常
     *
     * @param message 异常信息
     * @param cause   根因
     */
    public JSONConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
