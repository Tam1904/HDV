package com.sfin.message.messagegateway.repository;

import com.sfin.message.messagegateway.repository.entity.ShopZaloConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ShopZaloConfigDao extends JpaRepository<ShopZaloConfigEntity, Long>, JpaSpecificationExecutor<ShopZaloConfigEntity> {

}