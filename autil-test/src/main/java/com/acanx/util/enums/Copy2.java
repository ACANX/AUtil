package com.acanx.util.enums;


import com.acanx.meta.model.test.annotation.model.MessageFlex;
import com.acanx.meta.model.test.annotation.model.MessageStable;
import com.acanx.util.incubator.annotation.Copier;

/**
 * CopyTest
 *
 * @author ACANX
 * @since 20251110
 */
public class Copy2 {



    @Copier
    private void flexToStable(MessageFlex flex, MessageStable stable) {};


    @Copier
    private void flex2ToStable(MessageFlex flex, MessageStable stable) {};



}
