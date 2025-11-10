package com.acanx.util.annotation;

import com.acanx.meta.model.test.annotation.model.MessageFlex;
import com.acanx.meta.model.test.annotation.model.MessageStable;
import com.acanx.util.incubator.annotation.ObjectCopier;

/**
 * CopyTest
 *
 * @author ACANX
 * @since 20251110
 */
public class CopyTest {



    @ObjectCopier
    private void convert(MessageFlex flex, MessageStable stable) {};


    @ObjectCopier(ignoreNull = true, exclude = {"internalId"})
    private void convert1(MessageFlex flex, MessageStable stable) {
        // 编译后这个方法会被增强为：
        // if (flex == null) throw new IllegalArgumentException("Source object cannot be null");
        // if (stable == null) throw new IllegalArgumentException("Target object cannot be null");
        // stable.content = flex.content != null ? flex.content : stable.content;
        // stable.priority = flex.priority != null ? flex.priority : stable.priority;
        // stable.urgent = flex.urgent != null ? flex.urgent : stable.urgent;
        // stable.tags = flex.tags != null ? flex.tags : stable.tags;
    }

    @ObjectCopier(strategy = ObjectCopier.CopyStrategy.DEEP)
    private void deepConvert(MessageFlex flex, MessageStable stable) {
        // 深拷贝版本的转换
    }

    public void processConversion(MessageFlex flex, MessageStable stable) {
        convert(flex, stable);
        // 其他业务逻辑
    }

}
