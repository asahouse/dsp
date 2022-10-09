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
@Table(name = "rtb_consume_rolloverdata")
public class RtbConsumeRolloverData extends BaseRtbRolloverDataEntity {
    @Column(name = "consume")
    private Double consume;
}
