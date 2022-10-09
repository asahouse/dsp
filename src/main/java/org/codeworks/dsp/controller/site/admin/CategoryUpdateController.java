package org.codeworks.dsp.controller.site.admin;

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
@RequestMapping("/v2/admin/category")
@RestController("/v2/admin/category/CategoryUpdateController")
public class CategoryUpdateController extends AbstractController {

    @Autowired
    private CategoryService categoryService;

    @PutMapping(path = "/uploadFile")
    public Response uploadFile(@RequestParam("file") MultipartFile excel, @RequestParam String tagCode){

        CategoryHandler handler = new CategoryNormalHandler();

        if (tagCode.equals("MASSES_TAG")) handler = new CategoryMassesTagHandler();

        categoryService.updateCategory(tagCode, excel, handler);

        return Response.ok();
    }

}
