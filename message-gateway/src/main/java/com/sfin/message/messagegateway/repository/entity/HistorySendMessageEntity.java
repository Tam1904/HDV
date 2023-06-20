package com.sfin.message.messagegateway.repository.entity;

import lombok.Data;

import javax.persistence.*;
import javax.persistence.metamodel.StaticMetamodel;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "HISTORY_SEND_MESSAGE")
public class HistorySendMessageEntity implements Serializable {

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

    @Column(name = "template_name")
    private String templateName;

    @Column(name = "time_out")
    private Long timeout;

    @Column(name = "price")
    private Float price;

    @Column(name = "template_tag")
    private String templateTag;

    @Column(name = "preview_url")
    private String previewUrl;

    @Column(name = "type")
    private String type;

    @StaticMetamodel(HistorySendMessageEntity.class)
    public abstract class HistorySendMessage_ {
        public static final String SEND_TIME = "sendTime";
        public static final String TEMPLATE_ID = "templateId";
        public static final String TEMPLATE_NAME = "templateName";
        public static final String TYPE = "type";
        public static final String SHOP_ID = "shopId";
        public static final String ERROR = "error";
    }

}
