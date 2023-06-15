package com.sfin.message.messagegateway.repository;

import com.sfin.message.messagegateway.repository.entity.ShopTemplates;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ShopTemplatesDao extends JpaRepository<ShopTemplates, Long>, JpaSpecificationExecutor<ShopTemplates> {

    ShopTemplates findByShopIdAndTemplateId(Long shopId, Long templateId);
}