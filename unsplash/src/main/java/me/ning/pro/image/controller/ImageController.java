package me.ning.pro.image.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import me.ning.pro.common.dto.OutputDTO;
import me.ning.pro.image.entity.Image;
import me.ning.pro.image.service.ImageService;
import me.ning.pro.image.service.TodayService;
import me.ning.pro.util.http.ResponseUtil;
import me.ning.pro.util.img.ImageUtil;
import me.ning.pro.util.url.URLUtil;
import me.ning.pro.util.file.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.*;


@RequestMapping("/image")
@RestController
public class ImageController {

    private final Logger logger = LoggerFactory.getLogger(ImageController.class);

    private final static String BASE_URL = "https://source.unsplash.com/random/1920x1080";

    @Autowired
    private ImageService imageService;

    @Autowired
    private TodayService todayService;

    @RequestMapping("/url")
    public String url() {
        todayService.incrase();
        return URLUtil.RedirectUrl(this.BASE_URL);
    }

    @RequestMapping("/all")
    public me.ning.pro.common.vo.Page all(@RequestParam(value = "pageNum", required = false, defaultValue="1") Integer pageNum,
                                          @RequestParam(value = "pageSize", required = false, defaultValue="10") Integer pageSize
    ) {
        //分页处理，显示第一页的10条数据
         PageHelper.startPage(pageNum, pageSize);
        Page<Image> images = (Page<Image>) imageService.allImages();
        me.ning.pro.common.vo.Page page = new me.ning.pro.common.vo.Page(images.getResult());
        page.setTotal((int) images.getTotal());
        page.setPageNum(pageNum);
        page.setPageSize(pageSize);
        page.setPages(me.ning.pro.common.vo.Page.pages(pageSize,page.getTotal()));
        return page;
    }

    @RequestMapping("/save")
    public OutputDTO add(String url) {
        OutputDTO outputDTO = new OutputDTO();
        try {
            ImageUtil.checkAndDownload(url);
            boolean had = imageService.hadImage(url);
            if (had) {
                outputDTO.setMsg("已存在");
            }else {
                imageService.addImage(url);
                outputDTO.setMsg("保存成功");
                logger.info("保存图片：已保存至腾讯云");
            }
        } catch (Exception e) {
            e.printStackTrace();
            outputDTO.setMsg("已经有了");
        }
        return outputDTO;
    }

    @RequestMapping("/had")
    public OutputDTO had(String url) {
        OutputDTO outputDTO = new OutputDTO();
        boolean result = imageService.hadImage(url);
        if (result) {
            outputDTO.setMsg("已存在");
        }else{
            outputDTO.setCode("-1");
            outputDTO.setMsg("不存在");
        }
        return outputDTO;
    }

    @RequestMapping("/checkAndDownload")
    public OutputDTO valiExitsFile(String url){
        OutputDTO outputDTO = new OutputDTO();
       if(!ImageUtil.checkAndDownload(url)){
           outputDTO.setMsg("文件不存在，无法下载");
           outputDTO.setCode("-1");
       }
       return outputDTO;
    }

    @RequestMapping("/download")
    public void download(HttpServletResponse response, String url) {
        ImageUtil.checkAndDownload(url);
        File file = new File(
                ImageUtil.fullPath(url));
        if (FileUtil.checkExist(file)) {
            ResponseUtil.responseHeader(response,ImageUtil.name(url));
            FileUtil.downloadFile(response, file);
        }
    }

}
