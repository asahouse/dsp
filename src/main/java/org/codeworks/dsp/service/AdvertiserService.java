package org.codeworks.dsp.service;


import org.codeworks.dsp.model.entities.Advertiser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * 广告主业务接口
 * Created by Luis on 2016/8/23.
 */
public interface AdvertiserService {

    void createAdvertiser(Advertiser adv);

    Advertiser updateAdvertiser(Advertiser adv);

    void deleteAdvertiser(Integer id);

    Advertiser getAdvertiser(Integer id);

    Page<Advertiser> getAdvertisers(Map<String, Object> params, Pageable page);

    List<Advertiser> getAdvertisers(List<Integer> ids);

    Long countAdvertiser(Map<String, Object> params);

    Long countAll();
}
