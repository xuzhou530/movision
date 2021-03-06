package com.movision.utils;

import com.movision.facade.upload.UploadFacade;
import com.movision.utils.file.FileUtil;
import com.movision.utils.oss.MovisionOssClient;
import com.movision.utils.oss.UploadUtil;
import com.movision.utils.propertiesLoader.PropertiesLoader;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @Author zhurui
 * @Date 2017/6/10 13:15
 */
@Service
public class CoverImgCompressUtil {

    private static Logger log = LoggerFactory.getLogger(CoverImgCompressUtil.class);

    @Autowired
    private MovisionOssClient movisionOssClient;

    @Autowired
    private UploadFacade uploadFacade;


    public String ImgCompress(MultipartFile file, int w, int h) {
        Boolean compressFlag = false;
        String compress_dir_path = uploadFacade.getConfigVar("post.incise.domain");//压缩图片路径url
        List<String> existFileList = getExistFiles(compress_dir_path);//获取文件夹下的所有文件名

        //上传到本地服务器
        Map m = movisionOssClient.uploadMultipartFileObject(file, "img");

        String PATH = String.valueOf(m.get("url"));

        //获取图片名
        String filename = file.getOriginalFilename();

        //获取图片大小
        long size = file.getSize();

        //图片大于200Kb进行压缩
        if (size > 200 * 1024) {
            // 1 生成压缩后的图片的url
            String compress_file_path = compress_dir_path;
            log.info("压缩后的图片url，compress_file_path=" + compress_file_path);

            // 2 判断该文件夹下是否有同名的图片，若有则不处理，若没有则进行处理
            if (CollectionUtils.isEmpty(existFileList) || !existFileList.contains(filename)) {
                // 压缩核心算法
                compressFlag = compressJpgOrPng(w, h, compressFlag, filename, PATH, compress_file_path);
                // 处理过的图片加入到已处理集合，防止重复压缩图片
                existFileList.add(filename);
            } else {
                compressFlag = true;
                log.info("该图片已存在，不需要压缩，filename=" + filename);
            }
            if (compressFlag) {
                //压缩成功后返回图片压缩后的url
                return compress_file_path + filename;
            }
        } else {
            return PATH;
        }

        return null;
    }

    public String ImgCompress(String file, int w, int h) {
        Boolean compressFlag = false;
        String compress_dir_path = uploadFacade.getConfigVar("post.incise.domain");//压缩图片路径url
        List<String> existFileList = getExistFiles(compress_dir_path);//获取文件夹下的所有文件名

        String PATH = file;
        //String filename = FileUtil.getPicName(file);//获取图片文件名
        // 1 生成压缩后的图片的url
        String tempfilename = file.substring(file.lastIndexOf("/") + 1);//获取文件名称+后缀
        String compress_file_path = compress_dir_path + tempfilename;
        log.info("压缩后的图片url，compress_file_path=" + compress_file_path);

        // 2 判断该文件夹下是否有同名的图片，若有则不处理，若没有则进行处理
        if (CollectionUtils.isEmpty(existFileList) || !existFileList.contains(tempfilename)) {
            // 压缩核心算法
            compressFlag = compressJpgOrPng(w, h, compressFlag, tempfilename, PATH, compress_dir_path);
            // 处理过的图片加入到已处理集合，防止重复压缩图片
            existFileList.add(tempfilename);
        } else {
            compressFlag = true;
            log.info("该图片已存在，不需要压缩，filename=" + tempfilename);
        }
        if (compressFlag) {
            //压缩成功后返回图片压缩后的url
            return compress_file_path;
        }

        return null;
    }


    public boolean compressJpgOrPng(int w, int h, boolean compressFlag, String filename, String filePath, String tempDir) {
        if (filename.toLowerCase().endsWith(".jpg")) {

            log.info("压缩jpg图片，filepath=" + filePath);
            compressFlag = ImgCompressUtil.ImgCompress(filePath, tempDir, w, h);
        }
        if (filename.toLowerCase().endsWith(".png")) {
            log.info("压缩png图片，filepath=" + filePath);
            compressFlag = ImgCompressUtil.ImgCompress(filePath, tempDir, w, h);
        }
        if (filename.toLowerCase().endsWith(".jpeg")) {

            log.info("压缩jpeg图片，filepath=" + filePath);
            compressFlag = ImgCompressUtil.ImgCompress(filePath, tempDir, w, h);
        }
        if (filename.toLowerCase().endsWith(".bmp")) {

            log.info("压缩bmp图片，filepath=" + filePath);
            compressFlag = ImgCompressUtil.ImgCompress(filePath, tempDir, w, h);
        }
        return compressFlag;
    }


    /**
     * 获取指定文件夹下的文件 不分类型，即png,jpg都有
     *
     * @param compress_dir_path
     * @return
     */
    public List<String> getExistFiles(String compress_dir_path) {
        List<String> existFileList = new ArrayList<>();
        try {
            existFileList = FileUtil.readfileName(compress_dir_path, null);
            log.info("指定路径下的图片有：" + existFileList.toArray().toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return existFileList;
    }

}
