package com.movision.facade.h5wechat;

import com.movision.utils.oss.MovisionOssClient;
import com.movision.utils.propertiesLoader.PropertiesLoader;
import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageDecoder;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author shuxf
 * @Date 2017/8/18 15:48
 */
@Service
public class WechatH5Facade {

    @Autowired
    private MovisionOssClient movisionOssClient;
    public Map<String, Object> imgCompose(String manname, String womanname, int type) {
        Map<String, Object> map = new HashMap<>();
//        public static void exportImg1(){
//            int width = 100;
//            int height = 100;
//            String s = "你好";
//
//            File file = new File("d:/image.jpg");
//
//            Font font = new Font("Serif", Font.BOLD, 10);
//            BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//            Graphics2D g2 = (Graphics2D)bi.getGraphics();
//            g2.setBackground(Color.WHITE);
//            g2.clearRect(0, 0, width, height);
//            g2.setPaint(Color.RED);
//
//            FontRenderContext context = g2.getFontRenderContext();
//            Rectangle2D bounds = font.getStringBounds(s, context);
//            double x = (width - bounds.getWidth()) / 2;
//            double y = (height - bounds.getHeight()) / 2;
//            double ascent = -bounds.getY();
//            double baseY = y + ascent;
//
//            g2.drawString(s, (int)x, (int)baseY);
//
//            try {
//                ImageIO.write(bi, "jpg", file);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

//        (String username,String headImg)
        if (type == 1) {//结婚证
            try {
                //下面是模板图片的路径
                String timgurl = PropertiesLoader.getValue("wechat.h5.domain");
                InputStream is = new FileInputStream(timgurl);
                String newurl = PropertiesLoader.getValue("wechat.newh5.domain");

                //通过JPEG图象流创建JPEG数据流解码器
                JPEGImageDecoder jpegDecoder = JPEGCodec.createJPEGDecoder(is);
                //解码当前JPEG数据流，返回BufferedImage对象
                BufferedImage buffImg = jpegDecoder.decodeAsBufferedImage();
                //得到画笔对象
                //Graphics g = buffImg.getGraphics();
                Graphics2D g = (Graphics2D) buffImg.getGraphics();
                //创建你要附加的图象。//-----------------------------------------------这一段是将小图片合成到大图片上的代码
                //小图片的路径
//                ImageIcon imgIcon = new ImageIcon(headImg);
                //得到Image对象。
//                Image img = imgIcon.getImage();
                //将小图片绘到大图片上。
                //5,300 .表示你的小图片在大图片上的位置。
//                g.drawImage(img,400,15,null);

                //设置颜色。
                g.setColor(Color.BLACK);

                //最后一个参数用来设置字体的大小
                Font f = new Font("宋体", Font.PLAIN, 25);
                Color mycolor = Color.red;//new Color(0, 0, 255);
                g.setColor(mycolor);
                g.setFont(f);

                //10,20 表示这段文字在图片上的位置(x,y) .第一个是你设置的内容。
                g.drawString(manname, 160, 610);//合成男的名字
                g.drawString(womanname, 160, 720);//合成女的名字

                g.dispose();

                //OutputStream os;

                //os = new FileOutputStream("d:/union.jpg");
                String shareFileName = System.currentTimeMillis() + ".jpg";

                map.put("status", 200);
                map.put("url", shareFileName);
                String url = newurl + shareFileName;
                //  os = new FileOutputStream(shareFileName);
                //创键编码器，用于编码内存中的图象数据。
                //JPEGImageEncoder en = JPEGCodec.createJPEGEncoder(os);
                // en.encode(buffImg);
                ImageIO.write(buffImg, "png", new File(url));//图片的输出路径
                map.put("newurl", url);
                is.close();
                //  os.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (ImageFormatException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (type == 2) {//离婚

        }
        return map;
    }
}