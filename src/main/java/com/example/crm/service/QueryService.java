package com.example.crm.service;

import com.example.crm.util.RsqlUtil;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * 通用查询服务
 * 提供基于 RSQL 的灵活查询功能
 */
@ApplicationScoped
public class QueryService {

    /**
     * 验证 RSQL 查询字符串
     * 
     * @param rsqlQuery RSQL 查询字符串
     * @return 是否有效
     */
    public boolean isValidRsqlQuery(String rsqlQuery) {
        return RsqlUtil.isValidRsqlQuery(rsqlQuery);
    }

    /**
     * 获取 RSQL 查询的解析错误信息
     * 
     * @param rsqlQuery RSQL 查询字符串
     * @return 错误信息，如果没有错误则返回 null
     */
    public String getRsqlParseError(String rsqlQuery) {
        return RsqlUtil.getRsqlParseError(rsqlQuery);
    }

    /**
     * 解析 RSQL 查询并返回查询结果
     * 
     * @param rsqlQuery RSQL 查询字符串
     * @return 查询结果，包含 HQL 查询字符串和参数
     */
    public RsqlUtil.QueryResult parseRsqlQuery(String rsqlQuery) {
        return RsqlUtil.parseRsqlQuery(rsqlQuery);
    }
}
