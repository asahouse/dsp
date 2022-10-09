package org.codeworks.dsp.model.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import org.codeworks.dsp.model.entities.Material;
import org.codeworks.dsp.model.entities.Material.Review;

import java.io.Serializable;

/**
 * 审核物料包装
 * Created by Luis on 2016/9/28.
 */
public class AuditMaterial implements Serializable {

    private Integer id;

    private Integer review;

    private Integer tradeId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public AuditReview getReview() {
        return AuditReview.fromValue(review);
    }

    public void setReview(AuditReview review) {
        this.review = review.toValue();
    }

    public Integer getTradeId() {
        return tradeId;
    }

    public void setTradeId(Integer tradeId) {
        this.tradeId = tradeId;
    }



    public enum AuditReview {
        eligible(0), ineligible(1);

        private Integer value;

        AuditReview(Integer value) {
            this.value = value;
        }

        public static AuditMaterial.AuditReview fromValue(Integer value) {
            if (value != null) {
                for (AuditMaterial.AuditReview review : values()) {
                    if (review.value.equals(value)) {
                        return review;
                    }
                }
            }
            return null;
        }

        @JsonValue
        public Integer toValue() {
            return value;
        }
    }
}
