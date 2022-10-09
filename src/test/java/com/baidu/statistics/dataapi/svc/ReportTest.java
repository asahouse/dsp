package com.baidu.statistics.dataapi.svc;

import com.baidu.statistics.dataapi.om.profile.*;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertNotNull;

public class ReportTest extends BaseApiTest {

    @Test
    public void testGetSiteList() throws Exception {
        GetSiteListResponse ret = getSiteList();
        assertNotNull(ret);
    }

    @Test
    public void testGetData() throws Exception {
        GetDataRequest rq = new GetDataRequest();
        rq.setSite_id(2495822);
        rq.setMethod("overview/getTimeTrendRpt");
        rq.setStart_date(20161208);
        rq.setEnd_date(20161209);
        rq.setMetrics("ip_count");

        GetDataResponse ret = getData(rq);
        assertNotNull(ret);
    }

    @Test
    public void testGetCollectionData() throws Exception {

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

        List<GetDataResponse> ret = getCollectionData(rqCol);
        assertNotNull(ret);
    }
}
