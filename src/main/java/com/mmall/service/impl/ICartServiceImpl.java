package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.ICartService;
import com.mmall.utils.BigDecimalUtil;
import com.mmall.utils.PropertiesUtil;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by yangqun on 2017/12/27.
 */
@Service("iCartService")
public class ICartServiceImpl implements ICartService {
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;

    @Transactional
    public ServerResponse<CartVo> add(Integer userId,Integer productId,Integer count){
        Product isHas = productMapper.selectByPrimaryKey(productId);
        if (productId == null || count == null || isHas == null){
            return ServerResponse.creteByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart = cartMapper.selectCartByUserIdProductId(userId,productId);
        //如果 cart 是空 就说明购物车内没有此商品,需要新创建
        if (cart == null){
            Cart insertCart = new Cart();
            insertCart.setUserId(userId);
            insertCart.setProductId(productId);
            insertCart.setQuantity(count);
            insertCart.setChecked(Const.Cart.CHECKED);
            cartMapper.insert(insertCart);
        }else{
            //有的话直接加数量就好了
            count = cart.getQuantity() + count;
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        CartVo cartVo = this.getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }


    private CartVo getCartVoLimit(Integer userId){
        CartVo cartVo = new CartVo();
        List<CartProductVo> cartProductVoList = Lists.newArrayList();
        List<Cart> cartList = cartMapper.selectCartsByUserId(userId);
        BigDecimal cartTotalPrice = new BigDecimal("0");
        if (CollectionUtils.isNotEmpty(cartList)){
            for (Cart cartItem : cartList){
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setProductId(cartItem.getProductId());
                cartProductVo.setUserId(cartItem.getUserId());
                cartProductVo.setProductChecked(cartItem.getChecked());

                Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
                if(product != null){
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductStock(product.getStock());
                    cartProductVo.setProductSubtittle(product.getSubtitle());

                    int buyLimitCount = 0;
                    if (product.getStock() >= cartItem.getQuantity()){
                        //库存充足
                        buyLimitCount = cartItem.getQuantity();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    }else{
                        //库存不足的时候
                        buyLimitCount = product.getStock();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FALSE);
                        //购物车中更新购买数量
                        Cart cart = new Cart();
                        cart.setId(cartItem.getId());
                        cart.setQuantity(product.getStock());
                    }
                    cartProductVo.setQuantity(buyLimitCount);
                    //设置此商品单的价格
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cartProductVo.getQuantity().doubleValue()));
                }
            if (cartItem.getChecked() == 1){
                //选中状态,加到购物车总价中
                cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(),cartProductVo.getProductTotalPrice().doubleValue());
            }
                cartProductVoList.add(cartProductVo);
            }
            cartVo.setCartProductVoList(cartProductVoList);
            cartVo.setTotalPrice(cartTotalPrice);
            cartVo.setAllChecked(this.allCheckedStatus(userId));
            cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        }
        return cartVo;
    }

    private boolean allCheckedStatus(Integer userId){
        int count = cartMapper.selectAllCheckedStatus(userId);
        if (count > 0){
            return false;
        }
        return true;
    }
}
