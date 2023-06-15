package com.sfin.message.messagegateway.repository;

import com.sfin.message.messagegateway.repository.entity.ShopZaloConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Date;
import java.util.List;

public interface ShopZaloConfigDao extends JpaRepository<ShopZaloConfigEntity, Long>, JpaSpecificationExecutor<ShopZaloConfigEntity> {

    ShopZaloConfigEntity findByOaId(String oaId);

    List<ShopZaloConfigEntity> findByAccessTokenExpiresBetween(Date begin, Date end);

    List<ShopZaloConfigEntity> findByRefreshTokenExpiresBetween(Date begin, Date end);

}