package com.acanx.util.file.filter;

import com.acanx.annotation.Alpha;

import java.io.File;
import java.io.FileFilter;


/**
 * ParameterizedFileFilter 通用文件过滤器
 *
 * @since 0.0.1.10
 */
@Alpha
public class ParameterizedFileFilter implements FileFilter {

   private String fileNameSuffix;

    /**
     *  构造函数
     *
     * @param fileNameSuffix  文件扩展名
     */
    public ParameterizedFileFilter(String fileNameSuffix) {
        this.fileNameSuffix = fileNameSuffix;
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
    public boolean accept(File pathname){
        return pathname.getName().toLowerCase().endsWith(this.fileNameSuffix);
    }



}
