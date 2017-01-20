package com.movision.controller.boss;

import com.movision.mybatis.adminMenu.entity.Menu;
import com.movision.common.Response;
import com.movision.facade.user.MenuFacade;
import com.movision.utils.pagination.util.StringUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @Author zhuangyuhao
 * @Date 2017/1/19 17:15
 */
@RestController
@RequestMapping("boss/sys")
@Api(value = "boss/sys", description = "系统管理")
public class SysController {

    @Autowired
    private MenuFacade menuFacade;


    @ApiOperation(value = "新增菜单", notes = "新增菜单", response = Response.class)
    @RequestMapping(value = "menu/add_menu", method = RequestMethod.POST)
    public Response addMenu(@ApiParam @ModelAttribute Menu menu) {
        Response response = new Response();
        boolean isAdd = menuFacade.addMenu(menu);
        if (isAdd) {
            response.setCode(200);
        } else {
            response.setCode(400);
        }
        return response;
    }

    @ApiOperation(value = "修改菜单", notes = "修改菜单", response = Response.class)
    @RequestMapping(value = "menu/update_menu", method = RequestMethod.POST)
    public Response updateMenu(@ApiParam @ModelAttribute Menu menu) {
        Response response = new Response();
        boolean isAdd = menuFacade.updateMenu(menu);
        if (isAdd) {
            response.setCode(200);
        } else {
            response.setCode(400);
        }
        return response;
    }

    @ApiOperation(value = "查询菜单详情", notes = "查询菜单详情", response = Response.class)
    @RequestMapping(value = "menu/query_menu", method = RequestMethod.POST)
    public Response queryMenu(@ApiParam(value = "菜单id") @RequestParam int id) {
        Response response = new Response();
        Menu menu = menuFacade.queryMenu(id);
        response.setData(menu);
        return response;
    }

    @ApiOperation(value = "查询菜单列表（分页）", notes = "查询菜单列表（分页）", response = Response.class)
    @RequestMapping(value = "menu/query_menu_list)", method = RequestMethod.POST)
    public Response queryMenuList(@RequestParam(required = false) String pageNo,
                                  @RequestParam(required = false) String pageSize,
                                  @ApiParam(value = "菜单名称") @RequestParam(required = false) String menuname) {
        Response response = new Response();
        List<Menu> list = menuFacade.queryMenuList(pageNo, pageSize, menuname);
        response.setData(list);
        return response;
    }


}
