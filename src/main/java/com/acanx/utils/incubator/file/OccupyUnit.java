package com.acanx.utils.incubator.file;

public enum OccupyUnit {

    B("B",OccupySize.ofB(1L)),
    KB("KB", OccupySize.ofKB(1L)),
    MB("MB", OccupySize.ofMB(1L)),
    GB("GB", OccupySize.ofGB(1L)),
    TB("TB", OccupySize.ofTB(1L));

    private final String appendix;
    private final OccupySize size;

    private OccupyUnit(String appendix, OccupySize size) {
        this.appendix = appendix;
        this.size = size;
    }

    OccupySize size() {
        return this.size;
    }

    public static OccupyUnit fromAppendix(String appendix) {
        try {
            OccupyUnit unit = valueOf(appendix.toUpperCase());
            return unit;
        } catch (Exception var2) {
            throw new IllegalArgumentException("不支持的参数后缀'" + appendix + "'");
        }
    }
}
