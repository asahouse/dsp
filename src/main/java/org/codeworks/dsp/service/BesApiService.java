package org.codeworks.dsp.service;

import org.codeworks.dsp.adx.bes.dto.response.creative.CreativeListBesResponse;
import org.codeworks.dsp.adx.bes.dto.response.report.AdvertiserReportBesResponse;
import org.codeworks.dsp.adx.bes.dto.response.report.ConsumeReportBesResponse;
import org.codeworks.dsp.adx.bes.dto.response.report.CreativeRTBReportBesResponse;
import org.codeworks.dsp.adx.bes.dto.response.report.RtbReportBesResponse;
import org.codeworks.dsp.model.entities.Advertiser;
import org.codeworks.dsp.model.entities.Material;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * bes接口业务
 * Created by Luis on 2016/9/14.
 */
public interface BesApiService {

    Boolean addAdvertiser(Advertiser adv);

    Boolean updateAdvertiser(Advertiser adv);

    void addCreatives(List<Material> materialList);

    void updateCreatives(List<Material> besUpdateList);

    Optional<RtbReportBesResponse> callRtbReport(LocalDate startDate, LocalDate endDate);

    Optional<ConsumeReportBesResponse> callConsumeReport(LocalDate startDate, LocalDate endDate);

    Optional<AdvertiserReportBesResponse> callAdvertiserReport(LocalDate startDate, LocalDate endDate);

    Optional<CreativeRTBReportBesResponse> callCreativeRTBReport(LocalDate startDate, LocalDate endDate);

    List<Integer> scheduleCheckAndUpdateCreatives();

    List<Integer> scheduleCheckAndUpdateAdvertiser();

    List<Integer> scheduleCheckModifiedCreatives();

    List<Integer> scheduleSendAdvertiser();
}
