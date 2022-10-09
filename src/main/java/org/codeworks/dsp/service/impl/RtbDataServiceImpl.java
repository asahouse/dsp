package org.codeworks.dsp.service.impl;

import org.codeworks.dsp.dao.rtbMQ.RtbCampaignStatsRepository;
import org.codeworks.dsp.dao.rtbMQ.RtbConsumeRepository;
import org.codeworks.dsp.dao.rtbMQ.RtbMonitorRepository;
import org.codeworks.dsp.dao.rtbMQ.RtbStatsRepository;
import org.codeworks.dsp.dao.rtbMQ.base.RtbDataRepository;
import org.codeworks.dsp.model.entities.rtbMQ.RtbCampaignStats;
import org.codeworks.dsp.model.entities.rtbMQ.RtbConsume;
import org.codeworks.dsp.model.entities.rtbMQ.RtbMoniter;
import org.codeworks.dsp.model.entities.rtbMQ.RtbStats;
import org.codeworks.dsp.model.entities.rtbMQ.base.BaseRtbEntity;
import org.codeworks.dsp.rtbCall.dto.campaign.RtbCampaign;
import org.codeworks.dsp.service.RtbDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by benjaminkc on 16/12/22.
 */
@Service
public class RtbDataServiceImpl implements RtbDataService {

    @Autowired
    public RtbCampaignStatsRepository rtbCampaignStatsRepository;
    @Autowired
    public RtbConsumeRepository rtbConsumeRepository;
    @Autowired
    public RtbMonitorRepository rtbMonitorRepository;
    @Autowired
    public RtbStatsRepository rtbStatsRepository;

    public List<RtbCampaignStats> getRtbCampaignStatsDatasWithRange(LocalDate start, LocalDate end){
        return rtbCampaignStatsRepository.findByCompressDateBetween(start, end);
    }

    public Page<RtbCampaignStats> getRtbCampaignStatsDatasWithRange(LocalDate start, LocalDate end, Pageable page){
        return rtbCampaignStatsRepository.findByCompressDateBetween(start, end, page);
    }

    public List<RtbCampaignStats> getRtbCampaignStatsDatasWithData(LocalDate day){
        return rtbCampaignStatsRepository.findByCompressDate(day);
    }

    public Page<RtbCampaignStats> getRtbCampaignStatsDatasWithData(LocalDate day, Pageable page){
        return rtbCampaignStatsRepository.findByCompressDate(day, page);
    }

    public Page<RtbConsume> getRtbConsumeDatasWithRange(LocalDate start, LocalDate end, Pageable page){
        return rtbConsumeRepository.findByCompressDateBetween(start, end, page);
    }
    public Page<RtbConsume> getRtbConsumeDatasWithData(LocalDate day, Pageable page){
        return rtbConsumeRepository.findByCompressDate(day, page);
    }

    public Page<RtbMoniter> getRtbMoniterDatasWithRange(LocalDate start, LocalDate end, Pageable page){
        return rtbMonitorRepository.findByCompressDateBetween(start, end, page);
    }
    public Page<RtbMoniter> getRtbMoniterDatasWithData(LocalDate day, Pageable page){
        return rtbMonitorRepository.findByCompressDate(day, page);
    }

    public Page<RtbStats> getRtbStatsDatasWithRange(LocalDate start, LocalDate end, Pageable page){
        return rtbStatsRepository.findByCompressDateBetween(start, end, page);
    }
    public Page<RtbStats> getRtbStatsDatasWithData(LocalDate day, Pageable page){
        return rtbStatsRepository.findByCompressDate(day, page);
    }

}
