package org.codeworks.dsp.dao;


import org.codeworks.dsp.model.entities.Advertiser;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 广告主数据库接口
 * Created by Luis on 2016/8/23.
 */
public interface AdvertiserRepository extends
        PagingAndSortingRepository<Advertiser, Integer>, QueryDslPredicateExecutor<Advertiser> {

    @EntityGraph(value = "advertiser.detail", type = EntityGraph.EntityGraphType.FETCH)
    Optional<Advertiser> getById(Integer id);

    @EntityGraph(value = "advertiser.detail", type = EntityGraph.EntityGraphType.FETCH)
    Optional<Advertiser> getByUserNameAndPassword(String userName, String password);

    List<Advertiser> findByReviewIs(Integer review);

    List<Advertiser> findByIdIn(List<Integer> ids);

    Long countByIdIn(List<Integer> ids);

    @Modifying
    @Query("update Advertiser adv set adv.lastLoginDate = ?1 where adv.id = ?2")
    void updateLastLoginDate(LocalDateTime now, Integer id);

    @Modifying
    @Query("update Advertiser adv set adv.review = ?1 where adv.id in ?2")
    void updateReview(Advertiser.Review review, List<Integer> id);

    @Modifying
    @Query("update Advertiser adv set adv.review = ?1 where adv.id = ?2")
    void updateReview(Advertiser.Review review, Integer id);

    @Modifying
    @Query("update Advertiser adv set adv.refuseReason = ?1 where adv.id = ?2")
    void updateRefuseReason(String refuseReason, int id);
}
