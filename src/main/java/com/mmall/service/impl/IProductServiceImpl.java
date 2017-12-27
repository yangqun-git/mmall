package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ICategoryService;
import com.mmall.service.IProductService;
import com.mmall.utils.DateTimeUtil;
import com.mmall.utils.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品管理实现
 * Created by yangqun on 2017/12/25.
 */
@Service("iProductService")
public class IProductServiceImpl implements IProductService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ICategoryService iCategoryService;
    /*新增或者更新产品*/
    public ServerResponse<String> saveOrUpdateProduct(Product product){
        if (product != null){
            if (StringUtils.isNotBlank(product.getSubImages())){
                String[] images = product.getSubImages().split(",");
                product.setMainImage(images[0]);
            }
            if (product.getId() == null){
                int rowCount = productMapper.insert(product);
                if (rowCount > 0){
                    return ServerResponse.createBySuccess("插入商品成功!");
                }else {
                    return ServerResponse.createByErrorMessage("插入商品失败!");
                }
            }else {
                int rowCount = productMapper.updateByPrimaryKey(product);
                if (rowCount > 0){
                    return ServerResponse.createBySuccess("更新商品成功!");
                }else {
                    return ServerResponse.createByErrorMessage("更新商品失败!");
                }
            }
        }else{
            return ServerResponse.createByErrorMessage("请补充商品信息!");
        }
    }

    /*修改商品上下架状态*/
    public ServerResponse<String> setSaleStatus(Integer productId,Integer status){
        if (productId == null || status == null){
            return ServerResponse.creteByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int rowCount = productMapper.updateByPrimaryKeySelective(product);
        if (rowCount > 0){
            return ServerResponse.createBySuccess("修改商品上架状态成功");
        }
        return ServerResponse.createByErrorMessage("修改商品上架状态失败");
    }

    /*获取商品详情*/

    public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId){
        if (productId == null){
            return ServerResponse.creteByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null){
            return ServerResponse.createByErrorMessage("该商品已下架或者删除");
        }
        return ServerResponse.createBySuccess(assembleProductDetailVo(product));
    }

    private ProductDetailVo assembleProductDetailVo (Product product){
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());

        //ImageHost
        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://192.168.25.128/"));
        //parentCategoryId
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if (category == null){
            productDetailVo.setParentCategoryId(0);
        }else {
            productDetailVo.setParentCategoryId(category.getParentId());
        }

        //createTime
        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        //updateTime
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
        return productDetailVo;
    }

    /*获取商品列表*/
    public ServerResponse<PageInfo> getProductList(Integer pageNum,Integer pageSize){
        //PageHelper --> start
        //编写自己的sql
        //PageHelper --> 收尾
        PageHelper.startPage(pageNum,pageSize);

        List<ProductListVo> voList = new ArrayList<>();
        List<Product> productList = productMapper.seledctList();
        for (Product product : productList){
            ProductListVo productVo = assembleProductListVo(product);
            voList.add(productVo);
        }
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(voList);

        return ServerResponse.createBySuccess(pageInfo);
    }

    private ProductListVo assembleProductListVo(Product product){
        ProductListVo productListVo = new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setName(product.getName());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setMainImage(product.getMainImage());
        productListVo.setPrice(product.getPrice());
        productListVo.setStatus(product.getStatus());
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://192.168.25.128/"));
        return productListVo;
    }

    /*根据商品id和商品名字查找商品*/
    public ServerResponse<PageInfo> searchProduct(Integer productId,String productName,Integer pageNum,Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        if (StringUtils.isNotBlank(productName)){
            productName = new StringBuilder().append("%").append(productName).append("%").toString();
        }
        List<ProductListVo> voList = new ArrayList<>();
        List<Product> productList = productMapper.selectProductByNameAndId(productName,productId);
        for (Product product : productList){
            ProductListVo productVo = assembleProductListVo(product);
            voList.add(productVo);
        }
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(voList);

        return ServerResponse.createBySuccess(pageInfo);
    }

    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId){
        if (productId == null){
            return ServerResponse.creteByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null){
            return ServerResponse.createByErrorMessage("该商品已下架或者删除");
        }
        if (product.getStatus() != Const.ProductStatusEnum.ON_SELE.getCode()){
            return ServerResponse.createByErrorMessage("该商品已下架或者删除");
        }
        return ServerResponse.createBySuccess(assembleProductDetailVo(product));
    }

    public ServerResponse<PageInfo> selectProductByNameAndId(String keyWord,Integer categoryId,Integer pageNum,Integer pageSize,String orderBy){
        List<Integer> CategoryIdList = new ArrayList<>();
        if(categoryId != null){
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if (category == null && StringUtils.isBlank(keyWord)){
                //没有该分类并且没有关键字,返回空集合
                PageHelper.startPage(pageNum,pageSize);
                List<ProductListVo> productListVo = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productListVo);
                return ServerResponse.createBySuccess(pageInfo);
            }
            //迭代当前分类的所有子分类
            CategoryIdList = iCategoryService.getCategoryAndDeepChildrenCategory(categoryId).getdata();
        }
        if (StringUtils.isNotBlank(keyWord)){
            keyWord = new StringBuilder().append("%").append(keyWord).append("%").toString();
        }
        PageHelper.startPage(pageNum,pageSize);
        //排序处理
        if (StringUtils.isNoneBlank(orderBy)){
            if(Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
                String[] array = orderBy.split("_");
                PageHelper.orderBy(array[0] + " " +array[1]);
            }

        }

        List<Product> productList = productMapper.selectProductByNameAndCategory(StringUtils.isBlank(keyWord)?null:keyWord,CategoryIdList.size()>0?CategoryIdList:Lists.<Integer>newArrayList(categoryId));
        List<ProductListVo> productListVoList = new ArrayList<>();
        for (Product product : productList){
            ProductListVo productListVo =assembleProductListVo(product);
            productListVoList.add(productListVo);
        }
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }
}
