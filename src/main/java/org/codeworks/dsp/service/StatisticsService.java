package org.codeworks.dsp.service;

import com.baidu.statistics.dataapi.om.profile.GetDataRequest;
import com.baidu.statistics.dataapi.om.profile.GetDataCollectionRequest;
import com.baidu.statistics.dataapi.om.profile.Site;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by benjaminkc on 16/12/11.
 *
 * 接入不同统计系统的服务类
 */
@Service
public interface StatisticsService {
    List<Site> achieveBaiduSites();
    Map achieveBaiduData(GetDataRequest request);
    List<Map> achieveBaiduDatas(GetDataCollectionRequest request);
}
