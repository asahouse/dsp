package org.codeworks.dsp.testing.cache;

import org.codeworks.dsp.utils.EhcacheUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by benjaminkc on 16/12/11.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:config/spring/spring.xml")
@ActiveProfiles("local")
public class CacheTest {

    @Autowired
    private EhcacheUtil ehcacheUtil;

    @Test
    public void test(){
        ehcacheUtil.put("test", "name", "benjamin");
        String name = ehcacheUtil.get("test", "name").toString();
        Assert.assertNotNull(name);
    }
}
