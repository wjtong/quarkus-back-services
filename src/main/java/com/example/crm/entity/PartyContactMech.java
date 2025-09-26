package com.example.crm.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * PartyContactMech 实体类 - Party 和 ContactMech 的关联表
 * 管理 Party 的联系方式
 */
@Entity
@Table(name = "party_contact_mech")
public class PartyContactMech extends PanacheEntityBase {

    @Id
    @NotBlank(message = "Party ID 不能为空")
    @Size(max = 60, message = "Party ID 长度不能超过60个字符")
    @Column(name = "party_id", length = 60, nullable = false)
    public String partyId;

    @Id
    @NotBlank(message = "联系方式 ID 不能为空")
    @Size(max = 60, message = "联系方式 ID 长度不能超过60个字符")
    @Column(name = "contact_mech_id", length = 60, nullable = false)
    public String contactMechId;

    @Column(name = "from_date")
    public LocalDateTime fromDate;

    @Column(name = "thru_date")
    public LocalDateTime thruDate;

    @Size(max = 60, message = "角色类型 ID 长度不能超过60个字符")
    @Column(name = "role_type_id", length = 60)
    public String roleTypeId;

    @Column(name = "allow_solicitation", length = 1)
    public String allowSolicitation;

    @Column(name = "extension", length = 1)
    public String extension;

    @Column(name = "verified", length = 1)
    public String verified;

    @Column(name = "comments", length = 255)
    public String comments;

    @Column(name = "years_with_contact_mech")
    public Integer yearsWithContactMech;

    @Column(name = "months_with_contact_mech")
    public Integer monthsWithContactMech;

    @Column(name = "last_updated_stamp")
    public LocalDateTime lastUpdatedStamp;

    @Column(name = "last_updated_tx_stamp")
    public LocalDateTime lastUpdatedTxStamp;

    @Column(name = "created_stamp")
    public LocalDateTime createdStamp;

    @Column(name = "created_tx_stamp")
    public LocalDateTime createdTxStamp;

    // 与 Party 的多对一关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_id", insertable = false, updatable = false)
    public Party party;

    // 与 ContactMech 的多对一关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_mech_id", insertable = false, updatable = false)
    public ContactMech contactMech;

    /**
     * 检查联系方式是否有效
     */
    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return (fromDate == null || fromDate.isBefore(now) || fromDate.isEqual(now)) &&
               (thruDate == null || thruDate.isAfter(now));
    }

    /**
     * 检查是否允许营销
     */
    public boolean isAllowSolicitation() {
        return "Y".equals(allowSolicitation);
    }

    /**
     * 检查是否已验证
     */
    public boolean isVerified() {
        return "Y".equals(verified);
    }

    /**
     * 获取角色类型描述
     */
    public String getRoleTypeDescription() {
        if (roleTypeId == null) {
            return "未知";
        }
        
        switch (roleTypeId) {
            case "BILLING_LOCATION":
                return "账单地址";
            case "SHIPPING_LOCATION":
                return "配送地址";
            case "HOME_ADDRESS":
                return "家庭地址";
            case "WORK_ADDRESS":
                return "工作地址";
            case "PRIMARY_EMAIL":
                return "主要邮箱";
            case "SECONDARY_EMAIL":
                return "次要邮箱";
            case "PRIMARY_PHONE":
                return "主要电话";
            case "SECONDARY_PHONE":
                return "次要电话";
            default:
                return roleTypeId;
        }
    }
}
