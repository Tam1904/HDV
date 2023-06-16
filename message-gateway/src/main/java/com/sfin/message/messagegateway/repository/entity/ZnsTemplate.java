package com.sfin.message.messagegateway.repository.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "ZNS_TEMPLATE")
public class ZnsTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "template_id", nullable = false)
    private Integer templateId;

    @Column(name = "template_name")
    private String templateName;

    /**
     * 0 là thanh toán đơn hàng, 1 là ...
     */
    @Column(name = "type")
    private Integer type;

    @Column(name = "createdTime")
    private Date createdTime;

    /**
     * Trạng thái của template. Các giá trị trả về: PENDING_REVIEW, DISABLE, ENABLE, REJECT
     */
    @Column(name = "status")
    private String status;

    /**
     * Chất lượng gửi tin hiện tại của template: HIGH, MEDIUM, LOW, UNDEFINED
     */
    @Column(name = "template_quality")
    private String templateQuality;

}
