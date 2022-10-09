package org.codeworks.dsp.rtbCall.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.codeworks.dsp.model.dto.CategoryTree;
import org.codeworks.dsp.model.entities.Campaign;
import org.codeworks.dsp.model.entities.CampaignObjective;
import org.codeworks.dsp.model.entities.CategoryDictionary;
import org.codeworks.dsp.rtbCall.api.base.BaseRtbApi;
import org.codeworks.dsp.rtbCall.api.base.RtbApi;
import org.codeworks.dsp.rtbCall.dto.campaign.RtbCampaign;
import org.codeworks.dsp.rtbCall.dto.material.RtbMobileDevice;
import org.codeworks.dsp.rtbCall.dto.material.RtbUserCategory;
import org.codeworks.dsp.rtbCall.dto.request.DeleteRtbRequest;
import org.codeworks.dsp.rtbCall.dto.request.SyncRtbRequest;
import org.codeworks.dsp.rtbCall.dto.response.RtbResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by benjaminkc on 16/10/26.
 */
@Component
public class CampaignRtbApi extends BaseRtbApi implements RtbApi<RtbCampaign, Campaign> {

    @Override
    public RtbResponse<RtbCampaign> sync(Campaign campaign) throws JsonProcessingException {
        return sync(Collections.singletonList(campaign));
    }

    @Override
    public RtbResponse<RtbCampaign> sync(List<Campaign> campaigns) throws JsonProcessingException {
        SyncRtbRequest req = new SyncRtbRequest(convert(campaigns));
        ResponseEntity<RtbResponse> resp = post(rtbConstant.getSYNC_CAMPAIGN_URL(), mapper.writeValueAsString(req.getRtbs()), RtbResponse.class);
        isValidResponse(resp);
        return resp.getBody();
    }

    @Override
    public RtbResponse<RtbCampaign> delete(List<Integer> ids) throws JsonProcessingException{
        DeleteRtbRequest req = new DeleteRtbRequest(ids);
        ResponseEntity<RtbResponse> resp = delete(rtbConstant.getSYNC_CAMPAIGN_URL(), mapper.writeValueAsString(req.getIds()), RtbResponse.class);
        isValidResponse(resp);
        return resp.getBody();
    }

    @Override
    public List<RtbCampaign> convert(List<Campaign> campaigns) {
        if (!Optional.ofNullable(campaigns).isPresent() || campaigns.isEmpty()) return new ArrayList<>();

        return campaigns.stream().map(campaign -> {
            RtbCampaign rtb = new RtbCampaign();
                rtb.setId(campaign.getId());
                rtb.setAdvdId(campaign.getAdvertiser().getId());
                rtb.setBid(campaign.getBid());
                rtb.setBudget(campaign.getBudget());

                rtb.setUserCategory(transformUserCategory(campaign.getCampaignObjectives()));
                rtb.setMobileDevice(transformMobileDevice(campaign.getCampaignObjectives()));
            return rtb;
        }).collect(Collectors.toList());
    }

    private RtbUserCategory transformUserCategory(Set<CampaignObjective> objectives){
        RtbUserCategory userCategory = null;

        if (!Optional.ofNullable(objectives).isPresent()) return userCategory;

        List<CategoryDictionary> categoryDictionaries = objectives.stream()
                .map(campaignObjective -> campaignObjective.getCategoryDictionary())
                .filter(categoryDictionary -> categoryDictionary.getCategoryTag().getTagCode().equals("MASSES_TAG"))
                .collect(Collectors.toList());

        if (categoryDictionaries.isEmpty()) return userCategory;

        //性别
        List<Integer> gens = getNode(200, 0, categoryDictionaries).stream().map(ct -> ct.getCategoryId()).collect(Collectors.toList());
        userCategory.setGender(gens);
        //年龄
        List<Integer> ages = getNode(190, 0, categoryDictionaries).stream().map(ct -> ct.getCategoryId()).collect(Collectors.toList());
        userCategory.setAge(ages);
        //教育程度
        List<Integer> edus = getNode(747, 0, categoryDictionaries).stream().map(ct -> ct.getCategoryId()).collect(Collectors.toList());
        userCategory.setEducation(edus);
        //婚姻
        List<Integer> mars = getNode(619, 0, categoryDictionaries).stream().map(ct -> ct.getCategoryId()).collect(Collectors.toList());
        userCategory.setMarriage(mars);
        //所在行业
        List<Integer> careers = getNode(620, 0, categoryDictionaries).stream().map(ct -> ct.getCategoryId()).collect(Collectors.toList());
        userCategory.setCareer(careers);

        return userCategory;
    }

    private RtbMobileDevice transformMobileDevice(Set<CampaignObjective> objectives){
        RtbMobileDevice mobileDevice = null;

        if (!Optional.ofNullable(objectives).isPresent()) return mobileDevice;

        List<CategoryDictionary> categoryDictionaries = objectives.stream()
                .map(campaignObjective -> campaignObjective.getCategoryDictionary())
                .filter(categoryDictionary -> categoryDictionary.getCategoryTag().getTagCode().equals("MOBILE_DEVICE"))
                .collect(Collectors.toList());

        if (categoryDictionaries.isEmpty()) return mobileDevice;

        //营运商
        List<Integer> cars = getNode(1, 0, categoryDictionaries).stream().map(ct -> ct.getCategoryId()%10).collect(Collectors.toList());
        mobileDevice.setCarrier(cars);
        //联网方式
        List<Integer> nets = getNode(2, 0, categoryDictionaries).stream().map(ct -> ct.getCategoryId()%10).collect(Collectors.toList());
        mobileDevice.setNetworkType(nets);
        //操作系统
        List<Integer> oss = getNode(3, 0, categoryDictionaries).stream().map(ct -> ct.getCategoryId()%10).collect(Collectors.toList());
        mobileDevice.setOs(oss);

        return mobileDevice;
    }

    private List<CategoryTree> getNode(int pid, int level, List<CategoryDictionary> datas) {
        int currentLevel = ++level;

        List<CategoryTree> result = datas.stream().filter(dic -> dic.getCategoryParentId()==pid).map(dic ->{

            CategoryTree tree = new CategoryTree();
            tree.setCategoryDictionary(dic);
            tree.setCategoryDictionaryId(dic.getId());
            tree.setCategoryId(dic.getCategoryId());
            tree.setCategoryParentId(dic.getCategoryParentId());
            tree.setLevel(currentLevel);
            tree.setCategory(dic.getCategory());
            tree.setSubs(getNode(tree.getCategoryId(), currentLevel, datas));
            return tree;

        }).collect(Collectors.toList());
        return result;
    }
}
