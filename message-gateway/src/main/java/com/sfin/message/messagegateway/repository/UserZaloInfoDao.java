package com.sfin.message.messagegateway.repository;

import com.sfin.message.messagegateway.repository.entity.UserZaloInfoEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface UserZaloInfoDao extends JpaRepository<UserZaloInfoEntity, Long>, JpaSpecificationExecutor<UserZaloInfoEntity> {

}