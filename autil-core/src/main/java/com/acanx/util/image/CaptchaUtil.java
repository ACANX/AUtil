package com.acanx.util.image;

import com.acanx.constant.Constant;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 *   <p>CAPTCHAUtil.java</p>
 *
 * 文件由 ACANXX 创建于 2020/4/5 - 18:48
 * Description  CAPTCHAUtil:
 * 补充说明：
 * 验证码（CAPTCHA）
 * 是“Completely Automated Public Turing test to tell Computers and Humans Apart”
 * （全自动区分计算机和人类的图灵测试）的缩写，是一种区分用户是计算机还是人的公共全自动程序。可以防止：恶意破解密码、刷票、论坛灌水，
 * 有效防止某个黑客对某一个特定注册用户用特定程序暴力破解方式进行不断的登陆尝试，实际上用验证码是现在很多网站通行的方式，
 * 我们利用比较简易的方式实现了这个功能。这个问题可以由计算机生成并评判，但是必须只有人类才能解答。
 * 由于计算机无法解答CAPTCHA的问题，所以回答出问题的用户就可以被认为是人类。
 * <p>
 * 实现参考：
 * <a href="https://www.cnblogs.com/nanyangke-cjz/p/7049281.html">实现参考</a>
 *        2020/4/5  18:48
 *
 * @author ACANXX
 * @version 0.2.0.7
 * @since 0.0.1.8
 */
public class CaptchaUtil {

    /**
     * 定义图片的width
     */
    private static final int width = 200;

    /**
     * 定义图片的height
     */
    private static final int height = 50;

    /**
     * 定义图片上显示验证码的个数
     */
    private static final int codeCount = 6;


    /**
     * 生成字符水平间距
     */
    private static final int xx = 28;

    /**
     * 生成字符的高度
     */
    private static final int fontHeight = 48;

    /**
     * 生成是字符与顶部的距离
     */
    private static final int codeY = 45;
    private static final char[] codeSequence = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
            'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    /**
     * 构造函数
     * @hidden
     */
    private CaptchaUtil() {
    }

    /**
     * 生成一个map集合
     * code为生成的验证码
     * codePic为生成的验证码BufferedImage对象
     *
     * @return 返回包含code码及captcha图片的 {@link HashMap}
     */
    public static Map<String, Object> generateCodeAndPic() {
        // 定义图像buffer
        BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        // Graphics2D gd = buffImg.createGraphics();
        // Graphics2D gd = (Graphics2D) buffImg.getGraphics();
        Graphics gd = buffImg.getGraphics();
        // 创建一个随机数生成器类
        Random random = new Random();
        // 将图像填充为白色
        gd.setColor(Color.WHITE);
        gd.fillRect(Constant.INT_0, Constant.INT_0, width, height);

        // 创建字体，字体的大小应该根据图片的高度来定。
        Font font = new Font("Fixedsys", Font.BOLD, fontHeight);
        // 设置字体。
        gd.setFont(font);

        // 画边框。
        gd.setColor(Color.BLACK);
        gd.drawRect(Constant.INT_0, Constant.INT_0, width - Constant.INT_1, height - Constant.INT_1);

        // 随机产生80条干扰线，使图象中的认证码不易被其它程序探测到。
        gd.setColor(Color.BLACK);
        for (int i = Constant.INT_0; i < Constant.INT_80; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int xl = random.nextInt(Constant.INT_82);
            int yl = random.nextInt(Constant.INT_82);
            int x2, y2;
            if (x % Constant.INT_2 == Constant.INT_1) {
                x2 = x + xl;
            } else {
                x2 = x - xl;
            }
            if (y % Constant.INT_2 == Constant.INT_1) {
                y2 = y + yl;
            } else {
                y2 = x - yl;
            }
            gd.drawLine(x, y, x2, y2);
        }

        // randomCode用于保存随机产生的验证码，以便用户登录后进行验证。
        StringBuffer randomCode = new StringBuffer();
        int red = Constant.INT_0, green = Constant.INT_0, blue = Constant.INT_0;

        // 随机产生codeCount数字的验证码。
        for (int i = Constant.INT_0; i < codeCount; i++) {
            // 得到随机产生的验证码数字。
            String code = String.valueOf(codeSequence[random.nextInt(Constant.INT_36)]);
            // 产生随机的颜色分量来构造颜色值，这样输出的每位数字的颜色值都将不同。
            red = random.nextInt(Constant.INT_255);
            green = random.nextInt(Constant.INT_255);
            blue = random.nextInt(Constant.INT_255);

            // 用随机产生的颜色将验证码绘制到图像中。
            gd.setColor(new Color(red, green, blue));
            gd.drawString(code, Constant.INT_15 + i * xx, codeY);

            // 将产生的四个随机数组合在一起。
            randomCode.append(code);
        }
        Map<String, Object> map = new HashMap<String, Object>();
        //存放验证码
        map.put("code", randomCode);
        //存放生成的验证码BufferedImage对象
        map.put("codePic", buffImg);
        return map;
    }


}
