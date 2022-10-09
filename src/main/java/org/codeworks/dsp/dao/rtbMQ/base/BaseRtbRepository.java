package org.codeworks.dsp.dao.rtbMQ.base;

import org.codeworks.dsp.model.entities.rtbMQ.base.AbstractRtbEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by benjaminkc on 16/12/19.
 */
public interface BaseRtbRepository<E extends AbstractRtbEntity> extends
        PagingAndSortingRepository<E, Integer>, JpaRepository<E, Integer> {
}
