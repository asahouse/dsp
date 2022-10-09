package org.codeworks.dsp.model.entities.rtbMQ;

import lombok.Data;
import org.codeworks.dsp.model.entities.rtbMQ.base.BaseRtbEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by benjaminkc on 16/12/19.
 */
@Data
@Entity
@Table(name = "rtb_monitor")
public class RtbMoniter extends BaseRtbEntity {

    @Column(name = "imp")
    private Integer imp;

    @Column(name = "click")
    private Integer click;

}
