package org.codeworks.dsp.openrtb.service;

import org.codeworks.dsp.openrtb.dto.request.BidRequest;
import org.codeworks.dsp.openrtb.dto.response.BidResponse;

import java.util.Optional;

/**
 * OpenRTB 2.5 竞价与胜出结算服务接口
 */
public interface OpenRtbBiddingService {

    /**
     * 根据传入的 OpenRTB 2.5 询价请求进行竞价裁决
     * @param request OpenRTB 2.5 BidRequest
     * @return 若匹配到有效出价则返回 BidResponse，若底价不满足或无可用物料则返回 Optional.empty() (NO_BID)
     */
    Optional<BidResponse> processBid(BidRequest request);

    /**
     * 处理竞价胜出通知（Win Notice / NURL 触发）
     * 扣减广告主账户余额，累加胜出计数
     * @param bidId 竞价标识
     * @param price 结算第二价格（CPM）
     * @param advId 广告主ID
     * @param campaignId 活动ID
     * @param materialId 素材ID
     * @return 扣费与处理结果
     */
    boolean handleWinNotice(String bidId, Double price, Integer advId, Integer campaignId, Integer materialId);
}
