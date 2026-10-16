package org.codeworks.dsp.dao.rtbMQ;

import org.codeworks.dsp.dao.rtbMQ.base.BaseRtbRepository;
import org.codeworks.dsp.dao.rtbMQ.base.RtbDataRepository;
import org.codeworks.dsp.model.entities.rtbMQ.RtbCampaignStats;

import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;

/**
 * Created by benjaminkc on 16/12/19.
 */
public interface RtbCampaignStatsRepository extends BaseRtbRepository<RtbCampaignStats>, RtbDataRepository {

    @Query("select coalesce(sum(s.consume), 0.0) from RtbCampaignStats s where s.campaignId = ?1")
    Double sumConsumeByCampaignId(Integer campaignId);

    @Query("select coalesce(sum(s.consume), 0.0) from RtbCampaignStats s where s.campaignId = ?1 and s.compressDate = ?2")
    Double sumConsumeByCampaignIdAndDate(Integer campaignId, LocalDate date);
}
