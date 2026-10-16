package org.codeworks.dsp.service.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.codeworks.dsp.dao.CategoryDictionaryRepository;
import org.codeworks.dsp.dao.CategoryTagRepository;
import org.codeworks.dsp.exception.CategoryException;
import org.codeworks.dsp.exception.ErrorCodes;
import org.codeworks.dsp.handler.excel.CategoryHandler;
import org.codeworks.dsp.model.dto.CategoryTree;
import org.codeworks.dsp.model.entities.CategoryDictionary;
import org.codeworks.dsp.model.entities.CategoryTag;
import org.codeworks.dsp.model.entities.QCategoryDictionary;
import org.codeworks.dsp.model.entities.QCategoryTag;
import org.codeworks.dsp.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static org.codeworks.dsp.model.entities.QCategoryDictionary.categoryDictionary;
import static org.codeworks.dsp.model.entities.QCategoryTag.categoryTag;

/**
 * Created by benjaminkc on 16/10/19.
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    private static final Map<String, List<CategoryTree>> alls = new ConcurrentHashMap<>();

    @Autowired
    private CategoryDictionaryRepository dicRepo;

    @Autowired
    private CategoryTagRepository tagRepo;

    @Override
    public Map<String, List<CategoryTree>> getAll(){
        if (!alls.isEmpty()) return alls;

        List<CategoryTag> tags = (ArrayList)tagRepo.findAll(buildCategoryTagPredicate(new HashedMap()));

        Map<String, List<CategoryTree>> all = new HashedMap();
        tags.stream().forEach(tag -> {

            Map<String, Object> key = new HashedMap();
            key.put("categoryTagCode", tag.getTagCode());

            List<CategoryDictionary> dics = (ArrayList)dicRepo.findAll(buildCategoryDictionaryPredicate(key));
            all.put(tag.getTagCode(), getNode(0, 0, dics));

        });

        alls.putAll(all);

        return alls;
    }

    @Override
    public void createCategoryDictionary(CategoryDictionary categoryDictionary) {
        dicRepo.save(categoryDictionary);
        alls.clear();
    }

    @Override
    public void updateCategoryDictionary(CategoryDictionary categoryDictionary) {
        Optional<CategoryDictionary> dicOpt = dicRepo.findById(categoryDictionary.getId());
        if (dicOpt.isPresent()){
            CategoryDictionary dbDic = dicOpt.get();
            if (categoryDictionary.getCategory() != null) dbDic.setCategory(categoryDictionary.getCategory());
            if (categoryDictionary.getCategoryId() != null) dbDic.setCategoryId(categoryDictionary.getCategoryId());
            if (categoryDictionary.getCategoryParentId() != null) dbDic.setCategoryParentId(categoryDictionary.getCategoryParentId());
            if (categoryDictionary.getStatus() != null) dbDic.setStatus(categoryDictionary.getStatus());
            if (categoryDictionary.getCategoryTag() != null) dbDic.setCategoryTag(categoryDictionary.getCategoryTag());
            dicRepo.save(dbDic);
            alls.clear();
        }else
            throw new CategoryException(ErrorCodes.no_such_category);
    }

    @Override
    public Page<CategoryDictionary> getCategoryDictionaries(Map<String, Object> params, Pageable page) {
        Predicate predicate = buildCategoryDictionaryPredicate(params);
        return dicRepo.findAll(predicate, page);
    }

    @Override
    public CategoryDictionary getCategoryDictionary(Integer id) {
        Optional<CategoryDictionary> dicOpt = Optional.ofNullable(dicRepo.findOne(String.valueOf(id)));
        if (dicOpt.isPresent())
            return dicOpt.get();
        else
            throw new CategoryException(ErrorCodes.no_such_category);
    }

    @Override
    public void createCategoryTag(CategoryTag categoryTag) {
        tagRepo.save(categoryTag);
        alls.clear();
    }

    @Override
    public void updateCategoryTag(CategoryTag categoryTag) {
        Optional<CategoryTag> tagOpt = Optional.ofNullable(tagRepo.findOne(categoryTag.getId()));
        if (tagOpt.isPresent()){
            CategoryTag dbTag = tagOpt.get();
            if (categoryTag.getTag() != null) dbTag.setTag(categoryTag.getTag());
            if (categoryTag.getTagCode() != null) dbTag.setTagCode(categoryTag.getTagCode());
            tagRepo.save(dbTag);
            alls.clear();
        }else
            throw new CategoryException(ErrorCodes.no_such_category_tag);
    }

    @Override
    public Page<CategoryTag> getCategoryTags(Map<String, Object> params, Pageable page) {
        Predicate predicate = buildCategoryTagPredicate(params);
        return tagRepo.findAll(predicate, page);
    }

    @Override
    public CategoryTag getCategoryTag(Integer id) {
        Optional<CategoryTag> tagOpt = Optional.ofNullable(tagRepo.findOne(id));
        if (tagOpt.isPresent())
            return tagOpt.get();
        else
            throw new CategoryException(ErrorCodes.no_such_category);
    }

    @Override
    public void updateCategory(String tagCode, MultipartFile excel, CategoryHandler handler){
        try{

            List<List<Cell>> contexts = parseExcel(excel);
            if (contexts.isEmpty())
                throw new CategoryException(ErrorCodes.update_category_error);


            Optional<CategoryTag> tagOpt = tagRepo.findByTagCode(tagCode);
            if (!tagOpt.isPresent())
                throw new CategoryException(ErrorCodes.update_category_error_tag);

            CategoryTag tag = tagOpt.get();

            dicRepo.deleteByCategoryTagTagCode(tag.getTagCode());

            List<CategoryDictionary> dictionaries = handler.process(tag, contexts);

            dicRepo.save(dictionaries);

            alls.clear();

        }catch (JpaSystemException ex) {
            throw new CategoryException(ErrorCodes.update_category_error_identify);
        }catch (IOException ex) {
            throw new CategoryException(ErrorCodes.update_category_error);
        }catch (BiffException ex){
            throw new CategoryException(ErrorCodes.update_category_error_excel);
        }
    }

    @Override
    public void initCategoryTags(String propertiesValue){
        if (StringUtils.isBlank(propertiesValue))
            throw new CategoryException(ErrorCodes.init_category_tag_error);

        for (String s : propertiesValue.split(",")) {
            String[] subject = s.split(":");
            if (subject.length < 2) continue;
            String tagCode = subject[0].trim();
            String tagName = subject[1].trim();

            Optional<CategoryTag> tagOpt = tagRepo.findByTagCode(tagCode);
            if (tagOpt.isPresent()) {
                CategoryTag dbTag = tagOpt.get();
                dbTag.setTag(tagName);
                tagRepo.save(dbTag);
            } else {
                CategoryTag tag = new CategoryTag();
                tag.setTagCode(tagCode);
                tag.setTag(tagName);
                tagRepo.save(tag);
            }
        }
    }

    private Predicate buildCategoryDictionaryPredicate(Map<String, Object> params) {
        BooleanBuilder builder = new BooleanBuilder();
        if (!Optional.ofNullable(params).isPresent() ||
                params.isEmpty()) return builder;

        QCategoryDictionary qCategoryDictionary = categoryDictionary;

        if (params.containsKey("status"))
            builder.and(qCategoryDictionary.status.eq(Integer.parseInt((String) params.get("status"))));
        if (params.containsKey("category"))
            builder.and(qCategoryDictionary.category.containsIgnoreCase((String) params.get("category")));
        if (params.containsKey("categoryTagCode"))
            builder.and(qCategoryDictionary.categoryTag.tagCode.eq((String)params.get("categoryTagCode")));
        if (params.containsKey("categoryParentId"))
            builder.and(qCategoryDictionary.categoryParentId.eq(Integer.parseInt((String) params.get("categoryParentId"))));

        return builder;
    }

    private Predicate buildCategoryTagPredicate(Map<String, Object> params) {
        BooleanBuilder builder = new BooleanBuilder();
        if (!Optional.ofNullable(params).isPresent() ||
                params.isEmpty()) return builder;

        QCategoryTag qCategoryTag = categoryTag;

        if (params.containsKey("tag"))
            builder.and(qCategoryTag.tag.containsIgnoreCase((String) params.get("tag")));

        if (params.containsKey("tagCode"))
            builder.and(qCategoryTag.tagCode.containsIgnoreCase((String) params.get("tagCode")));

        return builder;
    }

    private List<List<Cell>> parseExcel(MultipartFile excel) throws IOException, BiffException{
        List<List<Cell>> bookContents = new ArrayList<>();

        Workbook book = Workbook.getWorkbook(excel.getInputStream());
        Sheet sheet = book.getSheet(0);
        Cell cell = null;
        for(int i=1; i<sheet.getRows(); i++){

            //每行的各个列值
            List<Cell> columns = new ArrayList<>();

            //迭代列数
            for(int j=0; j<sheet.getColumns(); j++){
                cell = sheet.getCell(j,i);//获取第i行，第j列的值
                columns.add(cell);
            }

            //每行数据录入context中
            bookContents.add(columns);
        }

        return bookContents;
    }

    private List<CategoryTree> getNode(int pid, int level, List<CategoryDictionary> datas) {
        int currentLevel = ++level;

        List<CategoryTree> result = datas.stream().filter(dic -> dic.getCategoryParentId()==pid).map(dic ->{

            CategoryTree tree = new CategoryTree();
                tree.setCategoryDictionaryId(dic.getId());
                tree.setCategoryId(dic.getCategoryId());
                tree.setCategoryParentId(dic.getCategoryParentId());
                tree.setLevel(currentLevel);
                tree.setCategory(dic.getCategory());
                tree.setSubs(getNode(tree.getCategoryId(), currentLevel, datas));
            return tree;

        }).collect(Collectors.toList());
        return result;
    }


}
