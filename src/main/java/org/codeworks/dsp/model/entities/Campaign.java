package org.codeworks.dsp.model.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonView;
import org.codeworks.dsp.model.groups.Create;
import org.codeworks.dsp.model.groups.Update;
import org.codeworks.dsp.model.views.campaign.EditView;
import org.codeworks.dsp.model.views.campaign.ListView;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@NamedEntityGraph(name = "campaign.all",
        attributeNodes = {@NamedAttributeNode("campaignObjectives"),@NamedAttributeNode("materials")})
@Entity
@Table(name = "campaign")
public class Campaign extends BaseEntity implements Serializable {

    @JsonView(ListView.class)
    @NotNull(groups = Update.class)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JsonView(ListView.class)
    @NotEmpty(groups = {Create.class, Update.class})
    @Length(min = 1, max = 30)
    @Column(name = "name", nullable = false, length = 30)
    private String name;

    @JsonView(ListView.class)
    @NotNull(groups = {Create.class, Update.class})
    @Column(name = "status", nullable = false)
    private Integer status;

    @JsonView(ListView.class)
    @Column(name = "review", nullable = false)
    private Integer review;

    @JsonView(ListView.class)
    @NotNull(groups = {Create.class, Update.class})
    @Column(name = "bid", nullable = false, precision = 2)
    private Float bid;

    @JsonView(ListView.class)
    @NotNull(groups = {Create.class, Update.class})
    @Column(name = "budget", nullable = false, precision = 2)
    private Float budget;

    @JsonView(ListView.class)
    @NotNull(groups = {Create.class, Update.class})
    @Column(name = "budgetType", nullable = false)
    private Integer budgetType;

    @JsonView(ListView.class)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Column(name = "startDate")
    private LocalDate startDate;

    @JsonView(ListView.class)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Column(name = "endDate")
    private LocalDate endDate;

    @JsonView(EditView.class)
    @Column(name = "targetLocation")
    private String targetLocation;

    @JsonView(EditView.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "createDate", nullable = false)
    private LocalDateTime createDate;

    @JsonView(ListView.class)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "advId", nullable = false)
    private Advertiser advertiser;

    @JsonView(EditView.class)
    @OneToMany(mappedBy = "campaignId", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private Set<CampaignObjective> campaignObjectives;

    @JsonView(EditView.class)
    @OneToMany(mappedBy = "campaign", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private Set<Material> materials;

    @JsonView(ListView.class)
    @Column(name = "sync")
    private Integer sync;

    @JsonView(ListView.class)
    @Column(name = "analysisCode")
    private String analysisCode;

    @JsonView(ListView.class)
    @Column(name = "analysisSiteId")
    private Integer analysisSiteId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Advertiser getAdvertiser() {
        return advertiser;
    }

    public void setAdvertiser(Advertiser advertiser) {
        this.advertiser = advertiser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Status getStatus() {
        return Status.fromValue(status);
    }

    public void setStatus(Status status) {
        this.status = status.toValue();
    }

    public Review getReview() {
        return Review.fromValue(review);
    }

    public void setReview(Review review) {
        this.review = review.toValue();
    }

    public Float getBid() {
        return bid;
    }

    public void setBid(Float bid) {
        this.bid = bid;
    }

    public Float getBudget() {
        return budget;
    }

    public void setBudget(Float budget) {
        this.budget = budget;
    }

    public BudgetType getBudgetType() {
        return BudgetType.fromValue(budgetType);
    }

    public void setBudgetType(BudgetType budgetType) {
        this.budgetType = budgetType.toValue();
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Set<String> getTargetLocation() {
        if (StringUtils.isEmpty(targetLocation))
            return new HashSet<>();
        else {
            String[] arr = targetLocation.split(",");
            return new HashSet<>(Arrays.asList(arr));
        }
    }

    public void setTargetLocation(String targetLocation) {
        this.targetLocation = targetLocation;
    }

    public String getTargetLocationString() {
        return this.targetLocation;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public Set<CampaignObjective> getCampaignObjectives() {
        return campaignObjectives;
    }

    public void setCampaignObjectives(Set<CampaignObjective> campaignObjectives) {
        this.campaignObjectives = campaignObjectives;
    }

    public Set<Material> getMaterials() {
        return materials;
    }

    public void setMaterials(Set<Material> materials) {
        this.materials = materials;
    }

    public Sync getSync() {
        return Sync.fromValue(sync);
    }

    public void setSync(Sync sync) {
        if (!Optional.ofNullable(sync).isPresent())
            this.sync = Sync.not.toValue();
        else
            this.sync = sync.toValue();
    }

    public String getAnalysisCode() {
        return analysisCode;
    }

    public void setAnalysisCode(String analysisCode) {
        this.analysisCode = analysisCode;
    }

    public Integer getAnalysisSiteId() {
        return analysisSiteId;
    }

    public void setAnalysisSiteId(Integer analysisSiteId) {
        this.analysisSiteId = analysisSiteId;
    }

    public enum Review {
        eligible(0), ineligible(1), pending(2), processing(3);

        private Integer value;

        Review(Integer value) {
            this.value = value;
        }

        public static Review fromValue(Integer value) {
            if (value != null) {
                for (Review review : values()) {
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

    public enum Status {
        eligible(0), ineligible(1);

        private Integer value;

        Status(Integer value) {
            this.value = value;
        }

        public static Status fromValue(Integer value) {
            if (value != null) {
                for (Status status : values()) {
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

    public enum BudgetType {
        all(0), daily(1);

        private Integer value;

        BudgetType(Integer value) {
            this.value = value;
        }

        public static BudgetType fromValue(Integer value) {
            if (value != null) {
                for (BudgetType budgetType : values()) {
                    if (budgetType.value.equals(value)) {
                        return budgetType;
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

    public enum Sync {
        /**
         * 是否同步RTB
         * not:是
         * yet:下架
         */
        not(0), yet(1);

        private Integer value;

        Sync(Integer value) {
            this.value = value;
        }

        public static Sync fromValue(Integer value) {
            if (value != null) {
                for (Sync sync : values()) {
                    if (sync.value.equals(value)) {
                        return sync;
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
