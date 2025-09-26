package com.example.crm;

import com.example.crm.entity.Party;
import com.example.crm.entity.Person;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Party 和 Person 序列化测试
 * 测试双向关联的无限循环问题是否已解决
 */
@QuarkusTest
public class PartyPersonSerializationTest {

    @Test
    public void testPartyPersonSerialization() throws JsonProcessingException {
        // 创建 Party 对象
        Party party = new Party();
        party.partyId = "TEST_PARTY_001";
        party.partyTypeId = "PERSON";
        party.partyName = "测试客户";
        party.description = "测试客户描述";

        // 创建 Person 对象
        Person person = new Person();
        person.partyId = "TEST_PARTY_001";
        person.firstName = "张";
        person.lastName = "三";
        person.gender = "M";

        // 建立双向关联
        party.person = person;
        person.party = party;

        // 序列化为 JSON
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(party);

        // 验证序列化成功（不会抛出异常）
        assertNotNull(json);
        assertFalse(json.isEmpty());

        // 验证 JSON 包含 Party 信息
        assertTrue(json.contains("TEST_PARTY_001"));
        assertTrue(json.contains("测试客户"));
        assertTrue(json.contains("PERSON"));

        // 验证 JSON 包含 Person 信息
        assertTrue(json.contains("张"));
        assertTrue(json.contains("三"));
        assertTrue(json.contains("M"));

        // 验证 JSON 不包含循环引用（Person 中不应该包含 Party 信息）
        // 由于 Person.party 被 @JsonIgnore，所以 JSON 中不应该包含 Party 的详细信息
        assertFalse(json.contains("\"party\":{"));

        System.out.println("序列化结果:");
        System.out.println(json);
    }

    @Test
    public void testPersonSerialization() throws JsonProcessingException {
        // 创建 Party 对象
        Party party = new Party();
        party.partyId = "TEST_PARTY_002";
        party.partyTypeId = "PERSON";
        party.partyName = "测试客户2";

        // 创建 Person 对象
        Person person = new Person();
        person.partyId = "TEST_PARTY_002";
        person.firstName = "李";
        person.lastName = "四";
        person.gender = "F";

        // 建立双向关联
        party.person = person;
        person.party = party;

        // 序列化 Person 为 JSON
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(person);

        // 验证序列化成功
        assertNotNull(json);
        assertFalse(json.isEmpty());

        // 验证 JSON 包含 Person 信息
        assertTrue(json.contains("TEST_PARTY_002"));
        assertTrue(json.contains("李"));
        assertTrue(json.contains("四"));
        assertTrue(json.contains("F"));

        // 验证 JSON 不包含 Party 信息（因为 @JsonIgnore）
        assertFalse(json.contains("\"party\":{"));

        System.out.println("Person 序列化结果:");
        System.out.println(json);
    }

    @Test
    public void testPartyWithoutPerson() throws JsonProcessingException {
        // 创建没有 Person 的 Party 对象
        Party party = new Party();
        party.partyId = "TEST_PARTY_003";
        party.partyTypeId = "ORGANIZATION";
        party.partyName = "测试组织";

        // 序列化为 JSON
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(party);

        // 验证序列化成功
        assertNotNull(json);
        assertFalse(json.isEmpty());

        // 验证 JSON 包含 Party 信息
        assertTrue(json.contains("TEST_PARTY_003"));
        assertTrue(json.contains("测试组织"));
        assertTrue(json.contains("ORGANIZATION"));

        // 验证 person 字段为 null
        assertTrue(json.contains("\"person\":null"));

        System.out.println("Party 无 Person 序列化结果:");
        System.out.println(json);
    }

    @Test
    public void testPartyDisplayName() {
        // 测试 Party 的显示名称方法
        Party party = new Party();
        party.partyId = "TEST_PARTY_004";
        party.partyName = "组织名称";

        // 没有 Person 时，应该返回 partyName
        assertEquals("组织名称", party.getDisplayName());

        // 有 Person 时，应该返回 Person 的完整姓名
        Person person = new Person();
        person.firstName = "王";
        person.lastName = "五";
        party.person = person;

        assertEquals("王 五", party.getDisplayName());
    }

    @Test
    public void testPersonFullName() {
        // 测试 Person 的完整姓名方法
        Person person = new Person();
        person.firstName = "赵";
        person.middleName = "六";
        person.lastName = "七";

        assertEquals("赵 六 七", person.getFullName());

        // 测试带称谓的姓名
        person.salutation = "先生";
        assertEquals("先生 赵 六 七", person.getFullName());
    }
}
