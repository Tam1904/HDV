package com.sfin.message.messagegateway.repository.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "SHOP_TEMPLATES")
public class ShopTemplates implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shop_id")
    private Long shopId;

    @Column(name = "template_id")
    private Long templateId;

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "is_active")
    private Boolean active;

}
