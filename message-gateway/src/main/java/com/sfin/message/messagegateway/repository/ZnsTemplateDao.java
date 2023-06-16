package com.sfin.message.messagegateway.repository;

import com.sfin.message.messagegateway.repository.entity.ZnsTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZnsTemplateDao extends JpaRepository<ZnsTemplate, Integer>, JpaSpecificationExecutor<ZnsTemplate> {

}