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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 行业分类与人群标签字典接口
 * <p>
 * 提供行业分类字典、行业树型全量列表以及定向人群标签的分页查询能力。
 * </p>
 *
 * @author benjaminkc
 */
@Api(tags = "行业分类与标签字典接口", description = "提供广告行业分类字典、标签树与人群定向标签查询")
@RequestMapping("/v2/category")
@RestController("/v2/category/CategoryController")
public class CategoryController extends AbstractController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 分页查询行业分类字典
     *
     * @param params 过滤参数
     * @param page 分页参数
     * @return 分类字典分页数据
     */
    @ApiOperation(value = "分页查询行业分类字典", notes = "获取系统收录的行业分类字典条目，支持按分类编码或名称检索")
    @GetMapping(path = "/dictionary")
    public Response dictionaries(@ApiParam(value = "过滤参数Map") @RequestParam Map<String, Object> params,
                                 @ApiParam(value = "分页参数") Pageable page) {
        Page<CategoryDictionary> categoryDictionaries = categoryService.getCategoryDictionaries(params, page);
        return Response.ok("list", categoryDictionaries.getContent()).add("total", categoryDictionaries.getTotalElements());
    }

    /**
     * 获取全量行业分类列表
     *
     * @return 完整的行业分类层级数据
     */
    @ApiOperation(value = "获取全量行业分类", notes = "获取所有可供广告主和活动选择的行业分类完整层级列表")
    @JsonView(ListView.class)
    @GetMapping(path = "/all")
    public Response all() {
        return Response.ok("list", categoryService.getAll());
    }

    /**
     * 分页查询人群标签
     *
     * @param params 过滤参数
     * @param page 分页参数
     * @return 人群标签分页数据
     */
    @ApiOperation(value = "分页查询人群标签", notes = "获取用于定向设置的人群属性标签列表")
    @JsonView(ListView.class)
    @GetMapping(path = "/tag")
    public Response tags(@ApiParam(value = "过滤参数Map") @RequestParam Map<String, Object> params,
                         @ApiParam(value = "分页参数") Pageable page) {
        Page<CategoryTag> categoryTags = categoryService.getCategoryTags(params, page);
        return Response.ok("list", categoryTags.getContent()).add("total", categoryTags.getTotalElements());
    }
}
