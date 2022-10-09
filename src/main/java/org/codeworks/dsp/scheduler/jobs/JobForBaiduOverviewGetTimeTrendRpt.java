package org.codeworks.dsp.scheduler.jobs;

import com.baidu.statistics.dataapi.om.profile.GetDataRequestRequisite;
import org.codeworks.dsp.service.AnalysisService;
import org.codeworks.dsp.service.BesApiService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by benjaminkc on 16/10/17.
 */
public class JobForBaiduOverviewGetTimeTrendRpt implements Job {

    @Autowired
    private AnalysisService analysisService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
//        GetDataRequestRequisite requisite = new GetDataRequestRequisite();
//        analysisService.reportForBaiduOverviewGetTimeTrendRpt(requisite);
    }
}
