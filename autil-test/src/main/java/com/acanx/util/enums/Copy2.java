package com.acanx.util.enums;


import com.acanx.meta.model.test.annotation.model.MessageFlex;
import com.acanx.meta.model.test.annotation.model.MessageStable;
import com.acanx.util.incubator.annotation.ObjectCopier;

/**
 * CopyTest
 *
 * @author ACANX
 * @since 20251110
 */
public class Copy2 {



    @ObjectCopier
    private void flexToStable(MessageFlex flex, MessageStable stable) {};


    @ObjectCopier
    private void flex2ToStable(MessageFlex flex, MessageStable stable) {};



}
