package com.sfin.message.messagegateway.repository;

import com.sfin.message.messagegateway.repository.entity.ShopTemplatesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ShopTemplatesDao extends JpaRepository<ShopTemplatesEntity, Long>, JpaSpecificationExecutor<ShopTemplatesEntity> {

    ShopTemplatesEntity findByShopIdAndTemplateId(Long shopId, Integer templateId);

    ShopTemplatesEntity findByShopIdAndTemplateIdAndType(Long shopId, Integer templateId, ShopTemplatesEntity.Type type);

    ShopTemplatesEntity findByShopIdAndType(Long shopId, ShopTemplatesEntity.Type type);
}