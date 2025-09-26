package com.example.crm.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * ContactMech 实体类 - 联系方式
 * 存储电话、邮箱、地址等联系信息
 */
@Entity
@Table(name = "contact_mech")
public class ContactMech extends PanacheEntityBase {

    @Id
    @NotBlank(message = "联系方式 ID 不能为空")
    @Size(max = 60, message = "联系方式 ID 长度不能超过60个字符")
    @Column(name = "contact_mech_id", length = 60, nullable = false)
    public String contactMechId;

    @Size(max = 60, message = "联系方式类型 ID 长度不能超过60个字符")
    @Column(name = "contact_mech_type_id", length = 60)
    public String contactMechTypeId;

    @Size(max = 255, message = "信息字符串长度不能超过255个字符")
    @Column(name = "info_string", length = 255)
    public String infoString;

    @Column(name = "last_updated_stamp")
    public LocalDateTime lastUpdatedStamp;

    @Column(name = "last_updated_tx_stamp")
    public LocalDateTime lastUpdatedTxStamp;

    @Column(name = "created_stamp")
    public LocalDateTime createdStamp;

    @Column(name = "created_tx_stamp")
    public LocalDateTime createdTxStamp;

    /**
     * 获取联系方式类型描述
     */
    public String getContactMechTypeDescription() {
        if (contactMechTypeId == null) {
            return "未知";
        }
        
        switch (contactMechTypeId) {
            case "EMAIL_ADDRESS":
                return "邮箱地址";
            case "TELECOM_NUMBER":
                return "电话号码";
            case "POSTAL_ADDRESS":
                return "邮政地址";
            case "WEB_ADDRESS":
                return "网站地址";
            case "IP_ADDRESS":
                return "IP地址";
            default:
                return contactMechTypeId;
        }
    }

    /**
     * 检查是否为邮箱地址
     */
    public boolean isEmailAddress() {
        return "EMAIL_ADDRESS".equals(contactMechTypeId);
    }

    /**
     * 检查是否为电话号码
     */
    public boolean isTelecomNumber() {
        return "TELECOM_NUMBER".equals(contactMechTypeId);
    }

    /**
     * 检查是否为邮政地址
     */
    public boolean isPostalAddress() {
        return "POSTAL_ADDRESS".equals(contactMechTypeId);
    }

    /**
     * 检查是否为网站地址
     */
    public boolean isWebAddress() {
        return "WEB_ADDRESS".equals(contactMechTypeId);
    }
}
