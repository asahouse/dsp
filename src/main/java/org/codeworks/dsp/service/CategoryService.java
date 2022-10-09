package org.codeworks.dsp.service;

import org.codeworks.dsp.handler.excel.CategoryHandler;
import org.codeworks.dsp.model.dto.CategoryTree;
import org.codeworks.dsp.model.entities.CategoryDictionary;
import org.codeworks.dsp.model.entities.CategoryTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * Created by benjaminkc on 16/10/19.
 */
public interface CategoryService {

    Map<String, List<CategoryTree>> getAll();

    void createCategoryDictionary(CategoryDictionary categoryDictionary);

    void updateCategoryDictionary(CategoryDictionary categoryDictionary);

    Page<CategoryDictionary> getCategoryDictionaries(Map<String, Object> params, Pageable page);

    CategoryDictionary getCategoryDictionary(Integer id);


    void createCategoryTag(CategoryTag categoryTag);

    void updateCategoryTag(CategoryTag categoryTag);

    Page<CategoryTag> getCategoryTags(Map<String, Object> params, Pageable page);

    CategoryTag getCategoryTag(Integer id);


    void updateCategory(String tagCode, MultipartFile excel, CategoryHandler handler);

    void initCategoryTags(String propertiesValue);
}
