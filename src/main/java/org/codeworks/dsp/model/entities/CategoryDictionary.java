package org.codeworks.dsp.model.entities;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonView;
import org.codeworks.dsp.model.groups.Create;
import org.codeworks.dsp.model.groups.Update;
import org.codeworks.dsp.model.views.advertiser.ListView;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by benjaminkc on 16/10/19.
 */
@NamedEntityGraph(name = "category.all", attributeNodes = @NamedAttributeNode("categoryTag"))
@Entity
@Table(name = "category_dictionary",
        uniqueConstraints = {@UniqueConstraint(columnNames={"categoryId", "categoryParentId", "categoryTagCode"})})
public class CategoryDictionary extends BaseEntity implements Serializable {


    @NotNull(groups = Update.class)
    @Id
    @GenericGenerator(name = "keyGen", strategy = "org.codeworks.dsp.model.generator.CategoryKeyGenerator")
    @GeneratedValue(generator = "keyGen")
    @Column(name = "id", unique = true, nullable = false, length = 45)
    private String id;


    @NotNull(groups = {Create.class, Update.class})
    @Column(name = "categoryId", nullable = false, unique = true)
    private Integer categoryId;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "categoryParentId", referencedColumnName = "id")
//    private CategoryDictionary categoryParent;


    @NotNull(groups = {Create.class, Update.class})
    @Column(nullable = false, unique = true)
    private Integer categoryParentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryTagCode", referencedColumnName = "tagCode", nullable = false, unique = true)
    private CategoryTag categoryTag;

    @JsonView(ListView.class)
    @NotEmpty(groups = {Create.class, Update.class})
    @Length(min = 1, max = 45, groups = {Create.class, Update.class})
    @Column(name = "category", nullable = false, length = 45)
    private String category;

    @NotNull(groups = {Create.class, Update.class})
    @Column(name = "status", nullable = false)
    private Integer status;

    public CategoryDictionary() {}

    public CategoryDictionary(String id){
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CategoryTag getCategoryTag() {
        return categoryTag;
    }

    public void setCategoryTag(CategoryTag categoryTag) {
        this.categoryTag = categoryTag;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

//    public CategoryDictionary getCategoryParent() {
//        return categoryParent;
//    }
//
//    public void setCategoryParent(CategoryDictionary categoryParent) {
//        this.categoryParent = categoryParent;
//    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getCategoryParentId() {
        return categoryParentId;
    }

    public void setCategoryParentId(Integer categoryParentId) {
        this.categoryParentId = categoryParentId;
    }

    public Status getStatus() {
        return Status.fromValue(status);
    }

    public void setStatus(Status status) {
        this.status = status.toValue();
    }

    public enum Status {
        /**
         * 字典字段状态
         * eligible:正常
         * ineligible:失效
         */
        eligible(0), ineligible(1);

        private Integer value;

        Status(Integer value) {
            this.value = value;
        }

        public static CategoryDictionary.Status fromValue(Integer value) {
            if (value != null) {
                for (CategoryDictionary.Status status : values()) {
                    if (status.value.equals(value)) {
                        return status;
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
