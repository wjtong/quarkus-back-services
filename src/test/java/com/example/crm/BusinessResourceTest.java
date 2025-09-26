package com.example.crm;

import com.example.crm.entity.Party;
import com.example.crm.entity.Person;
import com.example.crm.service.PartyService;
import com.example.crm.service.ProductService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 业务资源测试类
 */
@QuarkusTest
public class BusinessResourceTest {

    @Inject
    PartyService partyService;

    @Inject
    ProductService productService;

    @Test
    public void testPartyService() {
        // 测试获取所有 Party
        assertNotNull(partyService.getAllParties());
        
        // 测试创建 Party
        Party party = new Party();
        party.partyId = "TEST_PARTY_001";
        party.partyTypeId = "PERSON";
        party.partyName = "测试客户";
        party.description = "测试客户描述";
        
        Party createdParty = partyService.createParty(party);
        assertNotNull(createdParty);
        assertNotNull(createdParty.partyId);
        assertEquals("测试客户", createdParty.partyName);
        assertEquals("PERSON", createdParty.partyTypeId);
        
        // 测试根据ID获取 Party
        assertTrue(partyService.getPartyById(createdParty.partyId).isPresent());
        
        // 测试搜索 Party
        assertNotNull(partyService.searchPartiesByName("测试"));
        
        // 测试删除 Party
        assertTrue(partyService.deleteParty(createdParty.partyId));
    }

    @Test
    public void testProductService() {
        // 测试获取所有产品
        assertNotNull(productService.getAllProducts());
        
        // 测试创建产品
        com.example.crm.entity.Product product = new com.example.crm.entity.Product();
        product.productId = "TEST_PRODUCT_001";
        product.productName = "测试产品";
        product.description = "测试产品描述";
        product.brandName = "测试品牌";
        product.requireInventory = "Y";
        product.returnable = "Y";
        product.taxable = "Y";
        
        com.example.crm.entity.Product createdProduct = productService.createProduct(product);
        assertNotNull(createdProduct);
        assertNotNull(createdProduct.productId);
        assertEquals("测试产品", createdProduct.productName);
        assertEquals("测试品牌", createdProduct.brandName);
        
        // 测试根据ID获取产品
        assertTrue(productService.getProductById(createdProduct.productId).isPresent());
        
        // 测试搜索产品
        assertNotNull(productService.searchProductsByName("测试"));
        
        // 测试删除产品
        assertTrue(productService.deleteProduct(createdProduct.productId));
    }

    @Test
    public void testPartyValidation() {
        // 测试创建 Party 时 ID 重复
        Party party1 = new Party();
        party1.partyId = "DUPLICATE_PARTY";
        party1.partyTypeId = "PERSON";
        party1.partyName = "客户1";
        
        Party party2 = new Party();
        party2.partyId = "DUPLICATE_PARTY";
        party2.partyTypeId = "PERSON";
        party2.partyName = "客户2";
        
        partyService.createParty(party1);
        
        // 尝试创建重复 ID 的 Party 应该抛出异常
        assertThrows(IllegalArgumentException.class, () -> {
            partyService.createParty(party2);
        });
    }
}
