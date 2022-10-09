package org.codeworks.dsp.service.specification;

import org.codeworks.dsp.model.entities.Advertiser;
import org.codeworks.dsp.model.entities.Material;
import org.codeworks.dsp.model.entities.Material.Review;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 物料参数规范
 * Created by Luis on 2016/9/26.
 */
public class MaterialSpecification {

    public static Specification<Material> auditList(Map<String, Object> params) {
        return (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (Long.class != cq.getResultType())
                root.fetch("advertiser", JoinType.INNER);

            if (params.containsKey("review")) {
                Integer pReview = Integer.parseInt((String) params.get("review"));
                Path<Integer> review = root.get("review");
                if (pReview.equals(Review.pending))
                    predicates.add(review.in(Review.pending.toValue(), Review.updatePending.toValue()));
                else
                    predicates.add(cb.equal(review, pReview));
            }

            if (params.containsKey("adv.userName")) {
                Join<Material, Advertiser> adv = root.join("advertiser", JoinType.INNER);
                Path<String> advUserName = adv.get("userName");
                predicates.add(cb.like(advUserName, "%" + params.get("adv.userName") + "%"));
            }

            Predicate[] pre = new Predicate[predicates.size()];
            return cq.where(predicates.toArray(pre)).getRestriction();
        };
    }

    public static Specification<Material> findOne(Map<String, Object> params) {
        return (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (Long.class != cq.getResultType())
                root.fetch("advertiser", JoinType.INNER);

            if (params.containsKey("id"))
                predicates.add(cb.equal(root.get("id").as(Integer.class), (Integer) params.get("id")));

            Predicate[] pre = new Predicate[predicates.size()];
            return cq.where(predicates.toArray(pre)).getRestriction();
        };
    }
}
