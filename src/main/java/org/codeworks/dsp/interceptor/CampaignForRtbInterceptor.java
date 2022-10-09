package org.codeworks.dsp.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.codeworks.dsp.model.dto.SubmitCampaign;
import org.codeworks.dsp.model.entities.Campaign;
import org.codeworks.dsp.service.CampaignService;
import org.codeworks.dsp.service.RtbCallService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

/**
 * Created by benjaminkc on 16/10/26.
 */
@Aspect
public class CampaignForRtbInterceptor {

    @Autowired
    private RtbCallService rtbApiService;

    @Autowired
    private CampaignService campaignService;

    @Pointcut("execution(* org.codeworks.dsp.service.impl.CampaignServiceImpl.createCampaign(..))")
    private void createCampaign() {}
    @Around("createCampaign()")
    public Campaign doPostCampaignToRtbAtCreate(ProceedingJoinPoint pjp) throws Throwable{
        Campaign campaign = (Campaign)pjp.proceed(pjp.getArgs());
        sendAndCheckCampaign(campaign);
        return campaign;
    }

    @Pointcut("execution(* org.codeworks.dsp.service.impl.CampaignServiceImpl.updateCampaign(..))")
    private void updateCampaign() {}
    @Around("updateCampaign()")
    public Campaign doPostCampaignToRtbAtModify(ProceedingJoinPoint pjp) throws Throwable{
        Object[] args = pjp.getArgs();

        Campaign orginCampaign = new Campaign();

        Integer argIndex = 1;
        if (args.length>=argIndex && Optional.ofNullable(args[argIndex]).isPresent()) {
            SubmitCampaign argCampagin = (SubmitCampaign) args[argIndex];
            if (Optional.ofNullable(argCampagin.getCampaign()).isPresent()){
                BeanUtils.copyProperties(campaignService.getCampaign(argCampagin.getCampaign().getId()), orginCampaign);
            }
        }


        Campaign modCampaign = (Campaign)pjp.proceed(args);

        //是否修改了<bid,budget,startData,endData,是否在允许时间区间内>
        if (Optional.ofNullable(orginCampaign.getId()).isPresent()){
            //boolean bidCondi = !(Float.floatToIntBits(orginCampaign.getBid()) == Float.floatToIntBits(modCampaign.getBid()));
            BigDecimal orginBid = new BigDecimal(Float.toString(orginCampaign.getBid()));
            BigDecimal modBid = new BigDecimal(Float.toString(modCampaign.getBid()));
            boolean bidCondi = orginBid.compareTo(modBid) != 0;

            //boolean budgetCondi = !(Float.floatToIntBits(orginCampaign.getBudget()) == Float.floatToIntBits(modCampaign.getBudget()));
            BigDecimal orginBudget = new BigDecimal(Float.toString(orginCampaign.getBudget()));
            BigDecimal modBugget = new BigDecimal(Float.toString(modCampaign.getBudget()));
            boolean budgetCondi = orginBudget.compareTo(modBugget) != 0;

            boolean startCondi = !orginCampaign.getStartDate().isEqual(modCampaign.getStartDate());
            boolean endCondi = !orginCampaign.getEndDate().isEqual(modCampaign.getEndDate());
            boolean insideCondi = LocalDate.now().isAfter(orginCampaign.getStartDate()) && LocalDate.now().isBefore(orginCampaign.getEndDate());
            if (bidCondi || budgetCondi || startCondi || endCondi || insideCondi)
                sendAndCheckCampaign(modCampaign);
        }

        return modCampaign;
    }

    private void sendAndCheckCampaign(Campaign campaign){
        rtbApiService.syncCampaign(Collections.singletonList(campaign));
    }
}
