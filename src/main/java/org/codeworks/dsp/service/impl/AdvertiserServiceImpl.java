package org.codeworks.dsp.service.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.apache.commons.codec.digest.DigestUtils;
import org.codeworks.dsp.dao.AdvertiserRepository;
import org.codeworks.dsp.exception.AdvertiserException;
import org.codeworks.dsp.exception.ErrorCodes;
import org.codeworks.dsp.model.entities.Advertiser;
import org.codeworks.dsp.model.entities.Advertiser.Review;
import org.codeworks.dsp.model.entities.QAdvertiser;
import org.codeworks.dsp.service.AdvertiserService;
import org.codeworks.dsp.service.BesApiService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.codeworks.dsp.model.entities.QAdvertiser.advertiser;

/**
 * 广告主业务接口实现
 * Created by Luis on 2016/8/23.
 */
@Service
public class AdvertiserServiceImpl implements AdvertiserService {

    @Autowired
    private AdvertiserRepository advRepo;

    @Autowired
    private BesApiService besApiService;

    @Override
    public void createAdvertiser(Advertiser adv) {
        if (advRepo.exists(advertiser.userName.eq(adv.getUserName())))
            throw new AdvertiserException(ErrorCodes.duplicate_advertiser);

        adv.setCreateDate(LocalDateTime.now());
        adv.setReview(Review.pending);
        advRepo.save(adv);

        if (adv.getSendAdx().equals(Advertiser.SendAdx.no)) return;

        if (besApiService.addAdvertiser(adv)) {
            adv.setReview(Review.processing);
        }else{
            adv.setReview(Review.waitingSubmit);
        }

        advRepo.save(adv);
    }

    @Override
    public Advertiser updateAdvertiser(Advertiser adv) {
        Optional<Advertiser> dbAdvOpt = advRepo.getById(adv.getId());
        if (!dbAdvOpt.isPresent()) throw new AdvertiserException(ErrorCodes.no_such_advertiser);

        Advertiser dbAdv = dbAdvOpt.get();

        if (dbAdv.getReview().equals(Review.processing))
            throw new AdvertiserException(ErrorCodes.can_not_modify_qualification_advertiser);

        BeanUtils.copyProperties(adv, dbAdv, "userName", "review", "balance", "createDate", "lastLoginDate");

        dbAdv.setReview(Review.pending);
        advRepo.save(dbAdv);

        if (adv.getSendAdx().equals(Advertiser.SendAdx.yes)){

            if (!this.isSameAdvertiserQualification(dbAdv, adv)) {
                if (besApiService.updateAdvertiser(dbAdv)) {
                    dbAdv.setReview(Review.processing);
                }else{
                    dbAdv.setReview(Review.waitingModify);
                }

                advRepo.save(dbAdv);
            }
        }

        return dbAdv;
    }

    @Override
    public void deleteAdvertiser(Integer id) {
        advRepo.delete(id);
    }

    @Override
    public Advertiser getAdvertiser(Integer id) {
        Optional<Advertiser> adv = advRepo.getById(id);
        if (!adv.isPresent())
            throw new AdvertiserException(ErrorCodes.no_such_advertiser);

        return adv.get();
    }

    @Override
    public Page<Advertiser> getAdvertisers(Map<String, Object> params, Pageable page) {
        Optional<Predicate> predicateOpt = buildAdvertiserPredicate(params);
        if (predicateOpt.isPresent())
            return advRepo.findAll(predicateOpt.get(), page);
        else
            return advRepo.findAll(page);
    }

    @Override
    public List<Advertiser> getAdvertisers(List<Integer> ids){
        return advRepo.findByIdIn(ids);
    }

    @Override
    public Long countAdvertiser(Map<String, Object> params){
        Optional<Predicate> predicateOpt = buildAdvertiserPredicate(params);
        if (predicateOpt.isPresent())
            return advRepo.count(predicateOpt.get());
        else
            return 0L;
    }

    @Override
    public Long countAll(){
        return advRepo.count();
    }

    private Optional<Predicate> buildAdvertiserPredicate(Map<String, Object> params) {
        if (params == null || params.size() == 0)
            return Optional.empty();
        BooleanBuilder builder = new BooleanBuilder();
        QAdvertiser qAdv = QAdvertiser.advertiser;

        if (params.containsKey("isAdmin"))
            builder.and(qAdv.isAdmin.eq(Boolean.valueOf((String) params.get("isAdmin"))));
        if (params.containsKey("review"))
            builder.and(qAdv.review.eq(Integer.valueOf((String) params.get("review"))));
        if (params.containsKey("status"))
            builder.and(qAdv.status.eq(Integer.valueOf((String) params.get("status"))));
        if (params.containsKey("userName"))
            builder.and(qAdv.userName.likeIgnoreCase(String.format("%%s%%", params.get("userName"))));
        if (params.containsKey("email"))
            builder.and(qAdv.email.likeIgnoreCase(String.format("%%s%%", params.get("email"))));

        return Optional.ofNullable(builder);
    }

    private Boolean isSameAdvertiserQualification(Advertiser A, Advertiser B){
        StringBuffer dbQua = new StringBuffer();
        dbQua.append(A.getQualification().getCompanyName());
        dbQua.append(A.getQualification().getSiteName());
        dbQua.append(A.getQualification().getOfficialSite());
        String dbQuaMD5 = DigestUtils.md5Hex(dbQua.toString());

        StringBuffer qua = new StringBuffer();
        qua.append(B.getQualification().getCompanyName());
        qua.append(B.getQualification().getSiteName());
        qua.append(B.getQualification().getOfficialSite());
        String quaMD5 = DigestUtils.md5Hex(qua.toString());

        return dbQuaMD5.equals(quaMD5);
    }
}
