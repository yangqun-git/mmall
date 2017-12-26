package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Created by yangqun on 2017/12/25.
 */
@Controller
@RequestMapping("/manger/category/")
public class CategoryMangerController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private ICategoryService iCategoryService;
    /*添加品类*/
    @RequestMapping(value = "add_category.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> addCategory(HttpSession session, @RequestParam(value = "parentId",defaultValue = "0") Integer parentId,
                                              String categoryName){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.creteByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"当前未登录!");
        }
        //判断是否是管理员
        if (iUserService.checkAdminRole(user).isSuccess()){
            return iCategoryService.addCategory(categoryName,parentId);
        }else{
            return ServerResponse.createByErrorMessage("当前用户不是管理员");
        }
    }

    /*重命名分类*/
    @RequestMapping(value = "set_category_name.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> setCategoryName(HttpSession session,String categoryName,Integer categoryId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.creteByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"当前未登录!");
        }
        //判断是否是管理员
        if (iUserService.checkAdminRole(user).isSuccess()){
            return iCategoryService.setCategoryName(categoryName,categoryId);
        }else{
            return ServerResponse.createByErrorMessage("当前用户不是管理员");
        }
    }

    /*获取当前分类的下一级分类*/
    @RequestMapping(value = "get_children.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<Category>> getChildrenParallelCategory(HttpSession session,Integer categoryId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.creteByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"当前未登录!");
        }
        //判断是否是管理员
        if (iUserService.checkAdminRole(user).isSuccess()){
            //
            return iCategoryService.getChildrenParallelCategory(categoryId);
        }else{
            return ServerResponse.createByErrorMessage("当前用户不是管理员");
        }
    }

    /*递归当前分类的所有子节点*/
    @RequestMapping(value = "deep_children.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<Integer>> getCategoryAndDeepChildrenCategory(HttpSession session,@RequestParam(value = "categoryId",
            defaultValue = "0") Integer categoryId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.creteByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"当前未登录!");
        }
        //判断是否是管理员
        if (iUserService.checkAdminRole(user).isSuccess()){
            //
            return iCategoryService.getCategoryAndDeepChildrenCategory(categoryId);
        }else{
            return ServerResponse.createByErrorMessage("当前用户不是管理员");
        }
    }
}
