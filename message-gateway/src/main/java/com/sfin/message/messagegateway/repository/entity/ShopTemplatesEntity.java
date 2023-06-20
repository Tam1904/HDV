package com.sfin.message.messagegateway.repository.entity;

import com.sfin.message.messagegateway.response.TemplateData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.persistence.metamodel.StaticMetamodel;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "SHOP_TEMPLATES")
@AllArgsConstructor
@NoArgsConstructor
public class ShopTemplatesEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shop_id")
    private Long shopId;

    @Column(name = "template_id")
    private Integer templateId;

    @Column(name = "template_name")
    private String templateName;

    @Column(name = "preview_url")
    private String previewUrl;

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "is_active")
    private Boolean active;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private Type type;

    @Transient
    private List<ZnsTemplateDetailEntity> templateDetails;

    @Column(name = "template_tag")
    private String templateTag;
    @Column(name = "template_quality")
    private String templateQuality;

    @Column(name = "time_out")
    private Long timeout;

    @Column(name = "price")
    private Float price;

    public enum Type {
        ORDER, SCHEDULE, REPORT, REMINDER_SCHEDULE, REMINDER_TAKE_MEDICINE, ASK_AFTER_TREATMENT, BIRTHDAY
    }

    @StaticMetamodel(ShopTemplatesEntity.class)
    public abstract class ShopTemplate_{
        public static final String SHOP_ID = "shopId";
        public static final String TEMPLATE_ID = "templateId";
        public static final String TEMPLATE_NAME = "templateName";
        public static final String TYPE = "type";
        public static final String ACTIVE = "active";
        public static final String CREATE_DATE = "createdDate";
    }
}
