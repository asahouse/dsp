package org.codeworks.dsp.service.impl;

import com.baidu.statistics.dataapi.om.profile.*;
import org.codeworks.dsp.exception.BaiduStatisticsException;
import org.codeworks.dsp.exception.ErrorCodes;
import org.codeworks.dsp.service.StatisticsService;
import org.codeworks.dsp.statistic.BaiduStatisticsApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by benjaminkc on 16/12/11.
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {
    @Autowired
    private BaiduStatisticsApi baiduStatisticsApi;

    @Override
    public List<Site> achieveBaiduSites(){
        GetSiteListResponse rq = baiduStatisticsApi.requestSiteList();
        if (!Optional.ofNullable(rq).isPresent()) {
            throw new BaiduStatisticsException(ErrorCodes.baidu_statistics_sites_empty);
        }

        return rq.getList();
    }

    @Override
    public Map achieveBaiduData(GetDataRequest request){
        GetDataResponse rq = baiduStatisticsApi.requestData(request);
        if (!Optional.ofNullable(rq).isPresent()) {
            throw new BaiduStatisticsException(ErrorCodes.baidu_statistics_data_wrong);
        }
        return rq.getResult();
    }

    @Override
    public List<Map> achieveBaiduDatas(GetDataCollectionRequest request){
        List<GetDataResponse> rqs;
        try {
            rqs = baiduStatisticsApi.requestCollectionData(request);
        } catch (Exception e) {
            throw new BaiduStatisticsException(ErrorCodes.baidu_statistics_data_collection_error, new Object[]{"achieveBaiduDatas"});
        }

        if (!Optional.ofNullable(rqs).isPresent()
                || rqs.isEmpty()) {
            throw new BaiduStatisticsException(ErrorCodes.baidu_statistics_data_collection_wrong);
        }

        return rqs.stream().map(GetDataResponse::getResult).collect(Collectors.toList());
    }
}
