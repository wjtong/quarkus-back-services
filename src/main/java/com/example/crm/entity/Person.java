package com.example.crm.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Person 实体类 - 个人信息
 * 与 Party 实体一对一关联
 */
@Entity
@Table(name = "person")
public class Person extends PanacheEntityBase {

    @Id
    @NotBlank(message = "Party ID 不能为空")
    @Size(max = 60, message = "Party ID 长度不能超过60个字符")
    @Column(name = "party_id", length = 60, nullable = false)
    public String partyId;

    @Size(max = 100, message = "称谓长度不能超过100个字符")
    @Column(name = "salutation", length = 100)
    public String salutation;

    @Size(max = 100, message = "名字长度不能超过100个字符")
    @Column(name = "first_name", length = 100)
    public String firstName;

    @Size(max = 100, message = "中间名长度不能超过100个字符")
    @Column(name = "middle_name", length = 100)
    public String middleName;

    @Size(max = 100, message = "姓氏长度不能超过100个字符")
    @Column(name = "last_name", length = 100)
    public String lastName;

    @Size(max = 100, message = "个人头衔长度不能超过100个字符")
    @Column(name = "personal_title", length = 100)
    public String personalTitle;

    @Size(max = 100, message = "后缀长度不能超过100个字符")
    @Column(name = "suffix", length = 100)
    public String suffix;

    @Size(max = 100, message = "昵称长度不能超过100个字符")
    @Column(name = "nickname", length = 100)
    public String nickname;

    @Size(max = 100, message = "本地名字长度不能超过100个字符")
    @Column(name = "first_name_local", length = 100)
    public String firstNameLocal;

    @Size(max = 100, message = "本地中间名长度不能超过100个字符")
    @Column(name = "middle_name_local", length = 100)
    public String middleNameLocal;

    @Size(max = 100, message = "本地姓氏长度不能超过100个字符")
    @Column(name = "last_name_local", length = 100)
    public String lastNameLocal;

    @Size(max = 100, message = "其他本地名称长度不能超过100个字符")
    @Column(name = "other_local", length = 100)
    public String otherLocal;

    @Size(max = 60, message = "会员 ID 长度不能超过60个字符")
    @Column(name = "member_id", length = 60)
    public String memberId;

    @Column(name = "gender", length = 1)
    public String gender;

    @Column(name = "birth_date")
    public LocalDate birthDate;

    @Column(name = "deceased_date")
    public LocalDate deceasedDate;

    @Column(name = "height")
    public Double height;

    // 与 Party 的一对一关系
    @OneToOne
    @JoinColumn(name = "party_id")
    @MapsId
    @JsonIgnore  // 防止 JSON 序列化时的无限循环
    public Party party;

    /**
     * 获取完整姓名
     */
    public String getFullName() {
        StringBuilder fullName = new StringBuilder();
        
        if (salutation != null && !salutation.trim().isEmpty()) {
            fullName.append(salutation).append(" ");
        }
        
        if (firstName != null && !firstName.trim().isEmpty()) {
            fullName.append(firstName).append(" ");
        }
        
        if (middleName != null && !middleName.trim().isEmpty()) {
            fullName.append(middleName).append(" ");
        }
        
        if (lastName != null && !lastName.trim().isEmpty()) {
            fullName.append(lastName);
        }
        
        return fullName.toString().trim();
    }

    /**
     * 获取显示名称（优先使用昵称）
     */
    public String getDisplayName() {
        if (nickname != null && !nickname.trim().isEmpty()) {
            return nickname;
        }
        return getFullName();
    }

    /**
     * 获取年龄
     */
    public Integer getAge() {
        if (birthDate == null) {
            return null;
        }
        return LocalDate.now().getYear() - birthDate.getYear();
    }

    /**
     * 检查是否已故
     */
    public boolean isDeceased() {
        return deceasedDate != null;
    }
}
