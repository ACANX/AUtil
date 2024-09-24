package com.acanx.utils.incubator.file;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OccupySize {

    private static final long BYTES_PER_KB = 1024L;
    private static final long BYTES_PER_MB = 1048576L;
    private static final long BYTES_PER_GB = 1073741824L;
    private static final long BYTES_PER_TB = 1099511627776L;
    private static final Pattern PATTERN = Pattern.compile("^(\\d+)(((B)|(KB)|(MB)))?$");
    private static final String ERROR_INPUT = "-1";
    private final long bytes;

    public OccupySize(long bytes) {
        this.bytes = bytes;
    }

    public static OccupySize ofB(long bytes) {
        return new OccupySize(bytes);
    }

    public static OccupySize ofKB(long kbytes) {
        return new OccupySize(Math.multiplyExact(1024L, kbytes));
    }

    public static OccupySize ofMB(long kbytes) {
        return new OccupySize(Math.multiplyExact(1048576L, kbytes));
    }

    public static OccupySize ofGB(long kbytes) {
        return new OccupySize(Math.multiplyExact(1073741824L, kbytes));
    }

    public static OccupySize ofTB(long kbytes) {
        return new OccupySize(Math.multiplyExact(1099511627776L, kbytes));
    }

    public long toBytes() {
        return this.bytes;
    }

    public static OccupySize of(long size, OccupyUnit unit) {
        if (unit == null) {
            throw new IllegalArgumentException("LogUnit单位不能为null");
        } else {
            return new OccupySize(Math.multiplyExact(size, unit.size().toBytes()));
        }
    }

    public static OccupySize resolve(CharSequence input) {
        if (input == null) {
            throw new IllegalArgumentException("input不能为null");
        } else if ("-1".equals(input)) {
            return new OccupySize(-1L);
        } else {
            Matcher matcher = PATTERN.matcher(input);
            if (!matcher.matches()) {
                throw new IllegalArgumentException("input不符合指定格式");
            } else {
                String appendix = matcher.group(2);
                OccupyUnit logUnit = OccupyUnit.B;
                if (appendix != null && !appendix.isEmpty()) {
                    logUnit = OccupyUnit.fromAppendix(appendix);
                }

                return of(Long.parseLong(matcher.group(1)), logUnit);
            }
        }
    }


}
