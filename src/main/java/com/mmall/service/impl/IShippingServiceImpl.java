package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Shipping;
import com.mmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by yangqun on 2017/12/28.
 */
@Service("iShippingService")
public class IShippingServiceImpl implements IShippingService {
    @Autowired
    private ShippingMapper shippingMapper;
    public ServerResponse<PageInfo> add(Integer userId, Shipping shipping){
        shipping.setUserId(userId);
        int count = shippingMapper.insert(shipping);
        if (count > 0){
            return this.list(userId,1,5);
        }
        return ServerResponse.createByErrorMessage("添加收货地址失败!");
    }

    public ServerResponse<PageInfo> del(Integer userId,Integer shippingId){
        if (userId == null || shippingId == null){
            return ServerResponse.creteByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        int count = shippingMapper.del(userId,shippingId);
        if (count > 0){
            return this.list(userId,1,5);
        }
        return ServerResponse.createByErrorMessage("删除收货地址失败!");
    }

    public ServerResponse<String> update(Integer userId,Shipping shipping){
        if (userId == null){
            return ServerResponse.creteByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        shipping.setUserId(userId);
        int count = shippingMapper.update(shipping);
        if (count > 0){
            return ServerResponse.createBySuccess("更新收货地址成功");
        }
        return ServerResponse.createByErrorMessage("更新收货地址失败!");
    }

    public ServerResponse<Shipping> select(Integer userId,Integer shippingId){
        if (userId == null){
            return ServerResponse.creteByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Shipping shipping = shippingMapper.select(userId,shippingId);
        if (shipping != null){
            return ServerResponse.createBySuccess(shipping);
        }
        return ServerResponse.createByErrorMessage("未找到此收货地址");
    }
    public ServerResponse<PageInfo> list(Integer userId,int pageNum,int pageSize){
        if (userId == null){
            return ServerResponse.creteByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        PageHelper.startPage(pageNum,pageSize);
        List<Shipping> shippingList = shippingMapper.list(userId);
        PageInfo pageInfo = new PageInfo(shippingList);
        return ServerResponse.createBySuccess(pageInfo);
    }
}
