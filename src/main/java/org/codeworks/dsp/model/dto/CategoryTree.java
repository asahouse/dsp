package org.codeworks.dsp.model.dto;

import com.fasterxml.jackson.annotation.JsonView;
import org.codeworks.dsp.model.entities.CategoryDictionary;
import org.codeworks.dsp.model.views.advertiser.ListView;

import java.io.Serializable;
import java.util.List;

/**
 * Created by benjaminkc on 16/10/21.
 */
public class CategoryTree implements Serializable {

    @JsonView(ListView.class)
    private String categoryDictionaryId;

    private CategoryDictionary categoryDictionary;

    @JsonView(ListView.class)
    private Integer categoryId;

    @JsonView(ListView.class)
    private Integer categoryParentId;

    @JsonView(ListView.class)
    private Integer level;

    @JsonView(ListView.class)
    private String category;

    @JsonView(ListView.class)
    List<CategoryTree> subs;

    public String getCategoryDictionaryId() {
        return categoryDictionaryId;
    }

    public void setCategoryDictionaryId(String categoryDictionaryId) {
        this.categoryDictionaryId = categoryDictionaryId;
    }

    public CategoryDictionary getCategoryDictionary() {
        return categoryDictionary;
    }

    public void setCategoryDictionary(CategoryDictionary categoryDictionary) {
        this.categoryDictionary = categoryDictionary;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getCategoryParentId() {
        return categoryParentId;
    }

    public void setCategoryParentId(Integer categoryParentId) {
        this.categoryParentId = categoryParentId;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<CategoryTree> getSubs() {
        return subs;
    }

    public void setSubs(List<CategoryTree> subs) {
        this.subs = subs;
    }
}
