package org.codeworks.dsp.scheduler;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.scheduling.quartz.AdaptableJobFactory;

/**
 * Created by benjaminkc on 16/10/18.
 *
 * Spring 会优先执行自定义JobFactory.
 */
public class JobFactory extends AdaptableJobFactory {

    @Autowired
    private AutowireCapableBeanFactory capableBeanFactory;//spring自动注入

    protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
        Object jobInstance = super.createJobInstance(bundle);

        capableBeanFactory.autowireBean(jobInstance);

        return jobInstance;
    }
}
