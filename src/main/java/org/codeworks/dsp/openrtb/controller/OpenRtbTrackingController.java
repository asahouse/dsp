package org.codeworks.dsp.openrtb.controller;

import io.swagger.annotations.*;
import org.codeworks.dsp.openrtb.service.OpenRtbTrackingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

/**
 * OpenRTB 广告效果追踪与转化归因控制器
 * <p>
 * 提供广告点击 302 重定向打点 (自动生成并注入 click_id) 与广告主 S2S 效果转化归因回传接口。
 * </p>
 *
 * @author benjaminkc
 */
@Api(tags = "OpenRTB 效果追踪与转化归因接口", description = "提供广告点击打点 (注入 click_id) 与广告主 S2S 转化回传归因")
@RestController
@RequestMapping
public class OpenRtbTrackingController {

    private static final Logger logger = LoggerFactory.getLogger(OpenRtbTrackingController.class);

    @Autowired
    private OpenRtbTrackingService trackingService;

    /**
     * 广告点击打点跳转端点
     * 用户点击广告时触发，服务端生成唯一 click_id 存入 Redis 并以 HTTP 302 重定向至广告主落地页
     *
     * @param bidId 出价标识
     * @param campaignId 投放活动ID
     * @param advId 广告主ID
     * @param materialId 素材ID
     * @param redirect 目标落地页 URL
     * @return 302 重定向响应
     */
    @ApiOperation(value = "OpenRTB 点击打点与落地页重定向", notes = "生成全局唯一 click_id，存入 7 天归因窗口缓存，并 302 重定向至目标落地页")
    @ApiResponses({
            @ApiResponse(code = 302, message = "成功生成 click_id 并重定向至携带参数的目标落地页")
    })
    @GetMapping(path = {"/openrtb2/click", "/v2/openrtb/click", "/v2/rtb/click"})
    public ResponseEntity<Void> click(
            @ApiParam(value = "出价标识", required = false)
            @RequestParam(required = false) String bidId,
            @ApiParam(value = "广告活动 ID", required = false)
            @RequestParam(required = false) Integer campaignId,
            @ApiParam(value = "广告主 ID", required = false)
            @RequestParam(required = false) Integer advId,
            @ApiParam(value = "创意素材 ID", required = false)
            @RequestParam(required = false) Integer materialId,
            @ApiParam(value = "目标落地页 URL (可带参数)", required = false, defaultValue = "https://www.example.com")
            @RequestParam(required = false, defaultValue = "https://www.example.com") String redirect) {

        String targetUrl = trackingService.recordClick(bidId, campaignId, advId, materialId, redirect);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(targetUrl));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    /**
     * 广告主 S2S (Server-to-Server) 转化回传端点 (GET 协议)
     *
     * @param clickId 点击唯一令牌
     * @param convType 转化类型 (purchase, register, activate 等)
     * @param orderAmount 订单或充值金额 (GMV)
     * @return 归因状态与处理结果明细
     */
    @ApiOperation(value = "S2S 转化回传归因 (GET)", notes = "根据落地页回传的 click_id 校验 7 天归因窗口有效性，执行幂等去重并统计转化指标")
    @ApiResponses({
            @ApiResponse(code = 200, message = "转化回传成功或幂等忽略重复回传")
    })
    @GetMapping(path = {"/openrtb2/conversion", "/v2/openrtb/conversion", "/v2/rtb/conversion"})
    public ResponseEntity<Map<String, Object>> conversionGet(
            @ApiParam(value = "点击唯一令牌 (click_id)", required = true)
            @RequestParam(value = "click_id", required = false) String clickId,
            @ApiParam(value = "转化业务类型 (如 purchase, register)", required = false, defaultValue = "purchase")
            @RequestParam(value = "conv_type", required = false, defaultValue = "purchase") String convType,
            @ApiParam(value = "订单交易金额", required = false, defaultValue = "0.0")
            @RequestParam(value = "order_amount", required = false, defaultValue = "0.0") Double orderAmount) {

        Map<String, Object> result = trackingService.attributeConversion(clickId, convType, orderAmount);
        return ResponseEntity.ok(result);
    }

    /**
     * 广告主 S2S 转化回传端点 (POST 协议)
     */
    @ApiOperation(value = "S2S 转化回传归因 (POST)", notes = "支持 POST 表单或请求体回传转化事件")
    @PostMapping(path = {"/openrtb2/conversion", "/v2/openrtb/conversion", "/v2/rtb/conversion"})
    public ResponseEntity<Map<String, Object>> conversionPost(
            @ApiParam(value = "点击唯一令牌 (click_id)", required = true)
            @RequestParam(value = "click_id", required = false) String clickId,
            @ApiParam(value = "转化业务类型", required = false, defaultValue = "purchase")
            @RequestParam(value = "conv_type", required = false, defaultValue = "purchase") String convType,
            @ApiParam(value = "订单交易金额", required = false, defaultValue = "0.0")
            @RequestParam(value = "order_amount", required = false, defaultValue = "0.0") Double orderAmount) {

        Map<String, Object> result = trackingService.attributeConversion(clickId, convType, orderAmount);
        return ResponseEntity.ok(result);
    }
}
