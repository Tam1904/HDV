package com.sfin.message.messagegateway.controller;

import com.sfin.eplaform.commons.response.ResponseFactory;
import com.sfin.message.messagegateway.repository.ShopZaloConfigDao;
import com.sfin.message.messagegateway.repository.entity.ShopTemplatesEntity;
import com.sfin.message.messagegateway.repository.entity.ShopZaloConfigEntity;
import com.sfin.message.messagegateway.request.MessageRequest;
import com.sfin.message.messagegateway.service.UserOAService;
import com.sfin.message.messagegateway.utils.DaoUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.ws.Response;

@RestController
@RequestMapping("/user-oa")
@Log4j2
public class UserOaController {

    @Autowired
    UserOAService userOAService;
    @Autowired
    ShopZaloConfigDao shopZaloConfigDao;


    @GetMapping
    public ResponseEntity getUserOfOA(@RequestParam Long shopId
            , @RequestParam(value = "keyword", defaultValue = "", required = false) String keyword
            , @RequestParam(value = "beginDate", required = false) Long begin
            , @RequestParam(value = "endDate", required = false) Long end
            , @RequestParam(value = "page", defaultValue = "0", required = false) Integer page
            , @RequestParam(value = "pageSize", defaultValue = "20", required = false) Integer pageSize
            , @RequestParam(value = "orderBy", defaultValue = "createdDate", required = false) String orderBy
            , @RequestParam(value = "direction", defaultValue = "DESC", required = false) String direction){
        Pageable pageable = DaoUtils.buildPageable(page, pageSize, orderBy, direction);
        return userOAService.getUserOfShop(keyword,shopId, begin, end, pageable);
    }

//    @GetMapping("/profiles")
//    public ResponseEntity getUserProfileOfOA(@RequestParam Long shopId){
//        ShopZaloConfigEntity shopZaloConfig = shopZaloConfigDao.findOneByShopId(shopId);
//        return ResponseFactory.success(userOAService.getUserProfile(shopZaloConfig.getAccessToken(), "5111403964612003268"));
//    }

    @GetMapping("/chats")
    public ResponseEntity getChatOfShop(@RequestParam Long shopId){
        log.info("get all chat of shop {}", shopId);
        return ResponseFactory.success(userOAService.getAllChatOfShop(shopId));
    }

    @GetMapping("/chats/user")
    public ResponseEntity getChatOfShop(@RequestParam Long shopId, @RequestParam String userId){
        log.info("get all conversation of shop {} userID {} ", shopId, userId);
        return userOAService.getAllConversationOfUser(shopId, userId);
    }

    @PostMapping("/chats/user")
    public ResponseEntity sendMessageToUser(@RequestParam Long shopId, @RequestBody MessageRequest request){
        log.info("send message to user {} of shop {}", request.getUserId(), shopId);
        return userOAService.sendMessageToUser(request.getMessage(), request.getUserId(), shopId);
    }
}
