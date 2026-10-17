package org.codeworks.dsp.openrtb.controller;

import io.swagger.annotations.*;
import org.codeworks.dsp.openrtb.dto.request.BidRequest;
import org.codeworks.dsp.openrtb.dto.response.BidResponse;
import org.codeworks.dsp.openrtb.service.OpenRtbBiddingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * OpenRTB 2.5 实时竞价出价端点 (对接 Prebid Server / ADX)
 * 提供符合全球标准化 OpenRTB 2.5 协议规范的实时询价响应服务。
 */
@Api(tags = "OpenRTB 2.5 实时竞价接口", description = "对接 Prebid Server 及各类标准 ADX 的核心出价引擎接口")
@RestController
@RequestMapping
public class OpenRtbBidController {

    private static final Logger logger = LoggerFactory.getLogger(OpenRtbBidController.class);

    @Autowired
    private OpenRtbBiddingService biddingService;

    /**
     * 响应来自 Prebid Server 的 OpenRTB 2.5 询价请求
     * 支持 /openrtb2/bid 与 /v2/rtb/bid 双路径
     * 
     * @param request 标准 OpenRTB 2.5 竞价请求对象 (包含曝光位 imp、尺寸、底价等)
     * @return 200 OK 并返回出价明细 (BidResponse)；若无匹配广告或预算用尽则返回 204 No Content
     */
    @ApiOperation(value = "OpenRTB 2.5 实时竞价出价 (Bid)", notes = "根据媒体传入的广告位尺寸、底价与定向规则，在 DSP 内部毫秒级匹配最优出价素材并返回")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功出价，返回包含 adm 与 nurl 的 BidResponse 报文", response = BidResponse.class),
            @ApiResponse(code = 204, message = "无竞价响应 (NO_BID)，广告位不匹配或活动预算已耗尽"),
            @ApiResponse(code = 400, message = "请求报文非法或缺少必要 ID 字段")
    })
    @PostMapping(path = {"/openrtb2/bid", "/v2/openrtb/bid", "/v2/rtb/bid"})
    public ResponseEntity<?> bid(
            @ApiParam(value = "OpenRTB 2.5 BidRequest 请求体", required = true)
            @RequestBody BidRequest request) {
        if (request == null || request.getId() == null) {
            return ResponseEntity.badRequest().build();
        }

        logger.debug("Received OpenRTB 2.5 BidRequest: id={}", request.getId());
        Optional<BidResponse> bidResponse = biddingService.processBid(request);

        if (bidResponse.isPresent()) {
            return ResponseEntity.ok(bidResponse.get());
        } else {
            // OpenRTB 2.5 标准：当无符合条件的出价时返回 HTTP 204 No Content
            return ResponseEntity.noContent().build();
        }
    }
}
