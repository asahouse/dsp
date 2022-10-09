package org.codeworks.dsp.service;

import org.codeworks.dsp.model.entities.Advertiser;

import java.util.Optional;

/**
 * 登录相关业务接口
 * Created by Luis on 2016/8/29.
 */
public interface LoginService {

    Optional<Advertiser> login(Advertiser adv);
}
