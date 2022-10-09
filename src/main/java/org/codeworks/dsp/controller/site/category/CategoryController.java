package org.codeworks.dsp.controller.site.category;

import com.fasterxml.jackson.annotation.JsonView;
import org.codeworks.dsp.controller.AbstractController;
import org.codeworks.dsp.handler.excel.CategoryHandler;
import org.codeworks.dsp.handler.excel.CategoryMassesTagHandler;
import org.codeworks.dsp.handler.excel.CategoryNormalHandler;
import org.codeworks.dsp.model.dto.Response;
import org.codeworks.dsp.model.entities.CategoryDictionary;
import org.codeworks.dsp.model.entities.CategoryTag;
import org.codeworks.dsp.model.views.advertiser.ListView;
import org.codeworks.dsp.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * Created by benjaminkc on 16/10/19.
 */
@RequestMapping("/v2/category")
@RestController("/v2/category/CategoryController")
public class CategoryController extends AbstractController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping(path = "/dictionary")
    public Response dictionaries(@RequestParam Map<String, Object> params, Pageable page) {
        Page<CategoryDictionary> categoryDictionaries = categoryService.getCategoryDictionaries(params, page);
        return Response.ok("list", categoryDictionaries.getContent()).add("total", categoryDictionaries.getTotalElements());
    }

    @JsonView(ListView.class)
    @GetMapping(path = "/all")
    public Response all(){
        return Response.ok("list", categoryService.getAll());
    }

    @JsonView(ListView.class)
    @GetMapping(path = "/tag")
    public Response tags(@RequestParam Map<String, Object> params, Pageable page) {
        Page<CategoryTag> categoryTags = categoryService.getCategoryTags(params, page);
        return Response.ok("list", categoryTags.getContent()).add("total", categoryTags.getTotalElements());
    }

    @PutMapping(path = "/uploadFile")
    public Response uploadFile(@RequestParam("file") MultipartFile excel, @RequestParam String tagCode){

        CategoryHandler handler = new CategoryNormalHandler();

        if (tagCode.equals("MASSES_TAG")) handler = new CategoryMassesTagHandler();

        categoryService.updateCategory(tagCode, excel, handler);

        return Response.ok();
    }

}
