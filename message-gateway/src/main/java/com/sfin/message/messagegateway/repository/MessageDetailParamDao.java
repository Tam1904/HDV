package com.sfin.message.messagegateway.repository;

import com.sfin.message.messagegateway.repository.entity.MessageDetailParamEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MessageDetailParamDao extends JpaRepository<MessageDetailParamEntity, Long>, JpaSpecificationExecutor<MessageDetailParamEntity> {

}