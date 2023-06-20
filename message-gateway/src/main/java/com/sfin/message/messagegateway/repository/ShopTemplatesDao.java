package com.sfin.message.messagegateway.repository;

import com.sfin.message.messagegateway.repository.entity.ShopTemplatesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ShopTemplatesDao extends JpaRepository<ShopTemplatesEntity, Long>, JpaSpecificationExecutor<ShopTemplatesEntity> {

    ShopTemplatesEntity findByShopIdAndTemplateId(Long shopId, Integer templateId);


}