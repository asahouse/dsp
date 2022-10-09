package org.codeworks.dsp.controller.site.campaign;

import com.fasterxml.jackson.annotation.JsonView;
import org.codeworks.dsp.DspConstant;
import org.codeworks.dsp.controller.AbstractController;
import org.codeworks.dsp.exception.ErrorCodes;
import org.codeworks.dsp.model.dto.Response;
import org.codeworks.dsp.model.dto.SubmitCampaign;
import org.codeworks.dsp.model.entities.Advertiser;
import org.codeworks.dsp.model.entities.Advertiser.Review;
import org.codeworks.dsp.model.entities.Campaign;
import org.codeworks.dsp.model.groups.Create;
import org.codeworks.dsp.model.groups.Update;
import org.codeworks.dsp.model.views.campaign.EditView;
import org.codeworks.dsp.model.views.campaign.ListView;
import org.codeworks.dsp.service.CampaignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 广告活动接口
 * Created by Luis on 2016/9/9.
 */
@RequestMapping("/v2/campaign")
@RestController("/v2/campaign/CampaignController")
public class CampaignController extends AbstractController {

    @Autowired
    private CampaignService campaignService;

    @JsonView(ListView.class)
    @PostMapping
    public Response add(@RequestParam Integer advId,
                        @SessionAttribute(name = DspConstant.LOGIN_USER_SESSION_KEY) Advertiser loginAdv,
                        @Validated(Create.class) @RequestBody SubmitCampaign campaign,
                        BindingResult result) {
        if (result.hasErrors())
            return Response.badRequest(getMessage(result.getFieldError()));
        if (!loginAdv.getReview().equals(Review.eligible))
            return Response.badRequest(getMessage(ErrorCodes.advertiser_not_verify.getCode()));

        return Response.ok("campaign",campaignService.createCampaign(advId, campaign, false));
    }

    @PutMapping
    public Response update(@RequestParam Integer advId,
                           @SessionAttribute(name = DspConstant.LOGIN_USER_SESSION_KEY) Advertiser loginAdv,
                           @Validated(Update.class) @RequestBody SubmitCampaign campaign,
                           BindingResult result) {
        if (result.hasErrors())
            return Response.badRequest(getMessage(result.getFieldError()));
        if (!loginAdv.getReview().equals(Review.eligible))
            return Response.badRequest(getMessage(ErrorCodes.advertiser_not_verify.getCode()));

        return Response.ok("campaign", campaignService.updateCampaign(advId, campaign, false));
    }

    @JsonView(ListView.class)
    @GetMapping
    public Response list(@RequestParam Integer advId, @RequestParam Map<String, Object> params, Pageable page) {
        Page<Campaign> campaigns = campaignService.getCampaignsAtAdvId(advId, params, page);
        return Response.ok("list", campaigns.getContent()).add("total", campaigns.getTotalElements());
    }

    @JsonView(EditView.class)
    @GetMapping(path = "/{id}")
    public Response get(@PathVariable Integer id, @RequestParam Integer advId) {
        return Response.ok("campaign", campaignService.getCampaignWithAll(id, advId));
    }
}
