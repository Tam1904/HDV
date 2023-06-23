package com.sfin.message.messagegateway.service;

import com.sfin.message.messagegateway.repository.entity.ShopZaloConfigEntity;
import com.sfin.message.messagegateway.response.OaInfoResponse;
import com.sfin.message.messagegateway.response.ShopChatResponse;
import com.sfin.message.messagegateway.response.UserOaInfoResponse;
import com.sfin.message.messagegateway.response.UserProfileResponse;
import lombok.SneakyThrows;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface UserOAService {
    OaInfoResponse getOAInfo(Long shopId);

    @SneakyThrows
    UserOaInfoResponse getUserOfOa(ShopZaloConfigEntity config, Integer offset, Integer count);

    ResponseEntity saveUserOa(Long shopId);

    @SneakyThrows
    UserProfileResponse getUserProfile(String accessToken, String userId);

    ResponseEntity getUserOfShop(String keyword, Long shopId, Long begin, Long end, Pageable pageable);

    ShopChatResponse getAllChatOfShop(Long shopId);

    ResponseEntity getAllConversationOfUser(Long shopId, String userId);

    ResponseEntity sendMessageToUser(String message, String userId, Long shopId);
}
