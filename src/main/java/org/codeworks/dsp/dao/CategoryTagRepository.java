package org.codeworks.dsp.dao;

import org.codeworks.dsp.model.entities.CategoryTag;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

/**
 * Created by benjaminkc on 16/10/19.
 */
public interface CategoryTagRepository extends
        PagingAndSortingRepository<CategoryTag, Integer>, QueryDslPredicateExecutor<CategoryTag> {

    Optional<CategoryTag> findByTagCode(String tagCode);
}
