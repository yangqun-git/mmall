package com.mmall.common;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Created by yangqun on 2017/12/23.
 */
public class Const {
    public static final String CURRENT_USER="currentuser";
    public static final String USERNAME="username";
    public static final String EMAIL="email";
    public interface ProductListOrderBy{
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_asc","price_desc");
    }
    public interface Role{
        int ROLE_CUSTOMER=0;//普通用户
        int ROLE_ADMIN=1;//管理员
    }

    public interface Cart{
        int CHECKED =1;     //1代表已勾选
        int UN_CHECKED =0;  //0代表未勾选

        String LIMIT_NUM_SUCCESS= "LIMIT_NUM_SUCCESS!";
        String LIMIT_NUM_FALSE= "LIMIT_NUM_FALSE";
    }
    public enum ProductStatusEnum{
        ON_SELE(1,"在售");
        private String value;
        private int code;
        ProductStatusEnum(int code,String value){
            this.value = value;
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }
}
