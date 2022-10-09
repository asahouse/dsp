package org.codeworks.dsp.model.dto;

import java.util.List;

/**
 * Created by benjaminkc on 16/10/21.
 */
public class SubmitCampaignObjective {

    private String categoryTagCode;

    List<String> categoryDictionaryIds;

    public String getCategoryTagCode() {
        return categoryTagCode;
    }

    public void setCategoryTagCode(String categoryTagCode) {
        this.categoryTagCode = categoryTagCode;
    }

    public List<String> getCategoryDictionaryIds() {
        return categoryDictionaryIds;
    }

    public void setCategoryDictionaryIds(List<String> categoryDictionaryIds) {
        this.categoryDictionaryIds = categoryDictionaryIds;
    }
}
