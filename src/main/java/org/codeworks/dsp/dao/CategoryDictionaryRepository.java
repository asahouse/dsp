package org.codeworks.dsp.dao;

import org.codeworks.dsp.model.entities.CategoryDictionary;
import org.codeworks.dsp.model.entities.CategoryTag;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Created by benjaminkc on 16/10/19.
 */
public interface CategoryDictionaryRepository extends
        PagingAndSortingRepository<CategoryDictionary, Integer>, QueryDslPredicateExecutor<CategoryDictionary> {

    @EntityGraph(value = "category.all", type = EntityGraph.EntityGraphType.FETCH)
    Optional<CategoryDictionary> findById(String id);

    List<CategoryDictionary> findByIdIn(List<String> ids);

    @Modifying
    @Query("delete from CategoryDictionary cd where cd.categoryTag = ?1")
    void deleteByTagCode(CategoryTag tag);

    Long deleteByCategoryTagTagCode(String tagCode);
}
