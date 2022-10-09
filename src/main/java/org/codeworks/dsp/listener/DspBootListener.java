package org.codeworks.dsp.listener;

import org.codeworks.dsp.scheduler.JobManager;
import org.codeworks.dsp.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

/**
 * Created by benjaminkc on 16/10/18.
 */
@Service
public class DspBootListener implements ApplicationListener<ContextRefreshedEvent> {

    @Value("${dsp.schedule.active}")
    private boolean isActive;

    @Value("${category.tags.types}")
    private String categoryTags;

    @Autowired
    private JobManager jobManager;

    @Autowired
    private CategoryService categoryService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent evt) {
        if (evt.getApplicationContext().getParent() != null) return;

        try {

            if (isActive) jobManager.run();

            categoryService.initCategoryTags(categoryTags);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
