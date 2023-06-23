package com.sfin.message.messagegateway.repository.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "ZNS_TEMPLATE_DETAIL")
public class ZnsTemplateDetailEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "template_id", nullable = false)
    private Integer templateId;

    /**
     * Tên thuộc tính.
     */
    @Column(name = "name")
    private String name;

    /**
     * Tính bắt buộc của thuộc tính.
     */
    @Column(name = "is_require")
    private Boolean require;

    /**
     * Định dạng validate của thuộc tính.
     */
    @Column(name = "type")
    private String type;

    /**
     * Số kí tự tối đa được truyền vào thuộc tính.
     */
    @Column(name = "max_length")
    private Integer maxLength;

    /**
     * Số kí tự tối thiểu được truyền vào thuộc tính.
     */
    @Column(name = "min_length")
    private Integer minLength;

    /**
     * Thông tin cho biết thuộc tính có thể nhận giá trị rỗng hay không.
     */
    @Column(name = "accept_null")
    private Boolean acceptNull;

    @Column(name = "created_date")
    private Date createdDate;

}
