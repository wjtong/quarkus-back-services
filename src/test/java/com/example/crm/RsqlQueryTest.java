package com.example.crm;

import com.example.crm.util.RsqlUtil;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RSQL 查询功能测试
 */
@QuarkusTest
public class RsqlQueryTest {

    @Test
    public void testSimpleEqualQuery() {
        String rsqlQuery = "productName==test";
        RsqlUtil.QueryResult result = RsqlUtil.parseRsqlQuery(rsqlQuery);
        
        assertNotNull(result);
        assertEquals("productName = ?1", result.getQuery());
        assertEquals(1, result.getParams().size());
        assertEquals("test", result.getParams().get(0));
    }

    @Test
    public void testLikeQuery() {
        String rsqlQuery = "productName==*acme*";
        RsqlUtil.QueryResult result = RsqlUtil.parseRsqlQuery(rsqlQuery);
        
        assertNotNull(result);
        assertEquals("productName = ?1", result.getQuery());
        assertEquals(1, result.getParams().size());
        assertEquals("*acme*", result.getParams().get(0));
    }

    @Test
    public void testInQuery() {
        String rsqlQuery = "statusId=in=(ACTIVE,NEW)";
        RsqlUtil.QueryResult result = RsqlUtil.parseRsqlQuery(rsqlQuery);
        
        assertNotNull(result);
        assertEquals("statusId IN (?,?)", result.getQuery());
        assertEquals(2, result.getParams().size());
        assertEquals("ACTIVE", result.getParams().get(0));
        assertEquals("NEW", result.getParams().get(1));
    }

    @Test
    public void testGreaterThanQuery() {
        String rsqlQuery = "createdDate=ge=2024-01-01";
        RsqlUtil.QueryResult result = RsqlUtil.parseRsqlQuery(rsqlQuery);
        
        assertNotNull(result);
        assertEquals("createdDate >= ?1", result.getQuery());
        assertEquals(1, result.getParams().size());
        assertNotNull(result.getParams().get(0));
    }

    @Test
    public void testAndQuery() {
        String rsqlQuery = "productName==*test*;isVirtual==Y";
        RsqlUtil.QueryResult result = RsqlUtil.parseRsqlQuery(rsqlQuery);
        
        assertNotNull(result);
        assertTrue(result.getQuery().contains("AND"));
        assertTrue(result.getQuery().contains("productName ="));
        assertTrue(result.getQuery().contains("isVirtual ="));
        assertEquals(2, result.getParams().size());
    }

    @Test
    public void testOrQuery() {
        String rsqlQuery = "productName==*test*,isVirtual==Y";
        RsqlUtil.QueryResult result = RsqlUtil.parseRsqlQuery(rsqlQuery);
        
        assertNotNull(result);
        assertTrue(result.getQuery().contains("OR"));
        assertTrue(result.getQuery().contains("productName ="));
        assertTrue(result.getQuery().contains("isVirtual ="));
        assertEquals(2, result.getParams().size());
    }

    @Test
    public void testInvalidQuery() {
        String invalidQuery = "invalid syntax";
        assertFalse(RsqlUtil.isValidRsqlQuery(invalidQuery));
        
        String error = RsqlUtil.getRsqlParseError(invalidQuery);
        assertNotNull(error);
    }

    @Test
    public void testEmptyQuery() {
        String emptyQuery = "";
        assertTrue(RsqlUtil.isValidRsqlQuery(emptyQuery));
        
        RsqlUtil.QueryResult result = RsqlUtil.parseRsqlQuery(emptyQuery);
        assertNotNull(result);
        assertTrue(result.getQuery().isEmpty());
        assertTrue(result.getParams().isEmpty());
    }

    @Test
    public void testNullQuery() {
        assertTrue(RsqlUtil.isValidRsqlQuery(null));
        
        RsqlUtil.QueryResult result = RsqlUtil.parseRsqlQuery(null);
        assertNotNull(result);
        assertTrue(result.getQuery().isEmpty());
        assertTrue(result.getParams().isEmpty());
    }
}
