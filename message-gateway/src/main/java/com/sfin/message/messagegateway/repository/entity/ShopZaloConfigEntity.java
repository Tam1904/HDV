package com.sfin.message.messagegateway.repository.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "SHOP_ZALO_CONFIG")
public class ShopZaloConfigEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "shop_id", nullable = false)
    private Long shopId;

    @Column(name = "app_id")
    private String appId;

    @Column(name = "secret_key")
    private String secretKey;

    @Column(name = "code_verifier")
    private String codeVerifier;

    @Column(name = "code_challenge")
    private String codeChallenge;

    @Column(name = "oa_id")
    private String oaId;

    @Column(name = "authorization_code")
    private String authorizationCode;

    @Column(name = "access_token")
    private String accessToken;

    @Column(name = "access_token_expires")
    private Date accessTokenExpires;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "refresh_token_expires")
    private Date refreshTokenExpires;

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "modify_token_date")
    private Date modifyTokenDate;

}
