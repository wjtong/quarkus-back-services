package com.example.crm.util;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * RSQL 工具类
 * 用于解析 RSQL/FIQL 查询字符串并转换为 Panache 查询
 */
public class RsqlUtil {

    private static final RSQLParser RSQL_PARSER = new RSQLParser(
        Set.of(
            RSQLOperators.EQUAL,
            RSQLOperators.NOT_EQUAL,
            RSQLOperators.GREATER_THAN,
            RSQLOperators.GREATER_THAN_OR_EQUAL,
            RSQLOperators.LESS_THAN,
            RSQLOperators.LESS_THAN_OR_EQUAL,
            RSQLOperators.IN,
            RSQLOperators.NOT_IN,
            new ComparisonOperator("=like=", true),
            new ComparisonOperator("=notlike=", true)
        )
    );
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 解析 RSQL 查询字符串并返回查询条件和参数
     * 
     * @param rsqlQuery RSQL 查询字符串
     * @return 查询结果，包含 HQL 查询字符串和参数列表
     */
    public static QueryResult parseRsqlQuery(String rsqlQuery) {
        if (rsqlQuery == null || rsqlQuery.trim().isEmpty()) {
            return new QueryResult("", new ArrayList<>());
        }

        try {
            Node rootNode = RSQL_PARSER.parse(rsqlQuery);
            return buildQuery(rootNode);
        } catch (Exception e) {
            throw new IllegalArgumentException("无效的 RSQL 查询: " + rsqlQuery, e);
        }
    }

    /**
     * 构建查询
     */
    private static QueryResult buildQuery(Node node) {
        if (node instanceof LogicalNode) {
            return buildLogicalQuery((LogicalNode) node);
        } else if (node instanceof ComparisonNode) {
            return buildComparisonQuery((ComparisonNode) node);
        } else {
            throw new IllegalArgumentException("不支持的节点类型: " + node.getClass().getSimpleName());
        }
    }

    /**
     * 处理逻辑节点 (AND, OR)
     */
    private static QueryResult buildLogicalQuery(LogicalNode node) {
        List<QueryResult> results = node.getChildren().stream()
                .map(RsqlUtil::buildQuery)
                .collect(Collectors.toList());

        if (node.getOperator() == LogicalOperator.AND) {
            return combineWithAnd(results);
        } else if (node.getOperator() == LogicalOperator.OR) {
            return combineWithOr(results);
        } else {
            throw new IllegalArgumentException("不支持的逻辑操作符: " + node.getOperator());
        }
    }

    /**
     * 处理比较节点
     */
    private static QueryResult buildComparisonQuery(ComparisonNode node) {
        String selector = node.getSelector();
        ComparisonOperator operator = node.getOperator();
        List<String> arguments = node.getArguments();

        if (arguments.isEmpty()) {
            throw new IllegalArgumentException("比较操作缺少参数");
        }

        String argument = arguments.get(0);
        
        // 根据操作符构建查询
        String operatorSymbol = operator.getSymbol();
        if ("==".equals(operatorSymbol)) {
            return buildEqualQuery(selector, argument);
        } else if ("!=".equals(operatorSymbol)) {
            return buildNotEqualQuery(selector, argument);
        } else if (">".equals(operatorSymbol)) {
            return buildGreaterThanQuery(selector, argument);
        } else if (">=".equals(operatorSymbol)) {
            return buildGreaterThanOrEqualQuery(selector, argument);
        } else if ("<".equals(operatorSymbol)) {
            return buildLessThanQuery(selector, argument);
        } else if ("<=".equals(operatorSymbol)) {
            return buildLessThanOrEqualQuery(selector, argument);
        } else if ("=gt=".equals(operatorSymbol)) {
            return buildGreaterThanQuery(selector, argument);
        } else if ("=ge=".equals(operatorSymbol)) {
            return buildGreaterThanOrEqualQuery(selector, argument);
        } else if ("=lt=".equals(operatorSymbol)) {
            return buildLessThanQuery(selector, argument);
        } else if ("=le=".equals(operatorSymbol)) {
            return buildLessThanOrEqualQuery(selector, argument);
        } else if ("=in=".equals(operatorSymbol)) {
            return buildInQuery(selector, arguments);
        } else if ("=out=".equals(operatorSymbol)) {
            return buildNotInQuery(selector, arguments);
        } else if ("=like=".equals(operatorSymbol)) {
            return buildLikeQuery(selector, argument);
        } else if ("=notlike=".equals(operatorSymbol)) {
            return buildNotLikeQuery(selector, argument);
        } else {
            throw new IllegalArgumentException("不支持的操作符: " + operator);
        }
    }

    /**
     * 创建等于查询
     */
    private static QueryResult buildEqualQuery(String selector, String argument) {
        Object value = parseValue(argument);
        return new QueryResult(selector + " = ?1", Arrays.asList(value));
    }

    /**
     * 创建不等于查询
     */
    private static QueryResult buildNotEqualQuery(String selector, String argument) {
        Object value = parseValue(argument);
        return new QueryResult(selector + " != ?1", Arrays.asList(value));
    }

    /**
     * 创建大于查询
     */
    private static QueryResult buildGreaterThanQuery(String selector, String argument) {
        Object value = parseValue(argument);
        return new QueryResult(selector + " > ?1", Arrays.asList(value));
    }

    /**
     * 创建大于等于查询
     */
    private static QueryResult buildGreaterThanOrEqualQuery(String selector, String argument) {
        Object value = parseValue(argument);
        return new QueryResult(selector + " >= ?1", Arrays.asList(value));
    }

    /**
     * 创建小于查询
     */
    private static QueryResult buildLessThanQuery(String selector, String argument) {
        Object value = parseValue(argument);
        return new QueryResult(selector + " < ?1", Arrays.asList(value));
    }

    /**
     * 创建小于等于查询
     */
    private static QueryResult buildLessThanOrEqualQuery(String selector, String argument) {
        Object value = parseValue(argument);
        return new QueryResult(selector + " <= ?1", Arrays.asList(value));
    }

    /**
     * 创建 IN 查询
     */
    private static QueryResult buildInQuery(String selector, List<String> arguments) {
        List<Object> values = arguments.stream()
                .map(RsqlUtil::parseValue)
                .collect(Collectors.toList());
        
        String placeholders = values.stream()
                .map(v -> "?")
                .collect(Collectors.joining(","));
        
        return new QueryResult(selector + " IN (" + placeholders + ")", values);
    }

    /**
     * 创建 NOT IN 查询
     */
    private static QueryResult buildNotInQuery(String selector, List<String> arguments) {
        List<Object> values = arguments.stream()
                .map(RsqlUtil::parseValue)
                .collect(Collectors.toList());
        
        String placeholders = values.stream()
                .map(v -> "?")
                .collect(Collectors.joining(","));
        
        return new QueryResult(selector + " NOT IN (" + placeholders + ")", values);
    }

    /**
     * 创建 LIKE 查询
     */
    private static QueryResult buildLikeQuery(String selector, String argument) {
        // 处理通配符
        String pattern = argument.replace("*", "%");
        return new QueryResult(selector + " LIKE ?1", Arrays.asList(pattern));
    }

    /**
     * 创建 NOT LIKE 查询
     */
    private static QueryResult buildNotLikeQuery(String selector, String argument) {
        // 处理通配符
        String pattern = argument.replace("*", "%");
        return new QueryResult(selector + " NOT LIKE ?1", Arrays.asList(pattern));
    }

    /**
     * 解析值，尝试转换为适当的类型
     */
    private static Object parseValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        // 尝试解析为布尔值
        if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
            return Boolean.parseBoolean(value);
        }

        // 尝试解析为整数
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            // 继续尝试其他类型
        }

        // 尝试解析为长整数
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            // 继续尝试其他类型
        }

        // 尝试解析为浮点数
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            // 继续尝试其他类型
        }

        // 尝试解析为日期时间
        try {
            return LocalDateTime.parse(value, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            // 继续尝试其他类型
        }

        // 尝试解析为日期
        try {
            return LocalDateTime.parse(value + " 00:00:00", DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            // 继续尝试其他类型
        }

        // 默认返回字符串
        return value;
    }

    /**
     * 合并查询 - AND 操作
     */
    private static QueryResult combineWithAnd(List<QueryResult> results) {
        if (results.isEmpty()) {
            return new QueryResult("", new ArrayList<>());
        }
        
        if (results.size() == 1) {
            return results.get(0);
        }

        List<String> conditions = new ArrayList<>();
        List<Object> allParams = new ArrayList<>();
        int paramIndex = 1;

        for (QueryResult result : results) {
            if (!result.getQuery().trim().isEmpty()) {
                String adjustedQuery = result.getQuery().replace("?1", "?" + paramIndex);
                conditions.add("(" + adjustedQuery + ")");
                
                for (Object param : result.getParams()) {
                    allParams.add(param);
                    paramIndex++;
                }
            }
        }

        String combinedQuery = String.join(" AND ", conditions);
        return new QueryResult(combinedQuery, allParams);
    }

    /**
     * 合并查询 - OR 操作
     */
    private static QueryResult combineWithOr(List<QueryResult> results) {
        if (results.isEmpty()) {
            return new QueryResult("", new ArrayList<>());
        }
        
        if (results.size() == 1) {
            return results.get(0);
        }

        List<String> conditions = new ArrayList<>();
        List<Object> allParams = new ArrayList<>();
        int paramIndex = 1;

        for (QueryResult result : results) {
            if (!result.getQuery().trim().isEmpty()) {
                String adjustedQuery = result.getQuery().replace("?1", "?" + paramIndex);
                conditions.add("(" + adjustedQuery + ")");
                
                for (Object param : result.getParams()) {
                    allParams.add(param);
                    paramIndex++;
                }
            }
        }

        String combinedQuery = String.join(" OR ", conditions);
        return new QueryResult(combinedQuery, allParams);
    }

    /**
     * 验证 RSQL 查询字符串
     */
    public static boolean isValidRsqlQuery(String rsqlQuery) {
        if (rsqlQuery == null || rsqlQuery.trim().isEmpty()) {
            return true; // 空查询被认为是有效的
        }

        try {
            RSQL_PARSER.parse(rsqlQuery);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取 RSQL 查询的解析错误信息
     */
    public static String getRsqlParseError(String rsqlQuery) {
        if (rsqlQuery == null || rsqlQuery.trim().isEmpty()) {
            return null;
        }

        try {
            RSQL_PARSER.parse(rsqlQuery);
            return null;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    /**
     * 查询结果类
     */
    public static class QueryResult {
        private final String query;
        private final List<Object> params;

        public QueryResult(String query, List<Object> params) {
            this.query = query;
            this.params = params;
        }

        public String getQuery() {
            return query;
        }

        public List<Object> getParams() {
            return params;
        }

        public Object[] getParamsArray() {
            return params.toArray();
        }
    }
}
