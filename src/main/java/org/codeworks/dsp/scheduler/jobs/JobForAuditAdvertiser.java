package org.codeworks.dsp.scheduler.jobs;

import org.codeworks.dsp.service.BesApiService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by benjaminkc on 16/10/17.
 */
public class JobForAuditAdvertiser implements Job {

    @Autowired
    private BesApiService besApiService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        besApiService.scheduleCheckAndUpdateAdvertiser();
    }
}
