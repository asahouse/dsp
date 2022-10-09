package org.codeworks.dsp.scheduler.jobs;

import org.codeworks.dsp.service.RtbCallService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by benjaminkc on 16/10/26.
 */
public class JobForSyncRtbCampaign implements Job {

    @Autowired
    private RtbCallService rtbApiService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        rtbApiService.scheduleSyncCampaignsOnAllowDate();
    }
}
