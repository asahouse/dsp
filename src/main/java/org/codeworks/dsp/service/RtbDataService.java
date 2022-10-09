package org.codeworks.dsp.service;

import org.codeworks.dsp.model.entities.rtbMQ.RtbCampaignStats;
import org.codeworks.dsp.model.entities.rtbMQ.RtbConsume;
import org.codeworks.dsp.model.entities.rtbMQ.RtbMoniter;
import org.codeworks.dsp.model.entities.rtbMQ.RtbStats;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by benjaminkc on 16/12/22.
 */
public interface RtbDataService {
    List<RtbCampaignStats> getRtbCampaignStatsDatasWithRange(LocalDate start, LocalDate end);
    Page<RtbCampaignStats> getRtbCampaignStatsDatasWithRange(LocalDate start, LocalDate end, Pageable page);
    List<RtbCampaignStats> getRtbCampaignStatsDatasWithData(LocalDate day);
    Page<RtbCampaignStats> getRtbCampaignStatsDatasWithData(LocalDate day, Pageable page);

    Page<RtbConsume> getRtbConsumeDatasWithRange(LocalDate start, LocalDate end, Pageable page);
    Page<RtbConsume> getRtbConsumeDatasWithData(LocalDate day, Pageable page);

    Page<RtbMoniter> getRtbMoniterDatasWithRange(LocalDate start, LocalDate end, Pageable page);
    Page<RtbMoniter> getRtbMoniterDatasWithData(LocalDate day, Pageable page);

    Page<RtbStats> getRtbStatsDatasWithRange(LocalDate start, LocalDate end, Pageable page);
    Page<RtbStats> getRtbStatsDatasWithData(LocalDate day, Pageable page);
}
