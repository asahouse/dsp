package org.codeworks.dsp.dao.rtbMQ.base;

import org.codeworks.dsp.model.entities.rtbMQ.base.BaseRtbRolloverDataEntity;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by benjaminkc on 16/12/20.
 */
public interface RtbRolloverDataRepository<E extends BaseRtbRolloverDataEntity>{
    List<E> findByTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
}
