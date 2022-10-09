package org.codeworks.dsp.controller.site.admin;

import com.fasterxml.jackson.annotation.JsonView;
import org.codeworks.dsp.controller.AbstractController;
import org.codeworks.dsp.model.dto.Response;
import org.codeworks.dsp.model.entities.Advertiser;
import org.codeworks.dsp.model.groups.Create;
import org.codeworks.dsp.model.groups.Update;
import org.codeworks.dsp.model.views.advertiser.ListView;
import org.codeworks.dsp.model.views.advertiser.NoPassword;
import org.codeworks.dsp.service.AdvertiserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 广告主后台接口
 * Created by Luis on 2016/8/23.
 */
@RequestMapping("/v2/admin/advertiser")
@RestController("/v2/admin/AdvertiserController")
public class AdvertiserController extends AbstractController {

    @Autowired
    private AdvertiserService advService;

    @PostMapping
    public Response add(@Validated(Create.class) @RequestBody Advertiser adv, BindingResult result) {
        if (result.hasErrors())
            return Response.badRequest(getMessage(result.getFieldError()));

        advService.createAdvertiser(adv);

        return Response.ok();
    }

    @PutMapping
    public Response update(@Validated(Update.class) @RequestBody Advertiser adv, BindingResult result) {
        if (result.hasErrors())
            return Response.badRequest(getMessage(result.getFieldError()));

        advService.updateAdvertiser(adv);

        return Response.ok();
    }

    @DeleteMapping
    public Response delete(Integer id) {
        advService.deleteAdvertiser(id);
        return Response.ok();
    }

    @JsonView(NoPassword.class)
    @GetMapping(path = "/{id}")
    public Response get(@PathVariable Integer id) {
        return Response.ok("advertiser", advService.getAdvertiser(id));
    }

    @JsonView(ListView.class)
    @GetMapping
    public Response list(@RequestParam Map<String, Object> params, Pageable page) {
        Page<Advertiser> advs = advService.getAdvertisers(params, page);
        return Response.ok("list", advs.getContent())
                .add("total", advs.getTotalElements());
    }
}
