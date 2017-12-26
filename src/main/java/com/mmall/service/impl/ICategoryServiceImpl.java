package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Created by yangqun on 2017/12/25.
 */
@Service("iCategoryService")
public class ICategoryServiceImpl implements ICategoryService{

    @Autowired
    private CategoryMapper categoryMapper;
    /*添加分类*/
    public ServerResponse<String> addCategory(String categoryName,Integer parentId){
        if (parentId != null && StringUtils.isNotBlank(categoryName)){
            Category category = new Category();
            category.setName(categoryName);
            category.setParentId(parentId);
            category.setStatus(true);
            int rowCount = categoryMapper.insertSelective(category);
            if (rowCount > 0){
                return ServerResponse.createBySuccessMessage("创建品类成功");
            }else{
                return ServerResponse.createByErrorMessage("创建品类失败,请重试!");
            }
        }
        return ServerResponse.createByErrorMessage("请输入完整信息已添加品类");
    }

    /*重命名分类*/
    public ServerResponse<String> setCategoryName(String categoryName,Integer categoryId){
        if (StringUtils.isBlank(categoryName) || categoryId == null){
            return ServerResponse.createByErrorMessage("请输入分类名!");
        }
        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);

        int rowConut = categoryMapper.updateByPrimaryKeySelective(category);
        if (rowConut > 0){
            return ServerResponse.createBySuccessMessage("重命名品成功");
        }
        return ServerResponse.createByErrorMessage("重命名品类失败");
    }

    /*查看当前分类下一级分类*/
    public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId){
        if (categoryId == null){
            return ServerResponse.createByErrorMessage("味照当当前分类!");
        }
        List<Category> response = categoryMapper.selectChildrenParallel(categoryId);
        if (CollectionUtils.isEmpty(response)){
            return ServerResponse.createByErrorMessage("当前分类没有子节点");
        }
        return ServerResponse.createBySuccess(response);
    }

    /*递归所有子节点*/
    public ServerResponse<List<Integer>> getCategoryAndDeepChildrenCategory(Integer categoryId){
        Set<Category> set = Sets.newHashSet();
        deepChildren(set,categoryId);

        List<Integer> categoryIdList = Lists.newArrayList();
        for (Category category : set){
            categoryIdList.add(category.getId());
        }
        return ServerResponse.createBySuccess(categoryIdList);
    }

    /*递归函数*/
    public Set<Category> deepChildren(Set<Category> categorySet,Integer categoryId){
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if (category != null){
            categorySet.add(category);
        }
        List<Category> categoryList = categoryMapper.selectChildrenParallel(categoryId);
        for (Category categoryItem : categoryList) {
            deepChildren(categorySet,categoryItem.getId());
        }
        return categorySet;
    }
}
