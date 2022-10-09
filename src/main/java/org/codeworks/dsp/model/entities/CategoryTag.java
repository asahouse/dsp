package org.codeworks.dsp.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import org.codeworks.dsp.model.groups.Update;
import org.codeworks.dsp.model.views.advertiser.ListView;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Set;

/**
 * Created by benjaminkc on 16/10/19.
 */
@Entity
@Table(name="category_tag",
        uniqueConstraints = {@UniqueConstraint(columnNames="tagCode")})
public class CategoryTag extends BaseEntity implements Serializable {

    @NotNull(groups = Update.class)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JsonView(ListView.class)
    @NotNull
    @Column(name="tag", nullable=false, length = 45)
    private String tag;

    @JsonView(ListView.class)
    @NotNull
    @Column(name="tagCode", nullable = false, length = 45, unique = true)
    private String tagCode;

    @JsonIgnore
    @OneToMany(mappedBy = "categoryTag", fetch = FetchType.LAZY)
    private Set<CategoryDictionary> categoryDictionaries;

    public CategoryTag() {}

    public CategoryTag(Integer id){
        this.id = id;
    }

    public String getTagCode() {
        return tagCode;
    }

    public void setTagCode(String tagCode) {
        this.tagCode = tagCode;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Set<CategoryDictionary> getCategoryDictionaries() {
        return categoryDictionaries;
    }

    public void setCategoryDictionaries(Set<CategoryDictionary> categoryDictionaries) {
        this.categoryDictionaries = categoryDictionaries;
    }

}
