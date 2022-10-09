package org.codeworks.dsp.dao;

import org.codeworks.dsp.model.entities.CampaignObjective;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by benjaminkc on 16/10/21.
 */
public interface CampaignObjectiveRepository extends
        PagingAndSortingRepository<CampaignObjective, Integer>, QueryDslPredicateExecutor<CampaignObjective> {

    @Modifying
    @Query("delete from CampaignObjective co where co.campaignId = ?1 and co.categoryDictionaryId in ?2")
    void deleteOnCampaignIdAndCategoryDictionaryIdIn(Integer cid, List<String> cdIds);

    Long deleteByCampaignId(Integer cid);

    List<CampaignObjective> findByCampaignId(Integer cId);
}
