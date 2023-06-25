package com.sfin.message.messagegateway.repository;

import com.sfin.message.messagegateway.repository.entity.ShopZaloConfigEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface ShopZaloConfigDao extends JpaRepository<ShopZaloConfigEntity, Long>, JpaSpecificationExecutor<ShopZaloConfigEntity> {

    ShopZaloConfigEntity findByOaId(String oaId);

    List<ShopZaloConfigEntity> findByAccessTokenExpiresBetween(Date begin, Date end);

    List<ShopZaloConfigEntity> findByRefreshTokenExpiresBetween(Date begin, Date end);

    ShopZaloConfigEntity findByShopIdAndOaId(Long shopId, String oaId);

    ShopZaloConfigEntity findOneByShopId(Long shopId);

    Page<ShopZaloConfigEntity> findByNameContainsIgnoreCaseAndCreatedDateBetween(String keyword, Date begin, Date end, Pageable pageable);

    Page<ShopZaloConfigEntity> findByNameContainsIgnoreCaseOrShopIdIn(String keyword, Set<Long> shopIds, Pageable pageable);

}