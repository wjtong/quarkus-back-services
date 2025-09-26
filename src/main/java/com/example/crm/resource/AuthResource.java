package com.example.crm.resource;

import com.example.crm.dto.ChangePasswordRequest;
import com.example.crm.dto.LoginRequest;
import com.example.crm.dto.RegisterRequest;
import com.example.crm.entity.User;
import com.example.crm.service.AuthService;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;
import java.util.Map;

/**
 * 认证 REST API 资源类
 * 提供用户认证、注册、密码管理等功能
 */
@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "认证管理", description = "用户认证、注册、密码管理等功能")
public class AuthResource {

    @Inject
    AuthService authService;

    @Inject
    JsonWebToken jwt;

    /**
     * 用户登录
     */
    @POST
    @Path("/login")
    @Operation(summary = "用户登录", description = "使用用户名和密码进行登录，返回 JWT Token")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "登录成功",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @APIResponse(responseCode = "401", description = "用户名或密码错误"),
            @APIResponse(responseCode = "423", description = "账户被锁定")
    })
    public Response login(@Valid LoginRequest loginRequest) {
        try {
            Map<String, Object> result = authService.login(loginRequest.username, loginRequest.password);
            return Response.ok(result).build();
        } catch (SecurityException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    /**
     * 用户注册
     */
    @POST
    @Path("/register")
    @Operation(summary = "用户注册", description = "注册新用户账户")
    @APIResponses({
            @APIResponse(responseCode = "201", description = "注册成功",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @APIResponse(responseCode = "400", description = "注册数据无效或用户名/邮箱已存在")
    })
    public Response register(@Valid RegisterRequest registerRequest) {
        try {
            User user = new User();
            user.username = registerRequest.username;
            user.password = registerRequest.password;
            user.email = registerRequest.email;
            user.fullName = registerRequest.fullName;

            User createdUser = authService.register(user);
            
            // 不返回密码
            createdUser.password = null;
            
            return Response.status(Response.Status.CREATED).entity(createdUser).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    /**
     * 获取当前用户信息
     */
    @GET
    @Path("/me")
    @Authenticated
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
    @APIResponse(responseCode = "200", description = "成功获取用户信息",
            content = @Content(schema = @Schema(implementation = User.class)))
    public Response getCurrentUser(@Context SecurityContext securityContext) {
        String username = securityContext.getUserPrincipal().getName();
        return authService.getUserByUsername(username)
                .map(user -> {
                    user.password = null; // 不返回密码
                    return Response.ok(user).build();
                })
                .orElse(Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("error", "用户不存在"))
                        .build());
    }

    /**
     * 修改密码
     */
    @PUT
    @Path("/change-password")
    @Authenticated
    @Operation(summary = "修改密码", description = "修改当前用户的密码")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "密码修改成功"),
            @APIResponse(responseCode = "400", description = "原密码错误或新密码格式不正确")
    })
    public Response changePassword(@Valid ChangePasswordRequest changePasswordRequest, 
                                  @Context SecurityContext securityContext) {
        try {
            String username = securityContext.getUserPrincipal().getName();
            User user = authService.getUserByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
            
            authService.changePassword(user.id, changePasswordRequest.oldPassword, changePasswordRequest.newPassword);
            
            return Response.ok(Map.of("message", "密码修改成功")).build();
        } catch (IllegalArgumentException | SecurityException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    /**
     * 重置密码请求
     */
    @POST
    @Path("/reset-password")
    @Operation(summary = "请求重置密码", description = "通过邮箱请求重置密码")
    @APIResponse(responseCode = "200", description = "重置密码邮件已发送")
    public Response resetPassword(@QueryParam("email") String email) {
        try {
            authService.resetPassword(email);
            return Response.ok(Map.of("message", "重置密码邮件已发送到您的邮箱")).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    /**
     * 使用重置令牌设置新密码
     */
    @POST
    @Path("/set-password")
    @Operation(summary = "设置新密码", description = "使用重置令牌设置新密码")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "密码设置成功"),
            @APIResponse(responseCode = "400", description = "重置令牌无效或已过期")
    })
    public Response setNewPassword(@QueryParam("token") String token, 
                                  @QueryParam("password") String newPassword) {
        try {
            authService.setNewPasswordWithToken(token, newPassword);
            return Response.ok(Map.of("message", "密码设置成功")).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    /**
     * 获取所有用户（管理员功能）
     */
    @GET
    @Path("/users")
    @RolesAllowed("ADMIN")
    @Operation(summary = "获取所有用户", description = "获取系统中所有用户列表（仅管理员）")
    @APIResponse(responseCode = "200", description = "成功获取用户列表",
            content = @Content(schema = @Schema(implementation = User.class)))
    public Response getAllUsers() {
        List<User> users = authService.getAllUsers();
        // 移除密码字段
        users.forEach(user -> user.password = null);
        return Response.ok(users).build();
    }

    /**
     * 根据ID获取用户（管理员功能）
     */
    @GET
    @Path("/users/{id}")
    @RolesAllowed("ADMIN")
    @Operation(summary = "根据ID获取用户", description = "根据用户ID获取用户信息（仅管理员）")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "成功获取用户信息",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @APIResponse(responseCode = "404", description = "用户不存在")
    })
    public Response getUserById(@PathParam("id") Long id) {
        return authService.getUserById(id)
                .map(user -> {
                    user.password = null; // 不返回密码
                    return Response.ok(user).build();
                })
                .orElse(Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("error", "用户不存在"))
                        .build());
    }

    /**
     * 更新用户信息（管理员功能）
     */
    @PUT
    @Path("/users/{id}")
    @RolesAllowed("ADMIN")
    @Operation(summary = "更新用户信息", description = "更新指定用户的信息（仅管理员）")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "用户信息更新成功",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @APIResponse(responseCode = "400", description = "请求数据无效"),
            @APIResponse(responseCode = "404", description = "用户不存在")
    })
    public Response updateUser(@PathParam("id") Long id, @Valid User userData) {
        try {
            User updatedUser = authService.updateUser(id, userData);
            updatedUser.password = null; // 不返回密码
            return Response.ok(updatedUser).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    /**
     * 删除用户（管理员功能）
     */
    @DELETE
    @Path("/users/{id}")
    @RolesAllowed("ADMIN")
    @Operation(summary = "删除用户", description = "删除指定用户（仅管理员）")
    @APIResponses({
            @APIResponse(responseCode = "204", description = "用户删除成功"),
            @APIResponse(responseCode = "404", description = "用户不存在")
    })
    public Response deleteUser(@PathParam("id") Long id) {
        boolean deleted = authService.deleteUser(id);
        if (deleted) {
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "用户不存在"))
                    .build();
        }
    }

    /**
     * 切换用户状态（管理员功能）
     */
    @PUT
    @Path("/users/{id}/toggle-status")
    @RolesAllowed("ADMIN")
    @Operation(summary = "切换用户状态", description = "激活或停用用户账户（仅管理员）")
    @APIResponse(responseCode = "200", description = "用户状态更新成功")
    public Response toggleUserStatus(@PathParam("id") Long id) {
        try {
            authService.toggleUserStatus(id);
            return Response.ok(Map.of("message", "用户状态更新成功")).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    /**
     * 解锁用户（管理员功能）
     */
    @PUT
    @Path("/users/{id}/unlock")
    @RolesAllowed("ADMIN")
    @Operation(summary = "解锁用户", description = "解锁被锁定的用户账户（仅管理员）")
    @APIResponse(responseCode = "200", description = "用户解锁成功")
    public Response unlockUser(@PathParam("id") Long id) {
        try {
            authService.unlockUser(id);
            return Response.ok(Map.of("message", "用户解锁成功")).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }
}
