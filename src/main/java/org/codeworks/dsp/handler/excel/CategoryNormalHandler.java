package org.codeworks.dsp.handler.excel;

import jxl.Cell;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.codeworks.dsp.model.entities.CategoryDictionary;
import org.codeworks.dsp.model.entities.CategoryTag;

import java.util.*;

/**
 * Created by benjaminkc on 16/10/23.
 */
public class CategoryNormalHandler implements CategoryHandler {

    @Override
    public List<CategoryDictionary> process(CategoryTag tag, List<List<Cell>> contexts) {

        Set<Integer> idSet = new HashSet<>();

        List<CategoryDictionary> dictionaries = new ArrayList<>();

        contexts.stream().forEach(rows -> {

            CategoryDictionary dic = new CategoryDictionary();
                dic.setCategoryTag(tag);
                dic.setStatus(CategoryDictionary.Status.eligible);
                dic.setCategoryParentId(0);

            rows.stream().forEach(cell -> {

                switch (cell.getColumn()) {
                    case 0: {
                        if (StringUtils.isBlank(cell.getContents())) break;
                        dic.setCategoryId(Integer.parseInt(cell.getContents()));
                        break;
                    }
                    case 1:
                        dic.setCategory(cell.getContents()); break;
                }
            });

            if (!idSet.contains(dic.getCategoryId())) {
                dictionaries.add(dic);
                idSet.add(dic.getCategoryId());
            }

        });


        List<CategoryDictionary> dictionariesForSecond = new ArrayList<>();
        contexts.stream().forEach(rows ->{

            CategoryDictionary dic = new CategoryDictionary();
                dic.setCategoryTag(tag);
                dic.setStatus(CategoryDictionary.Status.eligible);

            rows.stream().forEach(cell -> {
                switch (cell.getColumn()) {
                    case 0: {
                        if (StringUtils.isBlank(cell.getContents())) break;
                        dic.setCategoryParentId(Integer.parseInt(cell.getContents()));
                        break;
                    }
                    case 2: {
                        if (NumberUtils.isNumber(cell.getContents()))
                            dic.setCategoryId(Integer.parseInt(cell.getContents()));
                        break;
                    }
                    case 3:
                        dic.setCategory(cell.getContents()); break;
                }
            });

            Optional<Integer> idOpt = Optional.ofNullable(dic.getCategoryId());
            if (idOpt.isPresent())
                dictionariesForSecond.add(dic);
        });

        dictionaries.addAll(dictionariesForSecond);
        return dictionaries;
    }
}
