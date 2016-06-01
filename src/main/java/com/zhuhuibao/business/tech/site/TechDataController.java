package com.zhuhuibao.business.tech.site;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.zhuhuibao.common.Response;
import com.zhuhuibao.common.constant.ApiConstants;
import com.zhuhuibao.common.constant.Constants;
import com.zhuhuibao.common.constant.MsgCodeConstant;
import com.zhuhuibao.common.constant.TechConstant;
import com.zhuhuibao.common.util.ShiroUtil;
import com.zhuhuibao.exception.AuthException;
import com.zhuhuibao.mybatis.memCenter.service.UploadService;
import com.zhuhuibao.mybatis.tech.entity.DictionaryTechData;
import com.zhuhuibao.mybatis.tech.entity.TechData;
import com.zhuhuibao.mybatis.tech.entity.TechDownLoadData;
import com.zhuhuibao.mybatis.tech.service.DictionaryTechDataService;
import com.zhuhuibao.mybatis.tech.service.TechDataService;
import com.zhuhuibao.mybatis.tech.service.TechDownloadDataService;
import com.zhuhuibao.utils.MsgPropertiesUtils;
import com.zhuhuibao.utils.file.FileUtil;
import com.zhuhuibao.utils.pagination.model.Paging;
import com.zhuhuibao.utils.pagination.util.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
@RestController
@RequestMapping("/rest/tech/site/data")
@Api(value = "techData", description = "技术资料接口")
public class TechDataController {
    private static final Logger log = LoggerFactory.getLogger(TechDataController.class);

    @Autowired
    UploadService uploadService;

    @Autowired
    TechDataService techDataService;

    @Autowired
    DictionaryTechDataService dicTDService;

    @Autowired
    ApiConstants ApiConstants;

    @Autowired
    TechDownloadDataService dlService;

    @ApiOperation(value="上传技术资料(行业解决方案，技术文档，培训资料)",notes="上传技术资料(行业解决方案，技术文档，培训资料)",response = Response.class)
    @RequestMapping(value = "upload_tech_data", method = RequestMethod.POST)
    public Response uploadTechData(HttpServletRequest req) throws Exception {
        Response result = new Response();
        Subject currentUser = SecurityUtils.getSubject();
        Session session = currentUser.getSession(false);
        if(null != session){
            String url = uploadService.upload(req,"tech");
            Map map = new HashMap();
            map.put(Constants.name,url);
            if(url.lastIndexOf(".")!= -1) {
                map.put(TechConstant.FILE_FORMAT, url.substring(url.lastIndexOf(".")));
            }else
            {
                map.put(TechConstant.FILE_FORMAT, "");
            }
            map.put(TechConstant.FILE_FORMAT,req.getContentLength());
            result.setData(map);
            result.setCode(200);
        }else{
            throw new AuthException(MsgCodeConstant.un_login, MsgPropertiesUtils.getValue(String.valueOf(MsgCodeConstant.un_login)));
        }
        return result;
    }

    @RequestMapping(value="add_Tech_data", method = RequestMethod.POST)
    @ApiOperation(value="新增技术资料(行业解决方案，技术文档，培训资料)",notes = "新增技术资料(行业解决方案，技术文档，培训资料)",response = Response.class)
    public Response insertTechData(@ApiParam(value = "技术资料:行业解决方案，技术文档，培训资料")  @ModelAttribute(value="techData")TechData techData)
    {
        int result = techDataService.insertTechData(techData);
        Response response = new Response();
        return response;
    }

    @RequestMapping(value="sel_tech_data", method = RequestMethod.GET)
    @ApiOperation(value="运营管理平台搜索技术资料",notes = "运营管理平台搜索技术资料",response = Response.class)
    public Response findAllTechDataPager(@ApiParam(value = "1解决方案，2技术资料，3培训资料") @RequestParam(required = false) String fCategory,
                                         @ApiParam(value = "二级分类code") @RequestParam(required = false) String sCategory,
                                         @ApiParam(value = "页码") @RequestParam(required = false) String pageNo,
                                         @ApiParam(value = "每页显示的数目") @RequestParam(required = false) String pageSize) {
        Response response = new Response();
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("fCategory", fCategory);
        condition.put("sCategory", sCategory);
        //审核通过
        condition.put("status", TechConstant.TechDataStatus.AUDITPASS.toString());
        if (StringUtils.isEmpty(pageNo)) {
            pageNo = "1";
        }
        if (StringUtils.isEmpty(pageSize)) {
            pageSize = "10";
        }
        Paging<Map<String, String>> pager = new Paging<Map<String, String>>(Integer.valueOf(pageNo), Integer.valueOf(pageSize));
        List<Map<String, String>> techList = techDataService.findAllTechDataPager(pager, condition);
        pager.result(techList);
        response.setData(pager);
        return response;
    }

    @RequestMapping(value="sel_second_category", method = RequestMethod.GET)
    @ApiOperation(value="查询解决方案、技术资料，培训资料行业类别",notes = "查询解决方案、技术资料，培训资料行业类别",response = Response.class)
    public Response selectSecondCategoryByFirstId( @ApiParam(value = "一级分类ID：1解决方案，2技术资料，3培训资料")  @RequestParam() String firstCategoryId)
    {
        Response response = new Response();
        List<DictionaryTechData> secondCategoryList = dicTDService.getSecondCategory(Integer.parseInt(firstCategoryId));
        response.setData(secondCategoryList);
        return response;
    }

    //vip减 项目条数减
    @ApiOperation(value="下载技术资料",notes="下载技术资料",response = Response.class)
    @RequestMapping(value="downloadFile", method = RequestMethod.GET)
    public Response downloadBill(HttpServletResponse response,
                                 @ApiParam(value = "技术资料ID")@RequestParam String techDataId) throws Exception
    {
        Response jsonResult = new Response();
        log.debug("download tech data");
        try {
            Long createId = ShiroUtil.getCreateID();
            if(createId != null) {
                String attachName = techDataService.selectTechDataAttachName(Long.parseLong(techDataId));
                response.setDateHeader("Expires", 0);
                response.setHeader("Cache-Control",
                        "no-store, no-cache, must-revalidate");
                response.addHeader("Cache-Control", "post-check=0, pre-check=0");
                response.setHeader("Content-disposition", "attachment;filename=" + attachName);
                response.setContentType("application/octet-stream");
                attachName = ApiConstants.getUploadDoc() + TechConstant.UPLOAD_TECH_DATA_URL + "/" + attachName;
                jsonResult = FileUtil.downloadFile(response, attachName);
                //插入我的下载资料
                dlService.insertDownloadData(techDataId,createId);
                //更新下载率
                Map<String,Object> map = new HashMap<String,Object>();
                map.put("views","views");
                map.put("id",techDataId);
                techDataService.updateTechDataViewsOrDL(map);
            }else{
                throw new AuthException(MsgCodeConstant.un_login, MsgPropertiesUtils.getValue(String.valueOf(MsgCodeConstant.un_login)));
            }
        }
        catch(Exception e)
        {
            log.error("download tech data error! ",e);
        }
        return jsonResult;
    }

    @RequestMapping(value="sel_tech_data_detail", method = RequestMethod.GET)
    @ApiOperation(value="预览解决方案、技术资料，培训资料行业类别",notes = "预览解决方案、技术资料，培训资料行业类别",response = Response.class)
    public Response previewTechDataDetail( @ApiParam(value = "技术资料ID")  @RequestParam() String techDataId)
    {
        Response response = new Response();
        List<Map<String,String>> techDataList = techDataService.previewTechDataDetail(Long.parseLong(techDataId));
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("views","views");
        map.put("id",techDataId);
        techDataService.updateTechDataViewsOrDL(map);
        response.setData(techDataList);
        return response;
    }
}
