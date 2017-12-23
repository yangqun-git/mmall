package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;


/**用户模块
 * Created by yangqun on 2017/12/23.
 */
@Controller
@RequestMapping("/user/")
public class UserController {
    @Autowired
    private IUserService iUserService;

    /*登录模块*/
    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session){
        ServerResponse<User> reponse = iUserService.login(username,password);
        //添加进session
        if(reponse.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,reponse.getdata());
        }
        return reponse;
    }

    /*删除模块*/
    @RequestMapping(value = "logout.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }

    /*注册模块*/
    @RequestMapping(value = "register.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user){
        return  iUserService.register(user);
    }

    /*动态检测账号或者邮箱是否已经存在*/
    @RequestMapping(value = "check_valid.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str,String type){
        return iUserService.checkValid(str,type);
    }

    /*获取用户信息*/
    @RequestMapping(value = "get_user_info.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user != null) {
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("用户未登录,无法获取用户信息");
    }

    /*忘记密码*/
    @RequestMapping(value = "forget_get_question.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetGetQuestion(String username){
        return iUserService.getQuestion(username);
    }

    /*验证答案*/
    @RequestMapping(value = "forget_check_answer.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username,String question,String answer){
        return iUserService.checkAnswer(username, question, answer);
    }

    /*修改密码*/
    @RequestMapping(value = "forget_reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgerResetPassword(String username,String newPassword,String forgetToken){
        return iUserService.forgetResetPassword(username, newPassword, forgetToken);
    }
    /*登录状态修改密码*/
    @RequestMapping(value = "reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(String oldPassword,String newPassword,HttpSession session){
        if (StringUtils.isBlank(oldPassword) || StringUtils.isBlank(newPassword)){
            return ServerResponse.createByErrorMessage("请输出信息!");
        }
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user != null){
            return iUserService.resetPassword(oldPassword,newPassword,user);
        }
        return ServerResponse.createByErrorMessage("请登录!");
    }

    /*更改信息*/
    @RequestMapping(value = "update_information.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateInformation(HttpSession session,User user){
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null){
            return ServerResponse.createByErrorMessage("请登录");
        }
        //更改信息提交的user没有id属性
        user.setId(currentUser.getId());
        ServerResponse<User> response = iUserService.updateInformation(user);
        if (response.isSuccess()){
            response.getdata().setUsername(currentUser.getUsername());
            session.setAttribute(Const.CURRENT_USER,response.getdata());
        }
        return response;
    }

    /*获取个人信息*/
    @RequestMapping(value = "get_information.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getInformation(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.creteByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"需要强制登录!");
        }
        return iUserService.getInformation(user.getId());
    }
}
