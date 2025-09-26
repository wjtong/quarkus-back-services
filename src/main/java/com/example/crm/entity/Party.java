package com.example.crm.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Party 实体类 - OFBiz 中的核心实体
 * 代表客户、供应商、员工等所有参与方
 */
@Entity
@Table(name = "party")
public class Party extends PanacheEntityBase {

    @Id
    @NotBlank(message = "Party ID 不能为空")
    @Size(max = 60, message = "Party ID 长度不能超过60个字符")
    @Column(name = "party_id", length = 60, nullable = false)
    public String partyId;

    @Size(max = 60, message = "Party 类型 ID 长度不能超过60个字符")
    @Column(name = "party_type_id", length = 60)
    public String partyTypeId;

    @Size(max = 60, message = "外部 ID 长度不能超过60个字符")
    @Column(name = "external_id", length = 60)
    public String externalId;

    @Size(max = 60, message = "首选货币单位 ID 长度不能超过60个字符")
    @Column(name = "preferred_currency_uom_id", length = 60)
    public String preferredCurrencyUomId;

    @Column(name = "description", columnDefinition = "text")
    public String description;

    @Size(max = 60, message = "状态 ID 长度不能超过60个字符")
    @Column(name = "status_id", length = 60)
    public String statusId;

    @Column(name = "created_date")
    public LocalDateTime createdDate;

    @Size(max = 320, message = "创建用户登录名长度不能超过320个字符")
    @Column(name = "created_by_user_login", length = 320)
    public String createdByUserLogin;

    @Column(name = "last_modified_date")
    public LocalDateTime lastModifiedDate;

    @Size(max = 320, message = "最后修改用户登录名长度不能超过320个字符")
    @Column(name = "last_modified_by_user_login", length = 320)
    public String lastModifiedByUserLogin;

    @Size(max = 60, message = "数据源 ID 长度不能超过60个字符")
    @Column(name = "data_source_id", length = 60)
    public String dataSourceId;

    @Column(name = "is_unread", length = 1)
    public String isUnread;

    @Column(name = "last_updated_stamp")
    public LocalDateTime lastUpdatedStamp;

    @Column(name = "last_updated_tx_stamp")
    public LocalDateTime lastUpdatedTxStamp;

    @Column(name = "created_stamp")
    public LocalDateTime createdStamp;

    @Column(name = "created_tx_stamp")
    public LocalDateTime createdTxStamp;

    @Size(max = 100, message = "Party 名称长度不能超过100个字符")
    @Column(name = "party_name", length = 100)
    public String partyName;

    // 关联的 Person 信息（一对一关系）
    @OneToOne(mappedBy = "party", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public Person person;

    // 关联的联系方式（一对多关系）
    @OneToMany(mappedBy = "party", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public java.util.List<PartyContactMech> contactMechanisms;

    /**
     * 获取完整的显示名称
     */
    public String getDisplayName() {
        if (person != null) {
            return person.getFullName();
        }
        return partyName != null ? partyName : partyId;
    }

    /**
     * 检查是否为个人
     */
    public boolean isPerson() {
        return person != null;
    }

    /**
     * 检查是否为组织
     */
    public boolean isOrganization() {
        return person == null;
    }
}
