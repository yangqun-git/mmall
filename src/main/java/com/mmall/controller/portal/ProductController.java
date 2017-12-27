package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.service.IProductService;
import com.mmall.vo.ProductDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by yangqun on 2017/12/27.
 */
@Controller
@RequestMapping("/product/")
public class ProductController {

    @Autowired
    private IProductService iProductService;

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<ProductDetailVo> detail(Integer productId){
            return iProductService.getProductDetail(productId);
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(@RequestParam(required = false) String keyWord,
                                         @RequestParam(required = false)Integer categoryId,
                                         @RequestParam(required = false,defaultValue = "1") Integer pageNum,
                                         @RequestParam(required = false,defaultValue = "10") Integer pageSize,
                                         @RequestParam(required = false,defaultValue = "price_asc")String orderBy){
    return iProductService.selectProductByNameAndId(keyWord, categoryId, pageNum, pageSize, orderBy);
    }
}
