package com.movision.controller.app;

import com.movision.common.Response;
import com.movision.facade.collection.CollectionFacade;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author shuxf
 * @Date 2017/1/22 15:08
 */
@RestController
@RequestMapping("/app/collection/")
public class AppCollectionController {

    @Autowired
    private CollectionFacade collectionFacade;

    @ApiOperation(value = "收藏帖子接口", notes = "用户在帖子详情页面点击收藏时调用此接口进行收藏操作", response = Response.class)
    @RequestMapping(value = "collectionPost", method = RequestMethod.POST)
    public Response collectionPost(@ApiParam(value = "当前浏览的帖子id") @RequestParam String postid,
                                   @ApiParam(value = "当前登录的用户id") @RequestParam String userid,
                                   @ApiParam(value = "打赏类型：0 帖子 1 商品") @RequestParam String type) {
        Response response = new Response();

        int count = collectionFacade.collectionPost(postid, userid, type);

        if (response.getCode() == 200 && count == 1) {
            response.setMessage("收藏成功");
        }
        return response;
    }
}
