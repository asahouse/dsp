package org.codeworks.dsp.dao;

import org.codeworks.dsp.model.entities.Material;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

/**
 * 素材数据库接口
 * Created by Luis on 2016/9/7.
 */
public interface MaterialRepository extends
        PagingAndSortingRepository<Material, Integer>, QueryDslPredicateExecutor<Material>, JpaSpecificationExecutor<Material> {

    Optional<Material> findByIdAndCampaignId(Integer id, Integer cId);

    Optional<Material> findByIdAndAdvertiserIdAndCampaignId(Integer id, Integer advId, Integer cId);

    List<Material> findByIdIn(List<Integer> ids);

    List<Material> findByReviewIs(Integer review);

    Long countByIdIn(List<Integer> ids);

    @Modifying
    @Query("update Material mat set mat.review = ?1 where mat.id in ?2")
    void updateReview(Material.Review review, List<Integer> id);

    @Modifying
    @Query("update Material mat set mat.review = ?1 where mat.id = ?2")
    void updateReview(Material.Review review, Integer id);

    @Modifying
    @Query("update Material mat set mat.refuseReason = ?1 where mat.id = ?2")
    void updateRefuseReason(String refuseReason, int id);

    @Modifying
    @Query("update Material mat set mat.tradeId = ?1 where mat.id = ?2")
    void updateTradeId(int tradeId, int id);
}
