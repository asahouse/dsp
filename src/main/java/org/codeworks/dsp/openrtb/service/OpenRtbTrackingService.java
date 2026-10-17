package org.codeworks.dsp.openrtb.service;

import java.time.LocalDate;
import java.util.Map;

/**
 * OpenRTB 广告点击追踪与 S2S 转化归因服务接口
 *
 * @author benjaminkc
 */
public interface OpenRtbTrackingService {

    /**
     * 处理广告点击打点：
     * 1. 生成全局唯一且安全的点击令牌 click_id
     * 2. 在 Redis 中沉淀点击归因上下文 (TTL 7 天)，并进行降级本地缓存
     * 3. 累加广告活动与素材点击量计数
     * 4. 组装携带 click_id 的最终落地页跳转目标 URL
     *
     * @param bidId 出价标识
     * @param campaignId 投放活动ID
     * @param advId 广告主ID
     * @param materialId 素材ID
     * @param redirectUrl 目标落地页 URL
     * @return 包含 click_id 参数的最终 302 重定向 URL
     */
    String recordClick(String bidId, Integer campaignId, Integer advId, Integer materialId, String redirectUrl);

    /**
     * 处理广告主 S2S (Server-to-Server) 转化回传事件：
     * 1. 根据 click_id 核验归因有效性 (归因时间窗口 7 天)
     * 2. 执行转化幂等排重，防止重复归因
     * 3. 累加活动转化量与转化金额 (GMV)
     * 4. 异步推送转化事件消息至 RabbitMQ 削峰处理
     *
     * @param clickId 点击唯一令牌
     * @param convType 转化类型 (如 purchase, register, download, activate)
     * @param orderAmount 转化产生金额 (GMV)
     * @return 转化归因处理结果明细
     */
    Map<String, Object> attributeConversion(String clickId, String convType, Double orderAmount);

    /**
     * 获取指定活动当日实时累计点击量
     *
     * @param campaignId 广告活动ID
     * @param date 日期
     * @return 当日点击总量
     */
    long getClickCount(Integer campaignId, LocalDate date);

    /**
     * 获取指定活动当日实时累计转化量
     *
     * @param campaignId 广告活动ID
     * @param date 日期
     * @return 当日转化总量
     */
    long getConversionCount(Integer campaignId, LocalDate date);

    /**
     * 获取指定活动当日实时累计转化金额 (GMV)
     *
     * @param campaignId 广告活动ID
     * @param date 日期
     * @return 当日累计转化金额
     */
    double getConversionRevenue(Integer campaignId, LocalDate date);
}
