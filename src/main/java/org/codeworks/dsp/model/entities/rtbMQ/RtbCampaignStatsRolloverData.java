package org.codeworks.dsp.model.entities.rtbMQ;

import lombok.Data;
import org.codeworks.dsp.model.entities.rtbMQ.base.BaseRtbRolloverDataEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by benjaminkc on 16/12/19.
 */
@Data
@Entity
@Table(name = "rtb_campaign_rolloverdata")
public class RtbCampaignStatsRolloverData extends BaseRtbRolloverDataEntity {

    @Column(name = "imp")
    private Integer imp;

    @Column(name = "click")
    private Integer click;

    @Column(name = "offer")
    private Integer offer;

    @Column(name = "win")
    private Integer win;

    @Column(name = "consume")
    private Double consume;

    @Column(name = "campaignId")
    private Integer campaignId;
}
