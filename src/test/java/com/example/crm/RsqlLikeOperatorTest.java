package com.example.crm;

import com.example.crm.util.RsqlUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RSQL Like 操作符测试
 * 测试 =like= 和 =notlike= 操作符是否正常工作
 */
public class RsqlLikeOperatorTest {

    @Test
    public void testLikeOperator() {
        // 测试 =like= 操作符
        String rsqlQuery = "productName=like=*test*";
        RsqlUtil.QueryResult result = RsqlUtil.parseRsqlQuery(rsqlQuery);

        assertNotNull(result);
        assertNotNull(result.getQuery());
        assertTrue(result.getQuery().contains("LIKE"));
        assertTrue(result.getQuery().contains("productName"));
        assertEquals(1, result.getParams().size());
        assertEquals("%test%", result.getParams().get(0));
    }

    @Test
    public void testNotLikeOperator() {
        // 测试 =notlike= 操作符
        String rsqlQuery = "productName=notlike=*test*";
        RsqlUtil.QueryResult result = RsqlUtil.parseRsqlQuery(rsqlQuery);

        assertNotNull(result);
        assertNotNull(result.getQuery());
        assertTrue(result.getQuery().contains("NOT LIKE"));
        assertTrue(result.getQuery().contains("productName"));
        assertEquals(1, result.getParams().size());
        assertEquals("%test%", result.getParams().get(0));
    }

    @Test
    public void testLikeOperatorWithPrefix() {
        // 测试前缀匹配
        String rsqlQuery = "productName=like=test*";
        RsqlUtil.QueryResult result = RsqlUtil.parseRsqlQuery(rsqlQuery);

        assertNotNull(result);
        assertNotNull(result.getQuery());
        assertTrue(result.getQuery().contains("LIKE"));
        assertEquals(1, result.getParams().size());
        assertEquals("test%", result.getParams().get(0));
    }

    @Test
    public void testLikeOperatorWithSuffix() {
        // 测试后缀匹配
        String rsqlQuery = "productName=like=*test";
        RsqlUtil.QueryResult result = RsqlUtil.parseRsqlQuery(rsqlQuery);

        assertNotNull(result);
        assertNotNull(result.getQuery());
        assertTrue(result.getQuery().contains("LIKE"));
        assertEquals(1, result.getParams().size());
        assertEquals("%test", result.getParams().get(0));
    }

    @Test
    public void testLikeOperatorExactMatch() {
        // 测试精确匹配
        String rsqlQuery = "productName=like=test";
        RsqlUtil.QueryResult result = RsqlUtil.parseRsqlQuery(rsqlQuery);

        assertNotNull(result);
        assertNotNull(result.getQuery());
        assertTrue(result.getQuery().contains("LIKE"));
        assertEquals(1, result.getParams().size());
        assertEquals("test", result.getParams().get(0));
    }

    @Test
    public void testLikeOperatorWithAnd() {
        // 测试与 AND 操作符结合
        String rsqlQuery = "productName=like=*test*;isVirtual==Y";
        RsqlUtil.QueryResult result = RsqlUtil.parseRsqlQuery(rsqlQuery);

        assertNotNull(result);
        assertNotNull(result.getQuery());
        assertTrue(result.getQuery().contains("AND"));
        assertTrue(result.getQuery().contains("LIKE"));
        assertTrue(result.getQuery().contains("isVirtual ="));
        assertEquals(2, result.getParams().size());
    }

    @Test
    public void testLikeOperatorWithOr() {
        // 测试与 OR 操作符结合
        String rsqlQuery = "productName=like=*test*,productName=like=*demo*";
        RsqlUtil.QueryResult result = RsqlUtil.parseRsqlQuery(rsqlQuery);

        assertNotNull(result);
        assertNotNull(result.getQuery());
        assertTrue(result.getQuery().contains("OR"));
        assertTrue(result.getQuery().contains("LIKE"));
        assertEquals(2, result.getParams().size());
    }

    @Test
    public void testPartyNameLike() {
        // 测试 Party 名称的 like 查询
        String rsqlQuery = "partyName=like=*acme*";
        RsqlUtil.QueryResult result = RsqlUtil.parseRsqlQuery(rsqlQuery);

        assertNotNull(result);
        assertNotNull(result.getQuery());
        assertTrue(result.getQuery().contains("LIKE"));
        assertTrue(result.getQuery().contains("partyName"));
        assertEquals(1, result.getParams().size());
        assertEquals("%acme%", result.getParams().get(0));
    }

    @Test
    public void testComplexLikeQuery() {
        // 测试复杂的 like 查询
        String rsqlQuery = "partyName=like=*acme*;partyTypeId==PERSON";
        RsqlUtil.QueryResult result = RsqlUtil.parseRsqlQuery(rsqlQuery);

        assertNotNull(result);
        assertNotNull(result.getQuery());
        assertTrue(result.getQuery().contains("AND"));
        assertTrue(result.getQuery().contains("LIKE"));
        assertTrue(result.getQuery().contains("partyName"));
        assertTrue(result.getQuery().contains("partyTypeId ="));
        assertEquals(2, result.getParams().size());
    }

    @Test
    public void testLikeOperatorValidation() {
        // 测试 like 操作符的验证
        assertTrue(RsqlUtil.isValidRsqlQuery("productName=like=*test*"));
        assertTrue(RsqlUtil.isValidRsqlQuery("productName=notlike=*test*"));
        assertTrue(RsqlUtil.isValidRsqlQuery("partyName=like=*acme*"));
    }
}
