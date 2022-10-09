package org.codeworks.dsp.exception;

/**
 * 业务错误码
 * Created by Luis on 2016/8/25.
 */
public enum ErrorCodes {

    // 权限相关
    require_login("login.require.login"),
    permission_denied("login.permission.denied"),

    // 广告主相关
    duplicate_advertiser("advertiser.duplicated"),
    advertiser_not_verify("advertiser.not.verify"),
    no_such_advertiser("advertiser.notfound"),
    can_not_modify_qualification_advertiser("advertiser.cannot.modify.qualification.at.audit"),

    // 创意组相关
    material_is_empty("creative.material.empty"),
    no_such_material("creative.material.notfound"),
    prohibit_modify_material("creative.material.prohibit"),

    //广告活动相关
    no_such_campaign("campaign.notfound"),
    no_such_campaign_objectives("campaign.objectives.notfound"),

    //类别相关
    no_such_category("category.notfound"),
    no_such_category_tag("category.tag.notfound"),
    update_category_error("category.update.error"),
    update_category_error_tag("category.update.error.tag"),
    update_category_error_identify("category.update.error.identify"),
    update_category_error_excel("category.update.error.excel"),
    init_category_tag_error("category.tag.error.init"),

    //RTB接口_创意物料
    rtb_sync_error("rtb.api.error.sync"),
    rtb_material_error_verify("rtb.api.material.notverify"),
    rtb_advertiser_error_verify("rtb.api.advertiser.notverify"),

    //Baidu Statistics
    baidu_statistics_init_error("baidu.statistics.api.init.error"),
    baidu_statistics_request_error("baidu.statistics.api.request.error"),
    baidu_statistics_prelogin_error("baidu.statistics.prelogin.error"),
    baidu_statistics_prelogin_auth("baidu.statistics.prelogin.auth"),
    baidu_statistics_prelogin_format("baidu.statistics.prelogin.format"),
    baidu_statistics_dologin_error("baidu.statistics.dologin.error"),
    baidu_statistics_dologin_format("baidu.statistics.dologin.format"),
    baidu_statistics_dologin_return_error("baidu.statistics.dologin.return.error"),
    baidu_statistics_dologout_error("baidu.statistics.dologin.error"),
    baidu_statistics_dologout_format("baidu.statistics.dologin.format"),
    baidu_statistics_sites_empty("baidu.statistics.sites.empty"),
    baidu_statistics_sites_error("baidu.statistics.sites.error"),
    baidu_statistics_data_error("baidu.statistics.data.error"),
    baidu_statistics_data_wrong("baidu.statistics.data.wrong"),
    baidu_statistics_data_collection_error("baidu.statistics.data.collection.error"),
    baidu_statistics_data_collection_wrong("baidu.statistics.data.collection.wrong")
    ;


    private String code;

    ErrorCodes(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

}
