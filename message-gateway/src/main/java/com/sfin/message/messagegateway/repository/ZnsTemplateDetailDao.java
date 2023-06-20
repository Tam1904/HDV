package com.sfin.message.messagegateway.repository;

import com.sfin.message.messagegateway.repository.entity.ZnsTemplateDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ZnsTemplateDetailDao extends JpaRepository<ZnsTemplateDetailEntity, Long>, JpaSpecificationExecutor<ZnsTemplateDetailEntity> {

    List<ZnsTemplateDetailEntity> findByTemplateId(Integer templateId);
}