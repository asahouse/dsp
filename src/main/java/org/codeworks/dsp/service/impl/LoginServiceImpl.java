package org.codeworks.dsp.service.impl;

import org.codeworks.dsp.dao.AdvertiserRepository;
import org.codeworks.dsp.model.entities.Advertiser;
import org.codeworks.dsp.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 登录相关业务接口实现
 * Created by Luis on 2016/8/29.
 */
@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private AdvertiserRepository advRepo;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Optional<Advertiser> login(Advertiser adv) {
        Optional<Advertiser> advertiserOpt =
                advRepo.getByUserNameAndPassword(adv.getUserName(), adv.getPassword());
        if (advertiserOpt.isPresent())
            advRepo.updateLastLoginDate(LocalDateTime.now(), advertiserOpt.get().getId());
        return advertiserOpt;
    }
}
