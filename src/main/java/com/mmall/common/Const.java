package com.mmall.common;

/**
 * Created by yangqun on 2017/12/23.
 */
public class Const {
    public static final String CURRENT_USER="currentuser";
    public static final String USERNAME="username";
    public static final String EMAIL="email";
    public interface Role{
        int ROLE_CUSTOMER=0;//普通用户
        int ROLE_ADMIN=1;//管理员
    }
}
