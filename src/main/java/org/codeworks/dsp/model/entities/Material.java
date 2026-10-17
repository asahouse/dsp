package org.codeworks.dsp.model.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonView;
import org.codeworks.dsp.model.constraints.material.MaterialConstraint;
import org.codeworks.dsp.model.groups.Create;
import org.codeworks.dsp.model.groups.Update;
import org.codeworks.dsp.model.views.campaign.EditView;
import org.codeworks.dsp.model.views.creatives.AuditList;
import org.codeworks.dsp.model.views.creatives.ListView;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Optional;

@NamedEntityGraph(name = "material.advertiser",
        attributeNodes = {@NamedAttributeNode("advertiser"), @NamedAttributeNode("campaign")})
@Entity
@Table(name = "material")
@MaterialConstraint(groups = Create.class)
public class Material extends BaseEntity implements Serializable {

    @JsonView({ListView.class, EditView.class})
    @NotNull(groups = Update.class)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JsonView({ListView.class, EditView.class})
    @NotEmpty(groups = {Create.class, Update.class})
    @Length(min = 1, max = 30, groups = {Create.class, Update.class})
    @Column(name = "name", nullable = false, length = 30)
    private String name;

    @JsonView(ListView.class)
    @NotEmpty(groups = {Create.class})
    @Length(min = 1, max = 150, groups = {Create.class})
    @URL(groups = {Create.class})
    @Column(name = "url", nullable = false, length = 150)
    private String url;

    @JsonView({ListView.class, EditView.class})
    @NotNull(groups = {Create.class, Update.class})
    @Column(name = "status", nullable = false)
    private Integer status;

    @JsonView({ListView.class, EditView.class})
    @Column(name = "review", nullable = false)
    private Integer review;

    @JsonView(ListView.class)
    @NotNull(groups = {Create.class})
    @Column(name = "width", nullable = false)
    private Integer width;

    @JsonView(ListView.class)
    @NotNull(groups = {Create.class})
    @Column(name = "height", nullable = false)
    private Integer height;

    @JsonView(ListView.class)
    @Column(name = "tradeId", nullable = false)
    private Integer tradeId;

    @JsonView(ListView.class)
    @NotNull(groups = {Create.class})
    @Column(name = "type", nullable = false)
    private Integer type;

    @JsonView(ListView.class)
    @NotEmpty(groups = {Create.class, Update.class})
    @Length(min = 1, max = 255, groups = {Create.class, Update.class})
    @URL(groups = {Create.class, Update.class})
    @Column(name = "landingUrl", nullable = false, length = 255)
    private String landingUrl;

    @JsonView(ListView.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "createDate", nullable = false)
    private LocalDateTime createDate;

    @JsonView(ListView.class)
    @Column(name = "refuseReason")
    private String refuseReason;

    @JsonView(ListView.class)
    @Column(name = "sync")
    private Integer sync;

    @JsonView(AuditList.class)
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "advId", referencedColumnName = "id", nullable = false)
    private Advertiser advertiser;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "campaignId", nullable = false)
    private Campaign campaign;

    @JsonView(ListView.class)
    @Column(name = "admTemplate", length = 4000)
    private String admTemplate;

    @JsonView(ListView.class)
    @Column(name = "adDomain", length = 100)
    private String adDomain;

    @JsonView(ListView.class)
    @Column(name = "cpmPrice")
    private Double cpmPrice;

    @JsonView(ListView.class)
    @Column(name = "creativeType", length = 20)
    private String creativeType;

    /** 视频播放时长 (秒) */
    @JsonView(ListView.class)
    @Column(name = "videoDuration")
    private Integer videoDuration;

    /** 视频 MIME 类型, 如 video/mp4 */
    @JsonView(ListView.class)
    @Column(name = "videoMimes", length = 100)
    private String videoMimes;

    /** 视频码率 (kbps) */
    @JsonView(ListView.class)
    @Column(name = "videoBitrate")
    private Integer videoBitrate;

    /** 原生广告主标题 */
    @JsonView(ListView.class)
    @Column(name = "nativeTitle", length = 100)
    private String nativeTitle;

    /** 原生广告副标题/描述 */
    @JsonView(ListView.class)
    @Column(name = "nativeDesc", length = 255)
    private String nativeDesc;

    /** 原生广告图标 Icon URL */
    @JsonView(ListView.class)
    @Column(name = "nativeIconUrl", length = 500)
    private String nativeIconUrl;

    /** 原生广告行动号召文字 (CTA) */
    @JsonView(ListView.class)
    @Column(name = "nativeCtaText", length = 50)
    private String nativeCtaText = "查看详情";

    public Material() {}

    public Material (Integer id){
        this.id = id;
    }

    public String getAdmTemplate() { return admTemplate; }
    public void setAdmTemplate(String admTemplate) { this.admTemplate = admTemplate; }

    public String getAdDomain() { return adDomain; }
    public void setAdDomain(String adDomain) { this.adDomain = adDomain; }

    public Double getCpmPrice() { return cpmPrice; }
    public void setCpmPrice(Double cpmPrice) { this.cpmPrice = cpmPrice; }

    public String getCreativeType() { return creativeType; }
    public void setCreativeType(String creativeType) { this.creativeType = creativeType; }

    public Integer getVideoDuration() { return videoDuration; }
    public void setVideoDuration(Integer videoDuration) { this.videoDuration = videoDuration; }

    public String getVideoMimes() { return videoMimes; }
    public void setVideoMimes(String videoMimes) { this.videoMimes = videoMimes; }

    public Integer getVideoBitrate() { return videoBitrate; }
    public void setVideoBitrate(Integer videoBitrate) { this.videoBitrate = videoBitrate; }

    public String getNativeTitle() { return nativeTitle; }
    public void setNativeTitle(String nativeTitle) { this.nativeTitle = nativeTitle; }

    public String getNativeDesc() { return nativeDesc; }
    public void setNativeDesc(String nativeDesc) { this.nativeDesc = nativeDesc; }

    public String getNativeIconUrl() { return nativeIconUrl; }
    public void setNativeIconUrl(String nativeIconUrl) { this.nativeIconUrl = nativeIconUrl; }

    public String getNativeCtaText() { return nativeCtaText != null ? nativeCtaText : "查看详情"; }
    public void setNativeCtaText(String nativeCtaText) { this.nativeCtaText = nativeCtaText; }

    /**
     * 渲染广告响应代码 (adm)
     * 支持 Banner、Video (VAST 3.0 XML) 和 Native (OpenRTB Native 1.2 JSON)
     *
     * @param auctionPrice 结算价格宏替换
     * @param bidId 出价唯一标识
     * @param winUrl 胜出/曝光通知 URL
     * @return 组装后的 adm 内容
     */
    public String renderAdm(String auctionPrice, String bidId, String winUrl) {
        if (this.admTemplate != null && !this.admTemplate.trim().isEmpty()) {
            String rendered = this.admTemplate;
            if (auctionPrice != null) rendered = rendered.replace("${AUCTION_PRICE}", auctionPrice);
            if (bidId != null) rendered = rendered.replace("${AUCTION_BID_ID}", bidId);
            return rendered;
        }

        String rawLanding = (this.landingUrl != null) ? this.landingUrl : "#";
        String resolvedBidId = (bidId != null) ? bidId : "bid-" + System.currentTimeMillis();
        String click = rawLanding;
        if (!"#".equals(rawLanding)) {
            String clickPrefix = "/openrtb2/click?bidId=" + resolvedBidId
                    + (this.campaign != null && this.campaign.getId() != null ? "&campaignId=" + this.campaign.getId() : "")
                    + (this.advertiser != null && this.advertiser.getId() != null ? "&advId=" + this.advertiser.getId() : "")
                    + (this.id != null ? "&materialId=" + this.id : "")
                    + "&redirect=";
            click = clickPrefix + rawLanding;
        }
        String mediaUrl = (this.url != null) ? this.url : "";
        int w = (this.width != null) ? this.width : 300;
        int h = (this.height != null) ? this.height : 250;

        if ("VIDEO".equalsIgnoreCase(this.creativeType)) {
            // 组装 IAB 标准 VAST 3.0 XML
            int durSec = (this.videoDuration != null && this.videoDuration > 0) ? this.videoDuration : 15;
            String durStr = String.format("00:00:%02d", durSec);
            String mime = (this.videoMimes != null && !this.videoMimes.isEmpty()) ? this.videoMimes : "video/mp4";
            String title = (this.name != null) ? this.name : "Video Ad";

            StringBuilder vast = new StringBuilder();
            vast.append("<VAST version=\"3.0\">");
            vast.append("<Ad id=\"").append(resolvedBidId).append("\">");
            vast.append("<InLine>");
            vast.append("<AdSystem>DSP-Platform</AdSystem>");
            vast.append("<AdTitle>").append(title).append("</AdTitle>");
            if (winUrl != null) {
                vast.append("<Impression><![CDATA[").append(winUrl).append("]]></Impression>");
            }
            vast.append("<Creatives><Creative><Linear>");
            vast.append("<Duration>").append(durStr).append("</Duration>");
            vast.append("<TrackingEvents>");
            if (winUrl != null) {
                vast.append("<Tracking event=\"start\"><![CDATA[").append(winUrl).append("&event=start]]></Tracking>");
                vast.append("<Tracking event=\"midpoint\"><![CDATA[").append(winUrl).append("&event=midpoint]]></Tracking>");
                vast.append("<Tracking event=\"complete\"><![CDATA[").append(winUrl).append("&event=complete]]></Tracking>");
            }
            vast.append("</TrackingEvents>");
            vast.append("<VideoClicks><ClickThrough><![CDATA[").append(click).append("]]></ClickThrough></VideoClicks>");
            vast.append("<MediaFiles><MediaFile delivery=\"progressive\" type=\"").append(mime)
                .append("\" width=\"").append(w).append("\" height=\"").append(h).append("\"><![CDATA[")
                .append(mediaUrl).append("]]></MediaFile></MediaFiles>");
            vast.append("</Linear></Creative></Creatives>");
            vast.append("</InLine></Ad></VAST>");
            return vast.toString();
        }

        if ("NATIVE".equalsIgnoreCase(this.creativeType)) {
            // 组装 OpenRTB Native 1.2 规范 JSON 响应
            String title = (this.nativeTitle != null && !this.nativeTitle.isEmpty()) ? this.nativeTitle : (this.name != null ? this.name : "推广推荐");
            String desc = (this.nativeDesc != null && !this.nativeDesc.isEmpty()) ? this.nativeDesc : "优质好物，限时体验";
            String icon = (this.nativeIconUrl != null) ? this.nativeIconUrl : mediaUrl;
            String cta = getNativeCtaText();

            StringBuilder nativeJson = new StringBuilder();
            nativeJson.append("{\"native\":{\"ver\":\"1.2\",\"assets\":[");
            nativeJson.append("{\"id\":1,\"title\":{\"text\":\"").append(escapeJson(title)).append("\"}},");
            nativeJson.append("{\"id\":2,\"img\":{\"type\":3,\"url\":\"").append(escapeJson(mediaUrl)).append("\",\"w\":").append(w).append(",\"h\":").append(h).append("}},");
            nativeJson.append("{\"id\":3,\"img\":{\"type\":1,\"url\":\"").append(escapeJson(icon)).append("\"}},");
            nativeJson.append("{\"id\":4,\"data\":{\"type\":2,\"value\":\"").append(escapeJson(desc)).append("\"}},");
            nativeJson.append("{\"id\":5,\"data\":{\"type\":12,\"value\":\"").append(escapeJson(cta)).append("\"}}");
            nativeJson.append("],\"link\":{\"url\":\"").append(escapeJson(click)).append("\"}");
            if (winUrl != null) {
                nativeJson.append(",\"imptrackers\":[\"").append(escapeJson(winUrl)).append("\"]");
            }
            nativeJson.append("}}");
            return nativeJson.toString();
        }

        // 默认 Banner HTML 创意片段
        StringBuilder sb = new StringBuilder();
        sb.append("<div style=\"width:").append(w).append("px;height:").append(h).append("px;position:relative;display:inline-block;\">");
        sb.append("<a href=\"").append(click).append("\" target=\"_blank\">");
        sb.append("<img src=\"").append(mediaUrl).append("\" width=\"").append(w).append("\" height=\"").append(h).append("\" border=\"0\"/>");
        sb.append("</a>");
        if (winUrl != null) {
            sb.append("<img src=\"").append(winUrl).append("\" width=\"1\" height=\"1\" style=\"display:none;\"/>");
        }
        sb.append("</div>");
        return sb.toString();
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getTradeId() {
        return tradeId;
    }

    public void setTradeId(Integer tradeId) {
        this.tradeId = tradeId;
    }

    public Type getType() {
        return Type.fromValue(type);
    }

    public void setType(Type type) {
        this.type = type.toValue();
    }

    public String getLandingUrl() {
        return landingUrl;
    }

    public void setLandingUrl(String landingUrl) {
        this.landingUrl = landingUrl;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public String getRefuseReason() {
        return refuseReason;
    }

    public void setRefuseReason(String refuseReason) {
        this.refuseReason = refuseReason;
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

    public Advertiser getAdvertiser() {
        return advertiser;
    }

    public void setAdvertiser(Advertiser advertiser) {
        this.advertiser = advertiser;
    }

    public Campaign getCampaign() {
        return campaign;
    }

    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
    }

    public enum Review {
        /**
         * eligible:通过
         * ineligibleForAdmin:管理员拒绝
         * ineligibleForAdx:adx拒绝
         * pending:待管理员审核
         * processing:正提交百度审核
         * updatePending:正等待百度审核
         */
        eligible(0), ineligibleForAdmin(1), ineligibleForAdx(2), pending(3), processing(4), updatePending(5);

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

    public enum Type {
        text(0), image(1), flash(2), article(4), linkUnit(5), video(7);

        private Integer value;

        Type(Integer value) {
            this.value = value;
        }

        public static Type fromValue(Integer value) {
            if (value != null) {
                for (Type type : values()) {
                    if (type.value.equals(value)) {
                        return type;
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
         * 物料的上下架情况
         * eligible:上架
         * ineligible:下架
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
}
