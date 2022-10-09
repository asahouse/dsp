package org.codeworks.dsp.handler.excel;

import jxl.Cell;
import org.codeworks.dsp.model.entities.CategoryDictionary;
import org.codeworks.dsp.model.entities.CategoryTag;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by benjaminkc on 16/10/23.
 */
public class CategoryMassesTagHandler implements CategoryHandler {

    @Override
    public List<CategoryDictionary> process(CategoryTag tag, List<List<Cell>> contexts) {
        return contexts.stream().map(rows -> {
            CategoryDictionary dic = new CategoryDictionary();
            dic.setCategoryTag(tag);
            dic.setStatus(CategoryDictionary.Status.eligible);

            rows.stream().forEach(cell -> {
                switch (cell.getColumn()) {
                    case 0:
                        dic.setCategoryId(Integer.parseInt(cell.getContents())); break;
                    case 1:
                        dic.setCategoryParentId(Integer.parseInt(cell.getContents())); break;
                    case 3:
                        dic.setCategory(cell.getContents()); break;
                }
            });

            return dic;
        }).collect(Collectors.toList());
    }
}
