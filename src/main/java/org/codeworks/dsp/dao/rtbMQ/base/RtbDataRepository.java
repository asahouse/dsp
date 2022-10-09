package org.codeworks.dsp.dao.rtbMQ.base;

import org.codeworks.dsp.model.entities.rtbMQ.base.BaseRtbEntity;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Created by benjaminkc on 16/12/20.
 */
public interface RtbDataRepository<E extends BaseRtbEntity> {
    Optional<E> findOneByCompressHourAndCompressDate(Integer hour, LocalDate date);
    Page<E> findByCompressHourAndCompressDate(Integer hour, LocalDate data, Pageable pageable);
    Page<E> findByCompressDate(LocalDate data, Pageable pageable);
    List<E> findByCompressDate(LocalDate data);
    Page<E> findByCompressDateBetween(LocalDate start, LocalDate end, Pageable pageable);
    List<E> findByCompressDateBetween(LocalDate start, LocalDate end);
}
