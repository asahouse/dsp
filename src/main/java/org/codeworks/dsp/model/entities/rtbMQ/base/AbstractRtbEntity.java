package org.codeworks.dsp.model.entities.rtbMQ.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by benjaminkc on 16/12/19.
 */
@Data
@MappedSuperclass
public abstract class AbstractRtbEntity implements Serializable, RtbEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "createTime", nullable = false)
    private LocalDateTime createTime;
}
