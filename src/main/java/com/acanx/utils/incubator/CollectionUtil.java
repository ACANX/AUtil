package com.acanx.utils.incubator;

import com.acanx.annotation.Alpha;

import java.util.Collection;


/**
 *
 * @since 0.0.1.10
 *
 */
@Alpha
public class CollectionUtil {

    /**
     * Null-safe check if the specified collection is empty.
     * <p>
     * Null returns true.
     * </p>
     *
     * @param coll  the collection to check, may be null
     * @return true if empty or null
     * @since 0.0.1.10
     */
    @Alpha
    public static boolean isEmpty(final Collection<?> coll) {
        return coll == null || coll.isEmpty();
    }


    /**
     * Null-safe check if the specified collection is not empty.
     * <p>
     * Null returns false.
     * </p>
     *
     * @param coll  the collection to check, may be null
     * @return true if non-null and non-empty
     * @since 0.0.1.10
     */
    @Alpha
    public static boolean isNotEmpty(final Collection<?> coll) {
        return !isEmpty(coll);
    }


}
