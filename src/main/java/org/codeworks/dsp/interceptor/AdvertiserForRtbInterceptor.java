package org.codeworks.dsp.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.codeworks.dsp.model.entities.Advertiser;
import org.codeworks.dsp.service.AdvertiserService;
import org.codeworks.dsp.service.RtbCallService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by benjaminkc on 16/10/26.
 */
@Aspect
public class AdvertiserForRtbInterceptor {

    @Autowired
    private RtbCallService rtbApiService;

    @Autowired
    private AdvertiserService advertiserService;

    @Pointcut("execution(* org.codeworks.dsp.service.impl.BesApiServiceImpl.scheduleCheckAndUpdateAdvertiser(..))")
    private void scheduleCheckAndUpdateAdvertiser() {}
    @Around("scheduleCheckAndUpdateAdvertiser()")
    public List<Integer> doPostAdvertiserToRtbAtBaiduAudit(ProceedingJoinPoint pjp) throws Throwable{
        List<Integer> ids = (ArrayList)pjp.proceed();
        sendAndCheckAdvertiser(ids);
        return ids;
    }

    @Pointcut("execution(* org.codeworks.dsp.service.impl.AdvertiserServiceImpl.updateAdvertiser(..))")
    private void updateAdvertiser() {}
    @Around("updateAdvertiser()")
    public Advertiser doDeleteAdvertiserToRtbAtModify(ProceedingJoinPoint pjp) throws Throwable{
        Advertiser advertiser = (Advertiser)pjp.proceed();
        deleteAndCheckAdvertiser(Collections.singletonList(advertiser));
        return advertiser;
    }

    private void sendAndCheckAdvertiser(List<Integer> ids){
        if (!Optional.ofNullable(ids).isPresent()) return;

        List<Advertiser> advertisers = advertiserService.getAdvertisers(ids);

        //百度审核状态
        //显示状态
        List<Advertiser> filters = advertisers.stream()
                .filter(ad -> ad.getReview().equals(Advertiser.Review.eligible))
                //.filter(ad -> ad.getStatus().equals(Advertiser.Status.eligible))
                .collect(Collectors.toList());

        if (!filters.isEmpty())
            rtbApiService.syncAdvertiser(filters);
    }

    private void deleteAndCheckAdvertiser(List<Advertiser> advertisers){
        if (!Optional.ofNullable(advertisers).isPresent() || advertisers.isEmpty()) return;

        rtbApiService.deleteAdvertiser(advertisers);
    }
}
