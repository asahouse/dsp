package org.codeworks.dsp.handler.excel;

import jxl.Cell;
import org.codeworks.dsp.model.entities.CategoryDictionary;
import org.codeworks.dsp.model.entities.CategoryTag;

import java.util.List;

/**
 * Created by benjaminkc on 16/10/23.
 */
public interface CategoryHandler {
    List<CategoryDictionary> process(CategoryTag tag, List<List<Cell>> contexts);
}
