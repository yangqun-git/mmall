package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.utils.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Created by yangqun on 2017/12/23.
 */
@Service("iUserService")
public class IUserServiceImpl implements IUserService{
    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        //判断用户名是否存在
        int count = userMapper.checkUsername(username);
        if(count == 0){
            return ServerResponse.createByErrorMessage("没有找到用户");
        }

        //todo md5加密
        password = MD5Util.MD5EncodeUtf8(password);
        //验证账号密码
        User user = userMapper.selectLogin(username,password);
        if(user == null){
            return ServerResponse.createByErrorMessage("密码错误");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功",user);
    }

    @Override
    public ServerResponse<String> register(User user) {
        //判断用户名是否存在
        ServerResponse validResponse = this.checkValid(user.getUsername(),Const.USERNAME);
        if (!validResponse.isSuccess()){
            return validResponse;
        }
        //判断邮箱是否已存在
        validResponse = this.checkValid(user.getEmail(),Const.EMAIL);
        if(!validResponse.isSuccess()){
            return validResponse;
        }
        user.setRole(Const.Role.ROLE_CUSTOMER);
        //MD5加密密码
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int conut = userMapper.insert(user);
        if (conut == 0){
            return ServerResponse.createByErrorMessage("注册失败,请稍后重试!");
        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }

    /*动态检测用户名和邮箱是否已经存在*/
    @Override
    public ServerResponse<String> checkValid(String str, String type) {
        if (StringUtils.isNotBlank(type)){
            //检测用户名是否已经存在
            if (Const.USERNAME.equals(type)){
                int usernameCount = userMapper.checkUsername(str);
                if(usernameCount > 0){
                    return ServerResponse.createByErrorMessage("用户名已存在");
                }
            }
            //检测邮箱是否已经使用
            if (Const.EMAIL.equals(type)){
                int emailCount = userMapper.checkEmail(str);
                if(emailCount > 0){
                    return ServerResponse.createByErrorMessage("邮箱已经被使用");
                }
            }

        }else{
            return ServerResponse.createByErrorMessage("参数错误!");
        }
        return ServerResponse.createBySuccessMessage("检测成功");
    }

    /*忘记密码,返回找回密码问题*/
    @Override
    public ServerResponse<String> getQuestion(String username) {
        //先判断账号是否存在
        ServerResponse validResponse = this.checkValid(username,Const.USERNAME);
        if (validResponse.isSuccess()){
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String question = userMapper.selectQuestionByUsername(username);
        if(StringUtils.isNotBlank(question)){
            return ServerResponse.createBySuccessMessage(question);
        }
        return ServerResponse.createByErrorMessage("找回密码的问题是空的");
    }

    /*检验问题和回答是否正确*/
    public ServerResponse<String> checkAnswer(String username,String question,String answer){
        int conut = userMapper.checkAnswer(username,question,answer);
        //如果conut不等于0就说明问题和回答是正确的
        if (conut != 0){
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setKey("token_"+username,forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("答案错误,请重试");
    }

    /*重置密码*/
    public ServerResponse<String> forgetResetPassword(String username,String newPassword,String forgetToken){
        if (StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMessage("参数错误,错误的token信息");
        }
        ServerResponse validResponse = this.checkValid(username,Const.USERNAME);
        if (validResponse.isSuccess()){
            return ServerResponse.createByErrorMessage("用户不存在!");
        }
        String token = TokenCache.getKey("token_"+username);
        if (StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("不存在的token或者token已经失效!");
        }
        if (StringUtils.equals(forgetToken,token)){
            String MD5Password = MD5Util.MD5EncodeUtf8(newPassword);
            int count = userMapper.updatePasswordByUsername(username,MD5Password);
            if (count > 0){
                return ServerResponse.createBySuccessMessage("成功修改密码");
            }else{
                return ServerResponse.createByErrorMessage("修改密码失败,请重试!");
            }
        }
        return ServerResponse.createByErrorMessage("修改密码失败,请重试!");
    }

    /*登录状态更新密码*/
    public ServerResponse<String> resetPassword(String oldPassword,String newPassword,User user){
        //校验旧密码
        int count = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(oldPassword),user.getId());
        if (count > 0){
            user.setPassword(MD5Util.MD5EncodeUtf8(newPassword));
            int updateCount = userMapper.updateByPrimaryKeySelective(user);
            if (updateCount != 0) {
                return ServerResponse.createBySuccessMessage("修改密码成功");
            }else{
                return ServerResponse.createByErrorMessage("密码修改失败,请重试!");
            }
        }else{
            return ServerResponse.createByErrorMessage("旧密码错误");
        }
    }

    /*更改信息*/
    public ServerResponse<User> updateInformation(User user){
        //校验email是不是除了当前用户已经存在.
        int count = userMapper.checkEmailByUserid(user.getEmail(),user.getId());
        if (count > 0){
            return ServerResponse.createByErrorMessage("email已經存在!");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setAnswer(user.getAnswer());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if (updateCount > 0){
            return ServerResponse.createBySuccess("更新个人信息成功!",updateUser);
        }

        return ServerResponse.createByErrorMessage("信息修改失败!");
    }

    /*获取个人信息*/
    public ServerResponse<User> getInformation(Integer userId){
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null){
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }
        return ServerResponse.createBySuccess(user);
    }

    /*判断用户是否是管理员*/
    public ServerResponse<String> checkAdminRole(User user){
        if (user != null && user.getRole() == Const.Role.ROLE_ADMIN){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }
}
