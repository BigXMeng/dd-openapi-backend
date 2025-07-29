package com.dd.openapi.main.web.model.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 用户调用接口关系DTO
 * </p>
 *
 * @author liuxianmeng
 * @since 2025-07-28
 */
@Getter
@Setter
@Builder
@TableName("user_interface_info")
public class UserInterfaceInfoDTO implements Serializable {

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 描述
     */
    @TableField(value = "description")
    private String description;

    /**
     * 接口地址
     */
    @TableField(value = "url")
    private String url;

    /**
     * 请求参数
     */
    @TableField(value = "requestParams")
    private String requestParams;

    /**
     * 请求头
     */
    @TableField(value = "requestHeader")
    private String requestHeader;

    /**
     * 响应头
     */
    @TableField(value = "responseHeader")
    private String responseHeader;

    /**
     * 接口状态（0-关闭，1-开启）
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 请求类型
     */
    @TableField(value = "method")
    private String method;

    /**
     * 创建人
     */
    @TableField(value = "userAccount")
    private String userAccount;

    /**
     * 创建时间
     */
    @TableField(value = "createTime")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "updateTime")
    private Date updateTime;

    /**
     * 是否删除(0-未删, 1-已删)
     */
    @TableField(value = "isDelete")
    private Integer isDelete;

    /**
     * 总调用次数
     */
    @TableField(value = "totalNum")
    private Integer totalNum;

    /**
     * 剩余调用次数
     */
    @TableField(value = "leftNum")
    private Integer leftNum;
}
