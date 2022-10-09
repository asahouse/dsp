package org.codeworks.dsp.model.entities.rtbMQ.base;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

/**
 * Created by benjaminkc on 16/12/19.
 */
@Data
@MappedSuperclass
public abstract class BaseRtbRolloverDataEntity extends AbstractRtbEntity {

    @Column(name = "time", nullable = false)
    private LocalDateTime time;

}
