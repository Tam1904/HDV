package com.sfin.message.messagegateway.repository.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@javax.persistence.Table(name = "SHOP_ZALO_CONFIG")
@javax.persistence.Entity
@lombok.Data
@Data
@Entity
@Table(name = "SHOP_ZALO_CONFIG")
public class ShopZaloConfigEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @javax.persistence.GeneratedValue(strategy = GenerationType.IDENTITY)
    @javax.persistence.Column(name = "id", nullable = false)
    @javax.persistence.Id
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @javax.persistence.Column(name = "shop_id", nullable = false)
    @Column(name = "shop_id", nullable = false)
    private Long shopId;

    @javax.persistence.Column(name = "code_verifier")
    @Column(name = "code_verifier")
    private String codeVerifier;

    @javax.persistence.Column(name = "code_challenge")
    @Column(name = "code_challenge")
    private String codeChallenge;

    @javax.persistence.Column(name = "oa_id")
    @Column(name = "oa_id")
    private String oaId;

    @javax.persistence.Column(name = "authorization_code")
    @Column(name = "authorization_code")
    private String authorizationCode;

    @javax.persistence.Column(name = "access_token")
    @Column(name = "access_token")
    private String accessToken;

    @javax.persistence.Column(name = "access_token_expires")
    @Column(name = "access_token_expires")
    private Date accessTokenExpires;

    @javax.persistence.Column(name = "refresh_token")
    @Column(name = "refresh_token")
    private String refreshToken;

    @javax.persistence.Column(name = "refresh_token_expires")
    @Column(name = "refresh_token_expires")
    private Date refreshTokenExpires;

    @javax.persistence.Column(name = "create_date")
    @Column(name = "create_date")
    private Date createDate;

    @javax.persistence.Column(name = "modify_token_date")
    @Column(name = "modify_token_date")
    private Date modifyTokenDate;

}
