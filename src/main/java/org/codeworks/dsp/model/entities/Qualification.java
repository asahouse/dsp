package org.codeworks.dsp.model.entities;

import com.fasterxml.jackson.annotation.JsonView;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.codeworks.dsp.model.groups.Create;
import org.codeworks.dsp.model.groups.Update;
import org.codeworks.dsp.model.views.advertiser.LoginView;
import org.codeworks.dsp.model.views.advertiser.NoPassword;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "adv_qualification")
public class Qualification extends BaseEntity implements Serializable {

    @JsonView(NoPassword.class)
    @NotNull(groups = Update.class)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JsonView(NoPassword.class)
    @NotEmpty(groups = {Create.class, Update.class})
    @Length(min = 1, max = 50, groups = {Create.class, Update.class})
    @Column(name = "licence", nullable = false, length = 50)
    private String licence;

    @JsonView(NoPassword.class)
    @NotEmpty(groups = {Create.class, Update.class})
    @Length(min = 1, max = 50, groups = {Create.class, Update.class})
    @Column(name = "companyName", nullable = false, length = 50)
    private String companyName;

    @JsonView(NoPassword.class)
    @NotEmpty(groups = {Create.class, Update.class})
    @URL(groups = {Create.class, Update.class})
    @Length(min = 1, max = 100, groups = {Create.class, Update.class})
    @Column(name = "officialSite", nullable = false, length = 100)
    private String officialSite;

    @JsonView(NoPassword.class)
    @NotEmpty(groups = {Create.class, Update.class})
    @Length(min = 1, max = 20, groups = {Create.class, Update.class})
    @Column(name = "siteName", nullable = false, length = 20)
    private String siteName;

    @JsonView({NoPassword.class, LoginView.class})
    @NotNull(groups = {Create.class, Update.class})
    @Column(name = "tradeId", nullable = false)
    private Integer tradeId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLicence() {
        return licence;
    }

    public void setLicence(String licence) {
        this.licence = licence;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getOfficialSite() {
        return officialSite;
    }

    public void setOfficialSite(String officialSite) {
        this.officialSite = officialSite;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Integer getTradeId() {
        return tradeId;
    }

    public void setTradeId(Integer tradeId) {
        this.tradeId = tradeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Qualification that = (Qualification) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(licence, that.licence)
                .append(companyName, that.companyName)
                .append(officialSite, that.officialSite)
                .append(siteName, that.siteName)
                .append(tradeId, that.tradeId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(licence)
                .append(companyName)
                .append(officialSite)
                .append(siteName)
                .append(tradeId)
                .toHashCode();
    }
}
