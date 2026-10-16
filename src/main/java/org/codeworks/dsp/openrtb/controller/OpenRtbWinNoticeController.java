package org.codeworks.dsp.openrtb.controller;

import io.swagger.annotations.*;
import org.codeworks.dsp.openrtb.service.OpenRtbBiddingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * OpenRTB 2.5 胜出通知与计费结算端点
 * 处理 Prebid Server 或媒体客户端展示广告时触发的 Win Notice (NURL/BURL) 宏替换打点。
 */
@Api(tags = "OpenRTB 2.5 胜出结算接口", description = "接收媒体胜出计费通知，执行原子余额扣减与竞价消耗记账")
@RestController
@RequestMapping
public class OpenRtbWinNoticeController {

    private static final Logger logger = LoggerFactory.getLogger(OpenRtbWinNoticeController.class);

    @Autowired
    private OpenRtbBiddingService biddingService;

    /**
     * Prebid Server 或媒体渲染展示时触发的胜出回调
     * URL 中的 ${AUCTION_PRICE} 会被自动替换为实际胜出的结算价格（例如 3.25）
     * 具备严格的分布式幂等去重（防网络重放与重复扣费）以及原子扣费机制。
     *
     * @param bidId 出价 ID（唯一幂等键）
     * @param price 实际胜出结算价 (单位: CPM 千次曝光金额)
     * @param advId 广告主 ID
     * @param campaignId 广告活动 ID
     * @param materialId 创意素材 ID
     * @return 结算状态与清算价格响应
     */
    @ApiOperation(value = "OpenRTB 胜出计费通知 (Win Notice)", notes = "根据第二价格拍卖胜出价格计算单次曝光消耗 (price / 1000)，原子扣除广告主余额并累加活动花费")
    @ApiResponses({
            @ApiResponse(code = 200, message = "胜出通知已成功处理或幂等忽略重复通知")
    })
    @GetMapping(path = {"/openrtb2/win", "/v2/rtb/win"})
    public ResponseEntity<Map<String, Object>> winNotice(
            @ApiParam(value = "出价唯一标识 (用于幂等去重)", required = true)
            @RequestParam(required = false) String bidId,
            @ApiParam(value = "最终胜出清算价 (${AUCTION_PRICE} 宏替换后的真实 CPM 价格)", required = true)
            @RequestParam(required = false) Double price,
            @ApiParam(value = "扣费广告主 ID", required = true)
            @RequestParam(required = false) Integer advId,
            @ApiParam(value = "投放活动 ID", required = false)
            @RequestParam(required = false) Integer campaignId,
            @ApiParam(value = "胜出素材 ID", required = false)
            @RequestParam(required = false) Integer materialId) {

        logger.info("Received Win Notice: bidId={}, price={}, advId={}, campaignId={}, materialId={}",
                bidId, price, advId, campaignId, materialId);

        boolean success = biddingService.handleWinNotice(bidId, price, advId, campaignId, materialId);

        Map<String, Object> result = new HashMap<>();
        result.put("status", success ? "success" : "ignored");
        result.put("bidId", bidId);
        result.put("clearingPrice", price);

        return ResponseEntity.ok(result);
    }
}
