package org.codeworks.dsp.service.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.codeworks.dsp.dao.CampaignObjectiveRepository;
import org.codeworks.dsp.dao.CampaignRepository;
import org.codeworks.dsp.dao.CategoryDictionaryRepository;
import org.codeworks.dsp.dao.CategoryTagRepository;
import org.codeworks.dsp.exception.CampaignException;
import org.codeworks.dsp.exception.CategoryException;
import org.codeworks.dsp.exception.ErrorCodes;
import org.codeworks.dsp.model.dto.SubmitCampaign;
import org.codeworks.dsp.model.entities.*;
import org.codeworks.dsp.model.entities.Campaign.Review;
import org.codeworks.dsp.service.CampaignService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static org.codeworks.dsp.model.entities.QCampaign.campaign;

/**
 * 广告活动业务接口实现
 * Created by Luis on 2016/9/9.
 */
@Service
public class CampaignServiceImpl implements CampaignService {

    @Autowired
    private CampaignRepository campaignRepo;

    @Autowired
    private CampaignObjectiveRepository camObjRepo;

    @Autowired
    private CategoryDictionaryRepository cateRepo;

    @Autowired
    private CategoryTagRepository tagRepo;

    @Override
    public Campaign createCampaign(Integer advId, SubmitCampaign submitCampaign, Boolean isAdmin) {
        Advertiser loginAdv = new Advertiser(advId);

        if (!Optional.ofNullable(submitCampaign.getCampaign()).isPresent())
            throw new CategoryException(ErrorCodes.no_such_campaign);

        Campaign campaign = new Campaign();

        if (isAdmin)
            BeanUtils.copyProperties(submitCampaign.getCampaign(), campaign, "review");
        else
            BeanUtils.copyProperties(submitCampaign.getCampaign(), campaign, "review", "analysisCode", "analysisSiteId");

        campaign.setAdvertiser(loginAdv);
        campaign.setCreateDate(LocalDateTime.now());
        campaign.setReview(Review.eligible);
        Campaign saved = campaignRepo.save(campaign);

        if (Optional.ofNullable(submitCampaign.getCampaignObjectives()).isPresent()){
            submitCampaign.getCampaignObjectives().stream().forEach(co -> {
                String tagCode = co.getCategoryTagCode();
                co.getCategoryDictionaryIds().stream().forEach(id -> {
                    CampaignObjective object = new CampaignObjective();
                    object.setCategoryTagCode(tagCode);
                    object.setCampaignId(campaign.getId());
                    object.setCategoryDictionaryId(id);
                    camObjRepo.save(object);
                });
            });
        }

        return saved;
    }

    @Override
    public Campaign updateCampaign(Integer advId, SubmitCampaign submitCampaign, Boolean isAdmin) {
        Optional<Campaign> dbCampaignOpt = campaignRepo.findWithAllByIdAndAdvertiserId(
                submitCampaign.getCampaign().getId(), advId);

        if (dbCampaignOpt.isPresent()) {
            Campaign dbCampaign = dbCampaignOpt.get();
            Advertiser loginAdv = new Advertiser(advId);

            if (isAdmin)
                BeanUtils.copyProperties(submitCampaign.getCampaign(), dbCampaign, "review", "createDate");
            else
                BeanUtils.copyProperties(submitCampaign.getCampaign(), dbCampaign, "review", "createDate", "analysisCode", "analysisSiteId");

            dbCampaign.setAdvertiser(loginAdv);
            dbCampaign.setTargetLocation(submitCampaign.getCampaign().getTargetLocationString());
            Campaign saved = campaignRepo.save(dbCampaign);

            if (!Optional.ofNullable(submitCampaign.getCampaignObjectives()).isPresent()) return saved;

            List<String> presentCDIdsInCO = camObjRepo.findByCampaignId(saved.getId()).stream()
                    .map(co -> co.getCategoryDictionaryId())
                    .collect(Collectors.toList());
            List<String> submitCDIdsInCO = submitCampaign.getCampaignObjectives().stream()
                    .flatMap(co -> co.getCategoryDictionaryIds().stream())
                    .collect(Collectors.toList());

            //已有差集删除
            List<String> deleteCDIds = diff(presentCDIdsInCO, submitCDIdsInCO);
            if (!deleteCDIds.isEmpty())
                camObjRepo.deleteOnCampaignIdAndCategoryDictionaryIdIn(saved.getId(), deleteCDIds);

            //新增差集增加
            List<String> diffForInsertCDIds = diff(submitCDIdsInCO, presentCDIdsInCO);
            if (!diffForInsertCDIds.isEmpty()) {
                submitCampaign.getCampaignObjectives().stream().forEach(co -> {
                    String tagCode = co.getCategoryTagCode();

                    List<String> insertCDIds = co.getCategoryDictionaryIds().stream()
                            .filter(id -> diffForInsertCDIds.contains(id)).collect(Collectors.toList());

                    insertCDIds.stream().forEach(id -> {
                        CampaignObjective object = new CampaignObjective();
                        object.setCategoryTagCode(tagCode);
                        object.setCampaignId(saved.getId());
                        object.setCategoryDictionaryId(id);
                        camObjRepo.save(object);
                    });
                });
            }

            return saved;
        } else
            throw new CampaignException(ErrorCodes.no_such_campaign);
    }

    @Override
    public Page<Campaign> getCampaignsAtAdvId(Integer advId, Map<String, Object> params, Pageable page) {
        params.put("advertiser.id", advId);
        Optional<Predicate> predicate = buildCampaignPredicate(params);
        return campaignRepo.findAll(predicate.get(), page);
    }

    @Override
    public Page<Campaign> getCampaigns(Map<String, Object> params, Pageable page){
        Optional<Predicate> predicate = buildCampaignPredicate(params);
        return campaignRepo.findAll(predicate.get(), page);
    }

    @Override
    public Campaign getCampaign(Integer id, Integer advId) {
        Optional<Campaign> dbCampaignOpt = campaignRepo.findByIdAndAdvertiserId(id, advId);
        if (dbCampaignOpt.isPresent())
            return dbCampaignOpt.get();
        else
            throw new CampaignException(ErrorCodes.no_such_campaign);
    }

    @Override
    public Campaign getCampaign(Integer id){
        Optional<Campaign> dbCampaignOpt = Optional.ofNullable(campaignRepo.findOne(id));
        if (dbCampaignOpt.isPresent())
            return dbCampaignOpt.get();
        else
            throw new CampaignException(ErrorCodes.no_such_campaign);
    }

    @Override
    public Campaign getCampaignWithAll(Integer id, Integer advId) {
        Optional<Campaign> dbCampaignOpt = campaignRepo.findWithAllByIdAndAdvertiserId(id, advId);
        if (dbCampaignOpt.isPresent())
            return dbCampaignOpt.get();
        else
            throw new CampaignException(ErrorCodes.no_such_campaign);
    }

    @Override
    public Long countCampaign(Map<String, Object> params) {
        Optional<Predicate> predicateOpt = buildCampaignPredicate(params);
        if (predicateOpt.isPresent())
            return campaignRepo.count(predicateOpt.get());
        else
            return 0L;
    }

    @Override
    public Long countTotal(){
        return campaignRepo.count();
    }


    private Optional<Predicate> buildCampaignPredicate(Map<String, Object> params) {
        BooleanBuilder builder = new BooleanBuilder();
        QCampaign qCampaign = campaign;

        if (params.containsKey("advertiser.id"))
            builder.and(qCampaign.advertiser.id.eq((Integer) params.get("advertiser.id")));
        if (params.containsKey("status"))
            builder.and(qCampaign.status.eq(Integer.parseInt((String) params.get("status"))));
        if (params.containsKey("review"))
            builder.and(qCampaign.review.eq(Integer.parseInt((String) params.get("review"))));
        if (params.containsKey("name"))
            builder.and(qCampaign.name.likeIgnoreCase(String.format("%%s%%", params.get("name"))));
        if (params.containsKey("createTime between")) {
            Optional<String> date = Optional.ofNullable((String) params.get("createTime between"));
            Map<String, LocalDateTime> splitDate = this.splitStringToTime(date.get());
            builder.and(qCampaign.createDate.between(splitDate.get("fromDate"), splitDate.get("toDate")));
        }

        return Optional.ofNullable(builder);
    }

    /**
     * 集合差集
     * @param ls
     * @param ls2
     * @return
     */
    private List diff(List ls, List ls2) {
        List list = new ArrayList(Arrays.asList(new Object[ls.size()]));
        Collections.copy(list, ls);
        list.removeAll(ls2);
        return list;
    }

    private Map<String, LocalDateTime> splitStringToTime(String date){
        Map<String, LocalDateTime> result = new HashMap<>();
        if (StringUtils.isNotBlank(date) && date.indexOf("#")!=-1){
            int index = date.indexOf("#");
            String fromDateStr = date.substring(0, index);
            String toDateStr = date.substring(index+1);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            LocalDateTime fromDate = LocalDateTime.parse(fromDateStr, formatter);
            LocalDateTime toDate = LocalDateTime.parse(toDateStr, formatter);

            result.put("fromDate",fromDate);
            result.put("toDate",toDate);
        }
        return result;
    }
}
