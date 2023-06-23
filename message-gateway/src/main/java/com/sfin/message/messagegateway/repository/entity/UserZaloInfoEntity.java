package com.sfin.message.messagegateway.repository.entity;

import lombok.Data;

import javax.persistence.*;
import javax.persistence.metamodel.StaticMetamodel;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "USER_ZALO_INFO")
public class UserZaloInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sys_user_id")
    private Long sysUserId;

    @Column(name = "oa_user_id")
    private String oaUserId;

    @Column(name = "app_id")
    private String appId;

    @Column(name = "oa_id")
    private String oaId;

    @Column(name = "shop_id")
    private Long shopId;

    @Column(name = "user_phone")
    private String userPhone;

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "user_gender")
    private Integer userGender;

    @Column(name = "display_name")
    private String displayName;

    @StaticMetamodel(UserZaloInfoEntity.class)
    public abstract class  UserZaloInfo_{

        public static final String SHOP_ID = "shopId";
        public static final String USER_PHONE = "userPhone";
        public static final String DISPLAY_NAME = "displayName";
        public static final String CREATE_DATE = "createdDate";
        public static final String OA_ID = "oaId";
        public static final String OA_USER_ID = "oaUserId";
    }

}
