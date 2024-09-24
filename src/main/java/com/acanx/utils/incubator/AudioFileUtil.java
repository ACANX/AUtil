package com.acanx.utils.incubator;

import com.acanx.annotation.Alpha;
import com.acanx.annotation.UnitTestsPassed;

import java.io.File;
import java.util.List;

/**
 *
 * @since 0.0.1.10
 */
@Alpha
public class AudioFileUtil {

    /**
     *  获取单个文件夹下的音乐文件列表
     *
     * @param dir 音频文件目录
     * @param list 存放文件列表的集合
     * @return
     * @since 0.0.1.10
     */
    @UnitTestsPassed
    @Alpha
    public static List<File> getAudioFileList(File dir, List<File> list) {
        for (File file : dir.listFiles()) {
            if (file.isFile() && (file.getName().toLowerCase().endsWith(".mp3")
                    || file.getName().toLowerCase().endsWith(".flac")
                    || file.getName().toLowerCase().endsWith(".wav")
                    || file.getName().toLowerCase().endsWith(".ape"))) {
                list.add(file);
                // System.out.println("Scan File:" + file.getAbsolutePath());
            }
            if (file.isDirectory()){
                getAudioFileList(file, list);
            }
        }
        return list;
    }

    /**
     *   获取多个文件夹下的音乐文件列表
     *
     *   重载方法，支持传入多个目录
     *
     * @param dirs 音频文件目录（集合）
     * @param list 存放文件列表的集合
     * @return
     * @since 0.0.1.10
     */
    @Alpha
    public static List<File> getAudioFileList(List<File> dirs, List<File> list) {
        for (File file : dirs) {
            List<File> audioFiles = getAudioFileList(file, list);
            list.addAll(audioFiles);
        }
        return list;
    }


}
