package org.codeworks.dsp.testing.statistics;

import com.baidu.statistics.dataapi.om.profile.*;
import org.codeworks.dsp.service.StatisticsService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

/**
 * Created by benjaminkc on 16/12/11.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:config/spring/spring.xml")
@ActiveProfiles("local")
public class StatisticsTest {

    @Autowired
    private StatisticsService statisticsService;

    @Test
    public void testSites(){
        List<Site> sites = null;
        try {
            sites = statisticsService.achieveBaiduSites();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(sites);
    }

    @Test
    public void testData(){
        Map data = null;
        try {
            GetDataRequest rq = new GetDataRequest();
            rq.setSite_id(2495822);
            rq.setStart_date(20161213);
            rq.setEnd_date(20161213);
            rq.setStart_date2(20161213);
            rq.setEnd_date2(20161213);
            rq.setMethod("overview/getCommonTrackRpt");
            rq.setMetrics("pv_count");

            data = statisticsService.achieveBaiduData(rq);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(data);
    }

    @Test
    public void testDatas(){
        List<Map> data = null;
        try {
            GetDataRequestRequisite rr = new GetDataRequestRequisite();
            rr.setSite_id(2495822);
            rr.setStart_date(20161208);
            rr.setEnd_date(20161209);

            GetDataRequest rq = new GetDataRequest();
            rq.setMethod("overview/getTimeTrendRpt");
            rq.setMetrics("ip_count");

            GetDataRequest rq2 = new GetDataRequest();
            rq2.setMethod("overview/getTimeTrendRpt");
            rq2.setMetrics("ip_count");

            GetDataCollectionRequest rqCol = new GetDataCollectionRequest(rr,rq,rq2);

            data = statisticsService.achieveBaiduDatas(rqCol);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(data);
    }

}
