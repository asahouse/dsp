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
@Table(name = "rtb_stats")
public class RtbStats extends BaseRtbEntity {

    @Column(name = "handle")
    private Integer handle;

    @Column(name = "parseError")
    private Integer parseError;

    @Column(name = "drop")
    private Integer drop;

    @Column(name = "noMatch")
    private Integer noMatch;

    @Column(name = "offer")
    private Integer offer;

    @Column(name = "win")
    private Integer win;
}
