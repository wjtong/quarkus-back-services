package com.example.crm.service;

import com.example.crm.entity.Party;
import com.example.crm.entity.Person;
import com.example.crm.entity.PartyContactMech;
import com.example.crm.util.RsqlUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

/**
 * Party 服务类
 * 提供 Party 相关的业务逻辑
 */
@ApplicationScoped
public class PartyService {

    @Inject
    QueryService queryService;

    /**
     * 获取所有 Party
     */
    public List<Party> getAllParties() {
        return Party.listAll();
    }

    /**
     * 根据ID获取 Party
     */
    public Optional<Party> getPartyById(String partyId) {
        return Party.findByIdOptional(partyId);
    }

    /**
     * 根据名称搜索 Party
     */
    public List<Party> searchPartiesByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return getAllParties();
        }
        
        String searchPattern = "%" + name.toLowerCase() + "%";
        return Party.find("LOWER(partyName) LIKE ?1 OR LOWER(description) LIKE ?1", searchPattern).list();
    }

    /**
     * 根据类型获取 Party 列表
     */
    public List<Party> getPartiesByType(String partyTypeId) {
        return Party.find("partyTypeId", partyTypeId).list();
    }

    /**
     * 创建新 Party
     */
    @Transactional
    public Party createParty(@Valid Party party) {
        // 检查 Party ID 是否已存在
        if (getPartyById(party.partyId).isPresent()) {
            throw new IllegalArgumentException("Party ID 已存在: " + party.partyId);
        }
        
        party.persist();
        return party;
    }

    /**
     * 更新 Party 信息
     */
    @Transactional
    public Party updateParty(String partyId, @Valid Party partyData) {
        Party party = Party.findById(partyId);
        if (party == null) {
            throw new IllegalArgumentException("Party 不存在，ID: " + partyId);
        }

        // 更新 Party 信息
        party.partyTypeId = partyData.partyTypeId;
        party.externalId = partyData.externalId;
        party.preferredCurrencyUomId = partyData.preferredCurrencyUomId;
        party.description = partyData.description;
        party.statusId = partyData.statusId;
        party.partyName = partyData.partyName;

        return party;
    }

    /**
     * 删除 Party
     */
    @Transactional
    public boolean deleteParty(String partyId) {
        Party party = Party.findById(partyId);
        if (party == null) {
            return false;
        }
        party.delete();
        return true;
    }

    /**
     * 创建个人 Party
     */
    @Transactional
    public Party createPersonParty(@Valid Party party, @Valid Person person) {
        // 创建 Party
        party = createParty(party);
        
        // 创建 Person
        person.partyId = party.partyId;
        person.persist();
        
        return party;
    }

    /**
     * 更新个人信息
     */
    @Transactional
    public Person updatePerson(String partyId, @Valid Person personData) {
        Person person = Person.findById(partyId);
        if (person == null) {
            throw new IllegalArgumentException("Person 不存在，Party ID: " + partyId);
        }

        // 更新个人信息
        person.salutation = personData.salutation;
        person.firstName = personData.firstName;
        person.middleName = personData.middleName;
        person.lastName = personData.lastName;
        person.personalTitle = personData.personalTitle;
        person.suffix = personData.suffix;
        person.nickname = personData.nickname;
        person.gender = personData.gender;
        person.birthDate = personData.birthDate;
        person.memberId = personData.memberId;

        return person;
    }

    /**
     * 获取 Party 的联系方式
     */
    public List<PartyContactMech> getPartyContactMechanisms(String partyId) {
        return PartyContactMech.find("partyId", partyId).list();
    }

    /**
     * 添加联系方式到 Party
     */
    @Transactional
    public PartyContactMech addContactMechanism(@Valid PartyContactMech partyContactMech) {
        partyContactMech.persist();
        return partyContactMech;
    }

    /**
     * 移除 Party 的联系方式
     */
    @Transactional
    public boolean removeContactMechanism(String partyId, String contactMechId) {
        PartyContactMech partyContactMech = PartyContactMech.find("partyId = ?1 AND contactMechId = ?2", 
                partyId, contactMechId).firstResult();
        if (partyContactMech == null) {
            return false;
        }
        partyContactMech.delete();
        return true;
    }

    /**
     * 获取客户列表（个人类型）
     */
    public List<Party> getCustomers() {
        return Party.find("partyTypeId = 'PERSON'").list();
    }

    /**
     * 获取供应商列表
     */
    public List<Party> getSuppliers() {
        return Party.find("partyTypeId = 'PARTY_GROUP'").list();
    }

    /**
     * 根据状态获取 Party 列表
     */
    public List<Party> getPartiesByStatus(String statusId) {
        return Party.find("statusId", statusId).list();
    }

    /**
     * 使用 RSQL 查询 Party
     * 
     * @param rsqlQuery RSQL 查询字符串，例如：
     *                  - partyName==*acme* (名称包含 acme)
     *                  - partyTypeId=in=(PERSON,PARTY_GROUP) (类型为 PERSON 或 PARTY_GROUP)
     *                  - createdDate>=2024-01-01 (创建日期大于等于 2024-01-01)
     *                  - partyName==*test*;statusId==ACTIVE (名称包含 test 且状态为 ACTIVE)
     * @return 查询结果列表
     */
    public List<Party> queryParties(String rsqlQuery) {
        if (rsqlQuery == null || rsqlQuery.trim().isEmpty()) {
            return getAllParties();
        }

        try {
            RsqlUtil.QueryResult queryResult = queryService.parseRsqlQuery(rsqlQuery);
            
            if (queryResult.getQuery().trim().isEmpty()) {
                return getAllParties();
            }

            return Party.find(queryResult.getQuery(), queryResult.getParamsArray()).list();
        } catch (Exception e) {
            throw new IllegalArgumentException("RSQL 查询执行失败: " + e.getMessage(), e);
        }
    }

    /**
     * 使用 RSQL 查询 Party 并支持分页
     * 
     * @param rsqlQuery RSQL 查询字符串
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 查询结果列表
     */
    public List<Party> queryPartiesWithPagination(String rsqlQuery, int page, int size) {
        if (rsqlQuery == null || rsqlQuery.trim().isEmpty()) {
            return getAllParties().stream()
                    .skip(page * size)
                    .limit(size)
                    .collect(java.util.stream.Collectors.toList());
        }

        try {
            RsqlUtil.QueryResult queryResult = queryService.parseRsqlQuery(rsqlQuery);
            
            if (queryResult.getQuery().trim().isEmpty()) {
                return getAllParties().stream()
                        .skip(page * size)
                        .limit(size)
                        .collect(java.util.stream.Collectors.toList());
            }

            return Party.find(queryResult.getQuery(), queryResult.getParamsArray())
                    .page(page, size).list();
        } catch (Exception e) {
            throw new IllegalArgumentException("RSQL 查询执行失败: " + e.getMessage(), e);
        }
    }

    /**
     * 使用 RSQL 查询 Party 总数
     * 
     * @param rsqlQuery RSQL 查询字符串
     * @return 查询结果总数
     */
    public long queryPartiesCount(String rsqlQuery) {
        if (rsqlQuery == null || rsqlQuery.trim().isEmpty()) {
            return Party.count();
        }

        try {
            RsqlUtil.QueryResult queryResult = queryService.parseRsqlQuery(rsqlQuery);
            
            if (queryResult.getQuery().trim().isEmpty()) {
                return Party.count();
            }

            return Party.find(queryResult.getQuery(), queryResult.getParamsArray()).count();
        } catch (Exception e) {
            throw new IllegalArgumentException("RSQL 查询计数失败: " + e.getMessage(), e);
        }
    }

    /**
     * 验证 RSQL 查询字符串
     * 
     * @param rsqlQuery RSQL 查询字符串
     * @return 是否有效
     */
    public boolean isValidRsqlQuery(String rsqlQuery) {
        return queryService.isValidRsqlQuery(rsqlQuery);
    }

    /**
     * 获取 RSQL 查询的解析错误信息
     * 
     * @param rsqlQuery RSQL 查询字符串
     * @return 错误信息，如果没有错误则返回 null
     */
    public String getRsqlParseError(String rsqlQuery) {
        return queryService.getRsqlParseError(rsqlQuery);
    }
}
