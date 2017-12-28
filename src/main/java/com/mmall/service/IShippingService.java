package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Shipping;

/**
 * Created by yangqun on 2017/12/28.
 */
public interface IShippingService {
    ServerResponse<PageInfo> add(Integer userId, Shipping shipping);

    ServerResponse<PageInfo> del(Integer userId,Integer shippingId);

    ServerResponse<String> update(Integer userId,Shipping shipping);

    ServerResponse<Shipping> select(Integer userId,Integer shippingId);

    ServerResponse<PageInfo> list(Integer userId,int pageNum,int pageSize);
}
