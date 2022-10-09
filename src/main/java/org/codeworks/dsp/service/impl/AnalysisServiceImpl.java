package org.codeworks.dsp.service.impl;

import com.baidu.statistics.dataapi.om.profile.GetDataCollectionRequest;
import com.baidu.statistics.dataapi.om.profile.GetDataRequest;
import com.baidu.statistics.dataapi.om.profile.GetDataRequestRequisite;
import org.codeworks.dsp.service.AnalysisService;
import org.codeworks.dsp.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by benjaminkc on 16/12/12.
 */
@Service
public class AnalysisServiceImpl implements AnalysisService {

    @Autowired
    private StatisticsService statisticsService;

    @Override
    public List<Map> reportForBaiduOverviewGetTimeTrendRpt(GetDataRequestRequisite requisite) {
        if (!Optional.ofNullable(requisite).isPresent()) return new ArrayList<>();

        requisite.setMethod("overview/getTimeTrendRpt");

        List<GetDataRequest> rqs = new ArrayList<>();

        GetDataRequest rqIP = new GetDataRequest();
        rqIP.setMetrics("ip_count");
        rqs.add(rqIP);

        GetDataRequest rqPV = new GetDataRequest();
        rqPV.setMetrics("pv_count");
        rqs.add(rqPV);

        GetDataRequest rqUV = new GetDataRequest();
        rqUV.setMetrics("visitor_count");
        rqs.add(rqUV);

        GetDataRequest rqBR = new GetDataRequest();
        rqBR.setMetrics("bounce_ratio");
        rqs.add(rqBR);

        GetDataRequest rqAVT = new GetDataRequest();
        rqAVT.setMetrics("avg_visit_time");
        rqs.add(rqAVT);

        GetDataRequest rqTC = new GetDataRequest();
        rqTC.setMetrics("trans_count");
        rqs.add(rqTC);

        GetDataRequest rqCPV = new GetDataRequest();
        rqCPV.setMetrics("contri_pv");
        rqs.add(rqCPV);

        GetDataCollectionRequest body = new GetDataCollectionRequest(requisite, rqs);

        return statisticsService.achieveBaiduDatas(body);
    }
}
