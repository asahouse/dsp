package org.codeworks.dsp.controller.site.creatives;

import com.fasterxml.jackson.annotation.JsonView;
import org.codeworks.dsp.DspConstant;
import org.codeworks.dsp.controller.AbstractController;
import org.codeworks.dsp.exception.ErrorCodes;
import org.codeworks.dsp.model.dto.Materials;
import org.codeworks.dsp.model.dto.Response;
import org.codeworks.dsp.model.entities.Advertiser;
import org.codeworks.dsp.model.entities.Material;
import org.codeworks.dsp.model.groups.Create;
import org.codeworks.dsp.model.groups.Update;
import org.codeworks.dsp.model.views.creatives.ListView;
import org.codeworks.dsp.model.views.creatives.Single;
import org.codeworks.dsp.service.CreativesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 物料接口
 * Created by Luis on 2016/9/7.
 */
@RequestMapping("/v2/creatives/material")
@RestController("/v2/creatives/MaterialController")
public class MaterialController extends AbstractController {

    @Autowired
    private CreativesService creativesService;

    @PostMapping
    public Response add(@RequestParam Integer advId,
                        @RequestParam Integer cId,
                        @SessionAttribute(name = DspConstant.LOGIN_USER_SESSION_KEY) Advertiser loginAdv,
                        @Validated(Create.class) @RequestBody Materials materials,
                        BindingResult result) {
        if (result.hasErrors())
            return Response.badRequest(getMessage(result.getFieldError()));
        if (!loginAdv.getReview().equals(Advertiser.Review.eligible))
            return Response.badRequest(getMessage(ErrorCodes.advertiser_not_verify.getCode()));

        creativesService.createMaterials(advId, cId, materials);
        return Response.ok();
    }

    @PutMapping
    public Response update(@RequestParam Integer advId,
                           @RequestParam Integer cId,
                           @SessionAttribute(name = DspConstant.LOGIN_USER_SESSION_KEY) Advertiser loginAdv,
                           @Validated(Update.class) @RequestBody Materials materials,
                           BindingResult result) {
        if (result.hasErrors())
            return Response.badRequest(getMessage(result.getFieldError()));
        if (!loginAdv.getReview().equals(Advertiser.Review.eligible))
            return Response.badRequest(getMessage(ErrorCodes.advertiser_not_verify.getCode()));

        creativesService.updateMaterials(advId, cId, materials);
        return Response.ok();
    }

    @JsonView(ListView.class)
    @GetMapping
    public Response list(@RequestParam Integer advId,
                         @RequestParam Integer cId,
                         @RequestParam Map<String, Object> params,
                         Pageable page) {

        Page<Material> materials = creativesService.getMaterials(advId, cId, params, page);
        return Response.ok("list", materials.getContent())
                .add("total", materials.getTotalElements());
    }

    @JsonView(Single.class)
    @GetMapping(path = "/{id}")
    public Response get(@PathVariable Integer id,
                        @RequestParam Integer cId,
                        @RequestParam Integer advId) {

        return Response.ok("material", creativesService.getMaterial(id, advId, cId));
    }
}
