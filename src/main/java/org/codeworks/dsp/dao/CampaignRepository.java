package org.codeworks.dsp.dao;

import org.codeworks.dsp.model.entities.Campaign;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 广告活动数据库接口
 * Created by Luis on 2016/9/7.
 */
public interface CampaignRepository extends
        PagingAndSortingRepository<Campaign, Integer>, QueryDslPredicateExecutor<Campaign> {

    Optional<Campaign> findByIdAndAdvertiserId(Integer id, Integer advId);

    List<Campaign> findByAdvertiserId(Integer advId);

    List<Campaign> findByIdIn(List<Integer> ids);

    List<Campaign> findByStartDateBeforeAndEndDateAfterAndSync(LocalDate nowOne, LocalDate nowTwo, Integer sync);

    @org.springframework.data.jpa.repository.Query("select c from Campaign c where c.startDate <= ?1 and c.endDate >= ?2 and c.sync = ?3")
    List<Campaign> findActiveCampaignsOnDate(LocalDate startLimit, LocalDate endLimit, Integer sync);

    @org.springframework.data.jpa.repository.Query("select c from Campaign c where c.startDate <= ?1 and c.endDate >= ?2 and c.status = 0 and c.review = 0")
    List<Campaign> findActiveCampaignsWithinDate(LocalDate startLimit, LocalDate endLimit);

    @EntityGraph(value = "campaign.all", type = EntityGraph.EntityGraphType.FETCH)
    Optional<Campaign> findWithAllByIdAndAdvertiserId(Integer id, Integer advId);

    Long countByIdIn(List<Integer> ids);

    @org.springframework.data.jpa.repository.Query("select c from Campaign c where c.endDate < ?1 and c.sync = ?2")
    List<Campaign> findExpiredCampaigns(LocalDate date, Integer sync);

}
