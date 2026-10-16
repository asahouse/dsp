package org.codeworks.dsp.controller.site.admin;

import com.fasterxml.jackson.annotation.JsonView;
import org.codeworks.dsp.controller.AbstractController;
import org.codeworks.dsp.model.dto.Response;
import org.codeworks.dsp.model.entities.Advertiser;
import org.codeworks.dsp.model.groups.Create;
import org.codeworks.dsp.model.groups.Update;
import org.codeworks.dsp.model.views.advertiser.ListView;
import org.codeworks.dsp.model.views.advertiser.NoPassword;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.codeworks.dsp.exception.ErrorCodes;
import org.codeworks.dsp.service.AdvertiserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 广告主后台管理控制接口
 * 提供广告主开户、信息维护、资质变更、逻辑删除与分页检索功能。
 */
@Api(tags = "广告主后台管理接口", description = "广告主账号开户、资质维护、状态管理与列表查询")
@RequestMapping("/v2/admin/advertiser")
@RestController("/v2/admin/AdvertiserController")
public class AdvertiserController extends AbstractController {

    @Autowired
    private AdvertiserService advService;

    /**
     * 新增广告主
     */
    @ApiOperation(value = "开户新增广告主", notes = "创建新广告主账号及资质信息，初始余额需设置且用户名需唯一")
    @PostMapping
    public Response add(
            @ApiParam(value = "广告主创建对象", required = true)
            @Validated(Create.class) @RequestBody Advertiser adv, BindingResult result) {
        if (result.hasErrors())
            return Response.badRequest(getMessage(result.getFieldError()));

        advService.createAdvertiser(adv);

        return Response.ok();
    }

    /**
     * 更新广告主信息
     */
    @ApiOperation(value = "更新广告主资质及信息", notes = "修改广告主基本信息与资质；若资质变更将自动触发重新送审机制")
    @PutMapping
    public Response update(
            @ApiParam(value = "广告主更新对象", required = true)
            @Validated(Update.class) @RequestBody Advertiser adv, BindingResult result) {
        if (result.hasErrors())
            return Response.badRequest(getMessage(result.getFieldError()));

        advService.updateAdvertiser(adv);

        return Response.ok();
    }

    /**
     * 逻辑删除广告主 (防外键完整性崩溃)
     */
    @ApiOperation(value = "删除广告主", notes = "执行逻辑软删除（将其状态标记为不可用），完好保留历史账单与投放活动关联数据")
    @DeleteMapping
    public Response delete(
            @ApiParam(value = "广告主 ID", required = true)
            @RequestParam(required = false) Integer id) {
        if (id == null) {
            return Response.badRequest(getMessage(ErrorCodes.no_such_advertiser.getCode()));
        }
        advService.deleteAdvertiser(id);
        return Response.ok();
    }

    /**
     * 获取单个广告主详情
     */
    @ApiOperation(value = "获取广告主详情", notes = "根据主键 ID 查询广告主详细信息与企业资质")
    @JsonView(NoPassword.class)
    @GetMapping(path = "/{id}")
    public Response get(
            @ApiParam(value = "广告主 ID", required = true)
            @PathVariable Integer id) {
        return Response.ok("advertiser", advService.getAdvertiser(id));
    }

    /**
     * 分页查询广告主列表
     */
    @ApiOperation(value = "分页查询广告主列表", notes = "支持按审核状态、启用状态、用户名、邮箱等多种条件组合检索")
    @JsonView(ListView.class)
    @GetMapping
    public Response list(
            @ApiParam(value = "多条件查询参数映射 (review, status, userName, email 等)")
            @RequestParam Map<String, Object> params, Pageable page) {
        Page<Advertiser> advs = advService.getAdvertisers(params, page);
        return Response.ok("list", advs.getContent())
                .add("total", advs.getTotalElements());
    }
}
