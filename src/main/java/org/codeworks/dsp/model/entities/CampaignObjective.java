package org.codeworks.dsp.model.entities;

import com.fasterxml.jackson.annotation.JsonView;
import org.codeworks.dsp.model.groups.Update;
import org.codeworks.dsp.model.views.advertiser.ListView;
import org.codeworks.dsp.model.views.campaign.EditView;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by benjaminkc on 16/10/21.
 */
@Entity
@Table(name = "campaign_objective",
        uniqueConstraints = {@UniqueConstraint(columnNames={"campaignId","categoryTagCode","categoryDictionaryId"})})
public class CampaignObjective extends BaseEntity implements Serializable {

    @NotNull(groups = Update.class)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "campaignId", nullable = false, unique = true)
    private Integer campaignId;

    @JsonView(EditView.class)
    @Column(name = "categoryTagCode", nullable = false, unique = true)
    private String categoryTagCode;

    @JsonView(EditView.class)
    @Column(name = "categoryDictionaryId", nullable = false, unique = true)
    private String categoryDictionaryId;

    @Transient
    private CategoryDictionary categoryDictionary;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Integer campaignId) {
        this.campaignId = campaignId;
    }

    public String getCategoryTagCode() {
        return categoryTagCode;
    }

    public void setCategoryTagCode(String categoryTagCode) {
        this.categoryTagCode = categoryTagCode;
    }

    public String getCategoryDictionaryId() {
        return categoryDictionaryId;
    }

    public void setCategoryDictionaryId(String categoryDictionaryId) {
        this.categoryDictionaryId = categoryDictionaryId;
    }

    public CategoryDictionary getCategoryDictionary() {
        return categoryDictionary;
    }

    public void setCategoryDictionary(CategoryDictionary categoryDictionary) {
        this.categoryDictionary = categoryDictionary;
    }
}
