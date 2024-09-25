package com.acanx.utils.file.filter;

import com.acanx.annotation.Alpha;
import com.acanx.constant.FileConstant;

import java.io.File;
import java.io.FileFilter;

/**
 *  PropertiesFileFilter Properties文件过滤器
 *
 * @since 0.0.1.10
 */
@Alpha
public class PropertiesFileFilter implements FileFilter {

    /**
     * 构造函数
     * @hidden
     */
    private PropertiesFileFilter() {
    }

    /**
     * Tests whether or not the specified abstract pathname should be
     * included in a pathname list.
     *
     * @param pathname The abstract pathname to be tested
     * @return {@code true} if and only if {@code pathname}
     * should be included
     */
    @Override
    public boolean accept(File pathname) {
        return pathname.getName().toLowerCase().endsWith(FileConstant.DOT_PROPERTIES);
    }

}
