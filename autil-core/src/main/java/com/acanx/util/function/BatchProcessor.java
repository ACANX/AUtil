package com.acanx.util.function;

import java.util.List;
import java.util.function.Consumer;

/**
 * BatchProcessor
 *
 * @author ACANX
 * @since 20251107
 */
public class BatchProcessor {

    /**
     * 分批处理列表
     *
     * @param list 待处理的列表
     * @param batchSize 每批大小
     * @param consumer 处理函数
     */
    public static <T> void process(List<T> list, int batchSize, Consumer<List<T>> consumer) {
        if (list == null || list.isEmpty()) return;
        for (int i = 0; i < list.size(); i += batchSize) {
            int end = Math.min(list.size(), i + batchSize);
            List<T> batch = list.subList(i, end);
            consumer.accept(batch);
        }
    }


}
