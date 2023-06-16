package com.sfin.message.messagegateway.repository.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "HISTORY_SEND_MESSAGE")
public class HistorySendMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "template_id")
    private Integer templateId;

    @Column(name = "error")
    private Integer error;

    @Column(name = "message")
    private String message;

    @Column(name = "msg_id")
    private String msgId;

    @Column(name = "send_time")
    private Date sendTime;

    @Column(name = "shop_id")
    private Long shopId;

    @Column(name = "body")
    private String body;

    @Column(name = "daily_quota")
    private String dailyQuota;

    @Column(name = "remaining_Quota")
    private String remainingQuota;

}
