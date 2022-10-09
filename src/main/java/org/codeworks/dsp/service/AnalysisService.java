package org.codeworks.dsp.service;

import com.baidu.statistics.dataapi.om.profile.GetDataRequestRequisite;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by benjaminkc on 16/12/12.
 *
 * 产出基于各统计系统的报告汇总数据
 */
@Service
public interface AnalysisService {

    /**
     * 百度 趋势数据
     * @param requisite
     * @return
     */
    List<Map> reportForBaiduOverviewGetTimeTrendRpt(GetDataRequestRequisite requisite);
}
