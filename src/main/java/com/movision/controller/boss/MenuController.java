package com.movision.controller.boss;

import com.movision.common.Response;
import com.movision.common.util.ShiroUtil;
import com.movision.facade.user.MenuFacade;
import com.movision.facade.user.RoleFacade;
import com.movision.facade.user.RoleMenuRelationFacade;
import com.movision.mybatis.bossMenu.entity.Menu;
import com.movision.mybatis.bossMenu.entity.MenuDetail;
import com.movision.mybatis.bossMenu.entity.MenuVo;
import com.movision.mybatis.role.entity.Role;
import com.movision.shiro.realm.BossRealm;
import com.movision.utils.pagination.model.Paging;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


/**
 * @Author zhuangyuhao
 * @Date 2017/1/19 17:15
 */
@RestController
@RequestMapping("/boss/menu")
public class MenuController {

    private static Logger log = LoggerFactory.getLogger(MenuController.class);

    @Autowired
    private MenuFacade menuFacade;

    @Autowired
    private RoleFacade roleFacade;

    @Autowired
    private RoleMenuRelationFacade roleMenuRelationFacade;

    @ApiOperation(value = "新增菜单", notes = "新增菜单", response = Response.class)
    @RequestMapping(value = "add_menu", method = RequestMethod.POST)
    public Response addMenu(@ApiParam @ModelAttribute Menu menu) {
        Response response = new Response();
        menuFacade.addMenu(menu);

        return response;
    }

    @ApiOperation(value = "修改菜单", notes = "修改菜单", response = Response.class)
    @RequestMapping(value = "update_menu", method = RequestMethod.POST)
    public Response updateMenu(@ApiParam @ModelAttribute Menu menu) {
        Response response = new Response();
        boolean isAdd = menuFacade.updateMenu(menu);
        if (isAdd) {
            response.setCode(200);
            response.setMessage("修改成功！");
        } else {
            response.setCode(400);
            response.setMessage("修改失败，请联系管理员");
        }
        return response;
    }

    @ApiOperation(value = "查询菜单详情", notes = "查询菜单详情", response = Response.class)
    @RequestMapping(value = "query_menu", method = RequestMethod.GET)
    public Response queryMenu(@ApiParam(value = "菜单id") @RequestParam int id) {
        Response response = new Response();
        MenuDetail menu = menuFacade.queryMenuDetail(id);

        response.setData(menu);
        return response;
    }


    /*@ApiOperation(value = "查询菜单列表（分页）", notes = "查询菜单列表（分页）", response = Response.class)
    @RequestMapping(value = "query_menu_list", method = RequestMethod.GET)
    public Response queryMenuList(@RequestParam(required = false, defaultValue = "1") String pageNo,
                                  @RequestParam(required = false, defaultValue = "10") String pageSize,
                                  @ApiParam(value = "菜单名称") @RequestParam(required = false) String menuname) {
        Response response = new Response();
        Paging<Menu> pager = new Paging<>(Integer.valueOf(pageNo), Integer.valueOf(pageSize));
        List<Menu> list = menuFacade.queryMenuList(pager, menuname);
        pager.result(list);
        response.setData(pager);
        return response;
    }*/

    @ApiOperation(value = "查询所有菜单", notes = "查询所有菜单", response = Response.class)
    @RequestMapping(value = "query_all_menu", method = RequestMethod.GET)
    public Response queryAllMenu() {
        Response response = new Response();
        List<Map<String, Object>> list = menuFacade.getAllMenu();
        response.setData(list);
        return response;
    }

    /*@ApiOperation(value = "查询当前角色的所有菜单", notes = "查询当前角色的所有菜单", response = Response.class)
    @RequestMapping(value = "query_all_menu_by_current_role", method = RequestMethod.GET)
    public Response queryAllMenuByCurrentRole(@ApiParam(value = "角色id") @RequestParam(required = true) Integer roleid) {
        Response response = new Response();
        List<Map<String, Object>> list = menuFacade.getAuthroizeMenu(roleid);
        response.setData(list);
        return response;
    }*/

    @ApiOperation(value = "删除菜单", notes = "删除菜单", response = Response.class)
    @RequestMapping(value = "del_menu", method = RequestMethod.POST)
    public Response delMenu(@ApiParam(value = "菜单id") @RequestParam(required = true) Integer menuid) {

        Response response = new Response();
        //1 删除菜单角色关系表
        roleMenuRelationFacade.delRelationByMenuid(menuid);
        //2 逻辑删除菜单本身
        menuFacade.delMenu(menuid);

        return response;
    }

    @ApiOperation(value = "获取指定菜单的关联角色", notes = "获取指定菜单的关联角色", response = Response.class)
    @RequestMapping(value = "get_relative_roles", method = RequestMethod.GET)
    public Response getMenuAndRoleRelation(@ApiParam(value = "菜单id") @RequestParam(required = true) Integer id) {
        Response response = new Response();
        List<Role> list = roleFacade.getRoleByMenuid(id);
        response.setData(list);
        return response;
    }


    @ApiOperation(value = "查询首页侧边栏", notes = "用于查询后台系统侧边栏接口", response = Response.class)
    @RequestMapping(value = "query_sidebar", method = RequestMethod.POST)
    public Response querySidebar() {
        Response response = new Response();

        BossRealm.ShiroBossUser user = ShiroUtil.getBossUser();
        if (null == user) {
            response.setCode(400);
            response.setMessage("登录超时，请重新登录");
        } else {
            log.debug("【查询首页侧边栏】登录人信息：" + user);
            int roleid = user.getRole();
            log.info("查询首页侧边栏, roleid = " + roleid);
            List<MenuVo> list = menuFacade.querySidebar(roleid);
            response.setMessage("查询成功");
            response.setData(list);
        }

        return response;
    }

}
