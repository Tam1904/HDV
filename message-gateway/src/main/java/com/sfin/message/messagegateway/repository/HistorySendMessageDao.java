package com.sfin.message.messagegateway.repository;

import com.sfin.message.messagegateway.repository.entity.HistorySendMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface HistorySendMessageDao extends JpaRepository<HistorySendMessage, Long>, JpaSpecificationExecutor<HistorySendMessage> {

}