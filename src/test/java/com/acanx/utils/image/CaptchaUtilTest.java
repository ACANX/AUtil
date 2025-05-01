package com.acanx.utils.image;

import com.acanx.util.image.CaptchaUtil;
import com.acanx.util.FileUtil;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * CaptchaUtil Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>4月 5, 2020</pre>
 */
public class CaptchaUtilTest {

    /**
     * Method: generateCodeAndPic()
     */
    @Test
    public void generateCodeAndPicTest() throws Exception {
        String pathPrefix = FileUtil.getSysTempDir() + File.separator + "Captcha";
        Long totalCost = 0L;
        for (int i = 0; i < 10; i++) {
            //创建文件输出流对象
            Long start = System.currentTimeMillis();
            String path = pathPrefix + start + ".jpg";
            File file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            }
            OutputStream out = new FileOutputStream(file);
            Map<String, Object> map = CaptchaUtil.generateCodeAndPic();
            ImageIO.write((RenderedImage) map.get("codePic"), "jpeg", out);
            Long stop = System.currentTimeMillis();
            Long cost = (stop - start);
            System.out.println("Generate image file path: " + path);
            System.out.println("验证码的值为：" + map.get("code") + " 生成耗时：" + cost + "ms");
            totalCost = totalCost + cost;
        }
        System.out.println("平均耗时：" + (totalCost / 10) + "ms");
    }


} 
