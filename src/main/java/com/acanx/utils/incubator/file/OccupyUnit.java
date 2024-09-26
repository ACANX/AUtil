package com.acanx.utils.incubator.file;

/**
 *  OccupyUnit枚举
 *
 * @since 0.0.1.10
 */
public enum OccupyUnit {

    /** B */
    B("B",OccupySize.ofB(1L)),
    /** KB */
    KB("KB", OccupySize.ofKB(1L)),
    /** MB */
    MB("MB", OccupySize.ofMB(1L)),
    /** GB */
    GB("GB", OccupySize.ofGB(1L)),
    /** TB */
    TB("TB", OccupySize.ofTB(1L));

    private final String appendix;
    private final OccupySize size;

    /**
     *  构造方法
     *
     * @param appendix appendix
     * @param size size
     */
    private OccupyUnit(String appendix, OccupySize size) {
        this.appendix = appendix;
        this.size = size;
    }

    /**
     *  size
     *
     * @return OccupySize
     */
    OccupySize size() {
        return this.size;
    }

    /**
     *  fromAppendix
     *
     * @param appendix appendix
     * @return OccupyUnit
     */
    public static OccupyUnit fromAppendix(String appendix) {
        try {
            OccupyUnit unit = valueOf(appendix.toUpperCase());
            return unit;
        } catch (Exception var2) {
            throw new IllegalArgumentException("不支持的参数后缀'" + appendix + "'");
        }
    }
}
