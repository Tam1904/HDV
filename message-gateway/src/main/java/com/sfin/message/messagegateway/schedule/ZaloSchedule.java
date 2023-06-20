package com.sfin.message.messagegateway.schedule;


import com.sfin.eplaform.commons.utils.DateUtils;
import com.sfin.message.messagegateway.repository.RedisRepository;
import com.sfin.message.messagegateway.repository.ShopZaloConfigDao;
import com.sfin.message.messagegateway.repository.entity.ShopZaloConfigEntity;
import com.sfin.message.messagegateway.request.NotificationRequest;
import com.sfin.message.messagegateway.service.ZaloService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
@Log4j2
public class ZaloSchedule {

    @Autowired
    private ShopZaloConfigDao shopZaloConfigDao;
    @Autowired
    private ZaloService zaloService;
    @Autowired
    private RedisRepository redisRepository;


    @Scheduled(fixedDelay = 3600000)
    public void updateAccessToken(){
        log.info("update access token once every hour");
        Long current = System.currentTimeMillis();
        Date begin = DateUtils.getDateRound(new Date(current), Calendar.HOUR_OF_DAY);
        Date end = DateUtils.addTime(begin, 1, Calendar.HOUR_OF_DAY);
        List<ShopZaloConfigEntity> zaloConfigs = shopZaloConfigDao.findByAccessTokenExpiresBetween(begin, end);
        for(ShopZaloConfigEntity zaloConfig : zaloConfigs){
            zaloService.updateAccessToken(zaloConfig);
        }
    }

    @Scheduled(fixedDelay = 3000)
    public void sendAutoZns(){
        List<NotificationRequest> requests = redisRepository.messageZnsList();
        for(NotificationRequest request: requests)
            zaloService.sendMessage(request);
    }
}
