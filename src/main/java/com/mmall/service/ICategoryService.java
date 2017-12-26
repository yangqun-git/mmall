package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;

import java.util.List;

/**
 * Created by yangqun on 2017/12/25.
 */
public interface ICategoryService {
    ServerResponse<String> addCategory(String categoryName, Integer parentId);

    ServerResponse<String> setCategoryName(String categoryName,Integer categoryId);

    ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId);

    ServerResponse<List<Integer>> getCategoryAndDeepChildrenCategory(Integer categoryId);
}
