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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 行业分类与标签 Excel 上传更新接口
 * <p>
 * 支持管理员通过上传 Excel 字典表批量刷新行业分类或人群兴趣标签库。
 * </p>
 *
 * @author benjaminkc
 */
@Api(tags = "行业分类批量导入接口", description = "提供管理员批量上传 Excel 刷新行业分类库及人群标签字典")
@RequestMapping("/v2/admin/category")
@RestController("/v2/admin/category/CategoryUpdateController")
public class CategoryUpdateController extends AbstractController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 上传 Excel 文件更新行业或标签数据
     *
     * @param excel 上传的 Excel 文件
     * @param tagCode 标签类别标识（如 MASSES_TAG）
     * @return 操作成功响应
     */
    @ApiOperation(value = "上传 Excel 刷新分类字典", notes = "管理员上传标准化 Excel 表格，系统按解析器将数据落盘并刷新字典与缓存")
    @PutMapping(path = "/uploadFile")
    public Response uploadFile(@ApiParam(value = "分类数据Excel文件", required = true) @RequestParam("file") MultipartFile excel,
                               @ApiParam(value = "分类代码(例如: MASSES_TAG)", required = true) @RequestParam String tagCode) {

        CategoryHandler handler = new CategoryNormalHandler();

        if (tagCode.equals("MASSES_TAG")) handler = new CategoryMassesTagHandler();

        categoryService.updateCategory(tagCode, excel, handler);

        return Response.ok();
    }
}
