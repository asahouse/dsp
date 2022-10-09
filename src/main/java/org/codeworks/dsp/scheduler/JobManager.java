package org.codeworks.dsp.scheduler;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by benjaminkc on 16/10/17.
 */
@Component
@DisallowConcurrentExecution
public class JobManager {

    @Autowired
    private SchedulerFactoryBean scheduler;

    private static String CLASS_PREFIX_PATH = "org.codeworks.dsp.scheduler.jobs.";

    private static List<ScheduleJob> jobs = new ArrayList<>();
    static {
        //TODO 应设计涉及UI及使用界面的, 可控计划任务
        ScheduleJob jobForAuditAdvertiser = new ScheduleJob();
        jobForAuditAdvertiser.setJobId("10001");
        jobForAuditAdvertiser.setJobStatus("1");
        jobForAuditAdvertiser.setDesc("查询审核广告主状态");
        jobForAuditAdvertiser.setJobName("audit_advertiser");
        jobForAuditAdvertiser.setJobGroup("adx");
        jobForAuditAdvertiser.setClassName("JobForAuditAdvertiser");
        jobForAuditAdvertiser.setCronExpression("0 0/5 * * * ?");
        jobs.add(jobForAuditAdvertiser);

        ScheduleJob jobForAuditMaterial = new ScheduleJob();
        jobForAuditMaterial.setJobId("10002");
        jobForAuditMaterial.setJobStatus("1");
        jobForAuditMaterial.setDesc("查询审核物料状态");
        jobForAuditMaterial.setJobName("audit_material");
        jobForAuditMaterial.setJobGroup("adx");
        jobForAuditMaterial.setClassName("JobForAuditMaterial");
        jobForAuditMaterial.setCronExpression("0 0/5 * * * ?");
        jobs.add(jobForAuditMaterial);

        ScheduleJob jobForTradeModifiedMaterial = new ScheduleJob();
        jobForTradeModifiedMaterial.setJobId("10003");
        jobForTradeModifiedMaterial.setJobStatus("1");
        jobForTradeModifiedMaterial.setDesc("查询并更新物料变更情况");
        jobForTradeModifiedMaterial.setJobName("trade_modified_material");
        jobForTradeModifiedMaterial.setJobGroup("adx");
        jobForTradeModifiedMaterial.setClassName("JobForTradeModifiedMaterial");
        jobForTradeModifiedMaterial.setCronExpression("0 0/5 * * * ?");
        jobs.add(jobForTradeModifiedMaterial);

        ScheduleJob jobForSyncRtbCampaign = new ScheduleJob();
        jobForSyncRtbCampaign.setJobId("10004");
        jobForSyncRtbCampaign.setJobStatus("1");
        jobForSyncRtbCampaign.setDesc("同步活动给RBT");
        jobForSyncRtbCampaign.setJobName("sync_campaign");
        jobForSyncRtbCampaign.setJobGroup("rtb");
        jobForSyncRtbCampaign.setClassName("JobForSyncRtbCampaign");
        jobForSyncRtbCampaign.setCronExpression("0 0 0 * * ?");
        jobs.add(jobForSyncRtbCampaign);

        ScheduleJob jobForSendAdvertiserToAdx = new ScheduleJob();
        jobForSendAdvertiserToAdx.setJobId("10005");
        jobForSendAdvertiserToAdx.setJobStatus("1");
        jobForSendAdvertiserToAdx.setDesc("发送广告主信息到adx");
        jobForSendAdvertiserToAdx.setJobName("send_advertiser_adx");
        jobForSendAdvertiserToAdx.setJobGroup("adx");
        jobForSendAdvertiserToAdx.setClassName("JobForSendAdvertiserToAdx");
        jobForSendAdvertiserToAdx.setCronExpression("0/10 * * * * ?");
        jobs.add(jobForSendAdvertiserToAdx);

//        ScheduleJob jobForBaiduOverviewGetTimeTrendRpt = new ScheduleJob();
//        jobForBaiduOverviewGetTimeTrendRpt.setJobId("10006");
//        jobForBaiduOverviewGetTimeTrendRpt.setJobStatus("1");
//        jobForBaiduOverviewGetTimeTrendRpt.setDesc("定时获取百度统计数据报告-趋势报告");
//        jobForBaiduOverviewGetTimeTrendRpt.setJobName("achieve_baidu_report_overview");
//        jobForBaiduOverviewGetTimeTrendRpt.setJobGroup("baidustatistic");
//        jobForBaiduOverviewGetTimeTrendRpt.setClassName("JobForBaiduOverviewGetTimeTrendRpt");
//        jobForBaiduOverviewGetTimeTrendRpt.setCronExpression("*/55 * * * * ");//每55分钟进行一次
//        jobs.add(jobForSendAdvertiserToAdx);
    }

    public void run() throws SchedulerException,ClassNotFoundException {

        Scheduler scheduler = this.scheduler.getScheduler();
        for (ScheduleJob job : jobs) {

            TriggerKey triggerKey = TriggerKey.triggerKey(job.getJobName(), job.getJobGroup());
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);

            if (null == trigger) {
                Class classForJob = Class.forName(CLASS_PREFIX_PATH + job.getClassName());

                JobDetail jobDetail = JobBuilder.newJob(classForJob)
                        .withIdentity(job.getJobName(), job.getJobGroup()).build();

                jobDetail.getJobDataMap().put("scheduleJob", job);//传递数据给Job

                CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());

                trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey)
                        .withSchedule(scheduleBuilder).build();

                scheduler.scheduleJob(jobDetail, trigger);

            } else {

                CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job
                        .getCronExpression());

                trigger = trigger.getTriggerBuilder().withIdentity(triggerKey)
                        .withSchedule(scheduleBuilder).build();

                scheduler.rescheduleJob(triggerKey, trigger);
            }

        }
    }

}
