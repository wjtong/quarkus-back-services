package com.example.crm.resource;

import com.example.crm.entity.Party;
import com.example.crm.entity.Person;
import com.example.crm.entity.PartyContactMech;
import com.example.crm.service.PartyService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;
import java.util.Optional;

/**
 * Party REST API 资源类
 * 提供 Party 管理的 RESTful 接口
 */
@Path("/parties")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Party 管理", description = "Party 信息的增删改查操作")
public class PartyResource {

    @Inject
    PartyService partyService;

    /**
     * 获取所有 Party
     */
    @GET
    @RolesAllowed({"USER", "ADMIN", "MANAGER"})
    @Operation(summary = "获取所有 Party", description = "返回系统中所有 Party 的信息，支持 RSQL 查询")
    @APIResponse(responseCode = "200", description = "成功获取 Party 列表",
            content = @Content(schema = @Schema(implementation = Party.class)))
    public Response getAllParties(@QueryParam("q") String rsqlQuery,
                                 @QueryParam("page") @DefaultValue("0") int page,
                                 @QueryParam("size") @DefaultValue("20") int size) {
        try {
            List<Party> parties;
            if (rsqlQuery != null && !rsqlQuery.trim().isEmpty()) {
                // 验证 RSQL 查询
                if (!partyService.isValidRsqlQuery(rsqlQuery)) {
                    String error = partyService.getRsqlParseError(rsqlQuery);
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("{\"error\": \"无效的 RSQL 查询: " + error + "\"}")
                            .build();
                }
                
                if (page >= 0 && size > 0) {
                    parties = partyService.queryPartiesWithPagination(rsqlQuery, page, size);
                } else {
                    parties = partyService.queryParties(rsqlQuery);
                }
            } else {
                if (page >= 0 && size > 0) {
                    parties = partyService.getAllParties().stream()
                            .skip(page * size)
                            .limit(size)
                            .collect(java.util.stream.Collectors.toList());
                } else {
                    parties = partyService.getAllParties();
                }
            }
            return Response.ok(parties).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    /**
     * 根据ID获取 Party
     */
    @GET
    @Path("/{partyId}")
    @RolesAllowed({"USER", "ADMIN", "MANAGER"})
    @Operation(summary = "根据ID获取 Party", description = "根据 Party ID 获取特定 Party 的详细信息")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "成功获取 Party 信息",
                    content = @Content(schema = @Schema(implementation = Party.class))),
            @APIResponse(responseCode = "404", description = "Party 不存在")
    })
    public Response getPartyById(@PathParam("partyId") String partyId) {
        Optional<Party> party = partyService.getPartyById(partyId);
        if (party.isPresent()) {
            return Response.ok(party.get()).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Party 不存在，ID: " + partyId + "\"}")
                    .build();
        }
    }

    /**
     * 创建新 Party
     */
    @POST
    @RolesAllowed({"ADMIN", "MANAGER"})
    @Operation(summary = "创建新 Party", description = "创建新的 Party 记录")
    @APIResponses({
            @APIResponse(responseCode = "201", description = "Party 创建成功",
                    content = @Content(schema = @Schema(implementation = Party.class))),
            @APIResponse(responseCode = "400", description = "请求数据无效或 Party ID 已存在")
    })
    public Response createParty(@Valid Party party) {
        try {
            Party createdParty = partyService.createParty(party);
            return Response.status(Response.Status.CREATED).entity(createdParty).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    /**
     * 创建个人 Party
     */
    @POST
    @Path("/person")
    @Operation(summary = "创建个人 Party", description = "创建新的个人 Party 记录")
    @APIResponses({
            @APIResponse(responseCode = "201", description = "个人 Party 创建成功",
                    content = @Content(schema = @Schema(implementation = Party.class))),
            @APIResponse(responseCode = "400", description = "请求数据无效")
    })
    public Response createPersonParty(@Valid Person person) {
        try {
            // 从 Person 中提取 Party 信息
            Party party = new Party();
            party.partyId = person.partyId;
            party.partyTypeId = "PERSON";
            party.partyName = person.getFullName();
            
            Party createdParty = partyService.createPersonParty(party, person);
            return Response.status(Response.Status.CREATED).entity(createdParty).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    /**
     * 更新 Party 信息
     */
    @PUT
    @Path("/{partyId}")
    @Operation(summary = "更新 Party 信息", description = "更新指定 Party 的信息")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Party 信息更新成功",
                    content = @Content(schema = @Schema(implementation = Party.class))),
            @APIResponse(responseCode = "400", description = "请求数据无效"),
            @APIResponse(responseCode = "404", description = "Party 不存在")
    })
    public Response updateParty(@PathParam("partyId") String partyId, @Valid Party party) {
        try {
            Party updatedParty = partyService.updateParty(partyId, party);
            return Response.ok(updatedParty).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    /**
     * 更新个人信息
     */
    @PUT
    @Path("/{partyId}/person")
    @Operation(summary = "更新个人信息", description = "更新指定 Party 的个人信息")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "个人信息更新成功",
                    content = @Content(schema = @Schema(implementation = Person.class))),
            @APIResponse(responseCode = "400", description = "请求数据无效"),
            @APIResponse(responseCode = "404", description = "Person 不存在")
    })
    public Response updatePerson(@PathParam("partyId") String partyId, @Valid Person person) {
        try {
            Person updatedPerson = partyService.updatePerson(partyId, person);
            return Response.ok(updatedPerson).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    /**
     * 删除 Party
     */
    @DELETE
    @Path("/{partyId}")
    @Operation(summary = "删除 Party", description = "删除指定的 Party 记录")
    @APIResponses({
            @APIResponse(responseCode = "204", description = "Party 删除成功"),
            @APIResponse(responseCode = "404", description = "Party 不存在")
    })
    public Response deleteParty(@PathParam("partyId") String partyId) {
        boolean deleted = partyService.deleteParty(partyId);
        if (deleted) {
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Party 不存在，ID: " + partyId + "\"}")
                    .build();
        }
    }

    /**
     * 搜索 Party
     */
    @GET
    @Path("/search")
    @Operation(summary = "搜索 Party", description = "根据名称搜索 Party")
    @APIResponse(responseCode = "200", description = "搜索成功",
            content = @Content(schema = @Schema(implementation = Party.class)))
    public Response searchParties(@QueryParam("name") String name) {
        List<Party> parties = partyService.searchPartiesByName(name);
        return Response.ok(parties).build();
    }

    /**
     * 根据类型获取 Party 列表
     */
    @GET
    @Path("/type/{partyTypeId}")
    @Operation(summary = "根据类型获取 Party", description = "根据 Party 类型获取 Party 列表")
    @APIResponse(responseCode = "200", description = "成功获取 Party 列表",
            content = @Content(schema = @Schema(implementation = Party.class)))
    public Response getPartiesByType(@PathParam("partyTypeId") String partyTypeId) {
        List<Party> parties = partyService.getPartiesByType(partyTypeId);
        return Response.ok(parties).build();
    }

    /**
     * 获取客户列表
     */
    @GET
    @Path("/customers")
    @Operation(summary = "获取客户列表", description = "获取所有客户类型的 Party")
    @APIResponse(responseCode = "200", description = "成功获取客户列表",
            content = @Content(schema = @Schema(implementation = Party.class)))
    public Response getCustomers() {
        List<Party> customers = partyService.getCustomers();
        return Response.ok(customers).build();
    }

    /**
     * 获取供应商列表
     */
    @GET
    @Path("/suppliers")
    @Operation(summary = "获取供应商列表", description = "获取所有供应商类型的 Party")
    @APIResponse(responseCode = "200", description = "成功获取供应商列表",
            content = @Content(schema = @Schema(implementation = Party.class)))
    public Response getSuppliers() {
        List<Party> suppliers = partyService.getSuppliers();
        return Response.ok(suppliers).build();
    }

    /**
     * 获取 Party 的联系方式
     */
    @GET
    @Path("/{partyId}/contacts")
    @Operation(summary = "获取 Party 联系方式", description = "获取指定 Party 的所有联系方式")
    @APIResponse(responseCode = "200", description = "成功获取联系方式列表",
            content = @Content(schema = @Schema(implementation = PartyContactMech.class)))
    public Response getPartyContactMechanisms(@PathParam("partyId") String partyId) {
        List<PartyContactMech> contacts = partyService.getPartyContactMechanisms(partyId);
        return Response.ok(contacts).build();
    }

    /**
     * 添加联系方式到 Party
     */
    @POST
    @Path("/{partyId}/contacts")
    @Operation(summary = "添加联系方式", description = "为指定 Party 添加新的联系方式")
    @APIResponses({
            @APIResponse(responseCode = "201", description = "联系方式添加成功",
                    content = @Content(schema = @Schema(implementation = PartyContactMech.class))),
            @APIResponse(responseCode = "400", description = "请求数据无效")
    })
    public Response addContactMechanism(@PathParam("partyId") String partyId, @Valid PartyContactMech partyContactMech) {
        partyContactMech.partyId = partyId;
        PartyContactMech createdContact = partyService.addContactMechanism(partyContactMech);
        return Response.status(Response.Status.CREATED).entity(createdContact).build();
    }

    /**
     * 移除 Party 的联系方式
     */
    @DELETE
    @Path("/{partyId}/contacts/{contactMechId}")
    @Operation(summary = "移除联系方式", description = "移除指定 Party 的联系方式")
    @APIResponses({
            @APIResponse(responseCode = "204", description = "联系方式移除成功"),
            @APIResponse(responseCode = "404", description = "联系方式不存在")
    })
    public Response removeContactMechanism(@PathParam("partyId") String partyId, @PathParam("contactMechId") String contactMechId) {
        boolean removed = partyService.removeContactMechanism(partyId, contactMechId);
        if (removed) {
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"联系方式不存在\"}")
                    .build();
        }
    }

    /**
     * RSQL 查询 Party
     */
    @GET
    @Path("/query")
    @Operation(summary = "RSQL 查询 Party", description = "使用 RSQL 语法进行灵活查询")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "查询成功",
                    content = @Content(schema = @Schema(implementation = Party.class))),
            @APIResponse(responseCode = "400", description = "RSQL 查询语法错误")
    })
    public Response queryParties(@QueryParam("q") String rsqlQuery,
                                @QueryParam("page") @DefaultValue("0") int page,
                                @QueryParam("size") @DefaultValue("20") int size,
                                @QueryParam("count") @DefaultValue("false") boolean returnCount) {
        try {
            if (rsqlQuery == null || rsqlQuery.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"RSQL 查询参数 q 不能为空\"}")
                        .build();
            }

            // 验证 RSQL 查询
            if (!partyService.isValidRsqlQuery(rsqlQuery)) {
                String error = partyService.getRsqlParseError(rsqlQuery);
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"无效的 RSQL 查询: " + error + "\"}")
                        .build();
            }

            List<Party> parties;
            if (page >= 0 && size > 0) {
                parties = partyService.queryPartiesWithPagination(rsqlQuery, page, size);
            } else {
                parties = partyService.queryParties(rsqlQuery);
            }

            if (returnCount) {
                long totalCount = partyService.queryPartiesCount(rsqlQuery);
                java.util.Map<String, Object> result = new java.util.HashMap<>();
                result.put("data", parties);
                result.put("total", totalCount);
                result.put("page", page);
                result.put("size", size);
                return Response.ok(result).build();
            } else {
                return Response.ok(parties).build();
            }
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
}
