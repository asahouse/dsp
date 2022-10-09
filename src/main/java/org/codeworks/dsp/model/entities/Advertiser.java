package org.codeworks.dsp.model.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonView;
import org.codeworks.dsp.model.groups.Create;
import org.codeworks.dsp.model.groups.Login;
import org.codeworks.dsp.model.groups.Update;
import org.codeworks.dsp.model.views.advertiser.ListView;
import org.codeworks.dsp.model.views.advertiser.LoginView;
import org.codeworks.dsp.model.views.advertiser.NoPassword;
import org.codeworks.dsp.model.views.creatives.AuditList;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 广告主
 */
@NamedEntityGraph(name = "advertiser.detail",
        attributeNodes = @NamedAttributeNode("qualification"))
@Entity
@Table(name = "advertiser",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"userName"})})
public class Advertiser extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 5219966462607320898L;

    @JsonView(value = {ListView.class, AuditList.class, org.codeworks.dsp.model.views.campaign.ListView.class})
    @NotNull(groups = Update.class)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JsonView(value = {ListView.class, AuditList.class})
    @NotEmpty(groups = {Create.class, Login.class})
    @Pattern(regexp = "^[A-Za-z0-9]*$", groups = {Create.class, Login.class})
    @Length(min = 4, max = 20, groups = {Create.class, Login.class})
    @Column(name = "userName", nullable = false, length = 20, unique = true)
    private String userName;

    @NotEmpty(groups = Login.class)
    @Length(min = 32, max = 32, groups = {Create.class, Login.class, Update.class})
    @Column(name = "password", length = 32)
    private String password;

    @JsonView(ListView.class)
    @NotEmpty(groups = {Create.class, Update.class})
    @Email(groups = {Create.class, Update.class})
    @Length(min = 1, max = 50, groups = {Create.class, Update.class})
    @Column(name = "email", nullable = false, length = 50)
    private String email;

    @JsonView(ListView.class)
    @NotNull(groups = {Create.class})
    @Column(name = "balance", nullable = false, precision = 2)
    private Double balance;

    @JsonView(ListView.class)
    @NotNull(groups = {Create.class, Update.class})
    @Column(name = "isAdmin", nullable = false)
    private Boolean isAdmin;

    @JsonView(ListView.class)
    @Column(name = "review", nullable = false)
    private Integer review;

    @JsonView(ListView.class)
    @NotNull(groups = {Create.class, Update.class})
    @Column(name = "status", nullable = false)
    private Integer status;

    @JsonView(NoPassword.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "createDate", nullable = false)
    private LocalDateTime createDate;

    @JsonView(NoPassword.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "lastLoginDate", nullable = false)
    private LocalDateTime lastLoginDate;

    @JsonView(ListView.class)
    @Column(name = "refuseReason")
    private String refuseReason;

    @JsonView({NoPassword.class, LoginView.class})
    @NotNull(groups = {Create.class, Update.class})
    @Valid
    @OneToOne(optional = false, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "qualificationId", referencedColumnName = "id")
    private Qualification qualification;

    @JsonView(ListView.class)
    @Column(name = "sync")
    private Integer sync;

    @JsonView(ListView.class)
    @Column(name = "sendAdx")
    private Integer sendAdx;

    public Advertiser() {
    }

    public Advertiser(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public Review getReview() {
        return Review.fromValue(review);
    }

    public void setReview(Review review) {
        this.review = review.toValue();
    }

    public Status getStatus() {
        return Status.fromValue(status);
    }

    public void setStatus(Status status) {
        this.status = status.toValue();
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public LocalDateTime getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(LocalDateTime lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public String getRefuseReason() {
        return refuseReason;
    }

    public void setRefuseReason(String refuseReason) {
        this.refuseReason = refuseReason;
    }

    public Qualification getQualification() {
        return qualification;
    }

    public void setQualification(Qualification qualification) {
        this.qualification = qualification;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
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

    public SendAdx getSendAdx() {
        return SendAdx.fromValue(sendAdx);
    }

    public void setSendAdx(SendAdx sendAdx) {
        this.sendAdx = sendAdx.toValue();
    }

    public enum Review {
        /**
         * adx审核状态
         *
         * eligible:通过
         * ineligible:不通过
         * pending:待审核
         * processing:提交中
         * waitingSubmit:待向adx提交新增处理
         * waitingModify:待向adx提交修改处理
         *
         */
        eligible(0), ineligible(1), pending(2), processing(3), waitingSubmit(4), waitingModify(5);

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
        /**
         * 暂未使用
         * eligible:通过
         * ineligible:不通过
         */
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

    public enum SendAdx {
        /**
         * 是否提交百度审核
         * ok:是
         * no:否
         */
        yes(1), no(0);

        private Integer value;

        SendAdx(Integer value) {
            this.value = value;
        }

        public static SendAdx fromValue(Integer value) {
            if (value != null) {
                for (SendAdx flag : values()) {
                    if (flag.value.equals(value)) {
                        return flag;
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
