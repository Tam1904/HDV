package com.sfin.message.messagegateway.repository.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "MESSAGE_DETAIL_PARAM")
public class MessageDetailParamEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "history_message_id", nullable = false)
    private Long historyMessageId;

    @Column(name = "msg_id")
    private String msgId;

    @Column(name = "param_name")
    private String paramName;

    @Column(name = "param_value")
    private String paramValue;

    @Column(name = "template_id")
    private String templateId;

    @Column(name = "parma_type")
    private String parmaType;

    @Column(name = "created_date")
    private Date createdDate;

}
