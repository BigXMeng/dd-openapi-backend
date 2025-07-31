package com.dd.openapi.apiserver.common;

import com.dd.openapi.common.enumeration.CodedEnum;
import com.dd.openapi.common.enumeration.DescribableEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/7/31 0:33
 * @Description 接口字典
 */
@Getter
@AllArgsConstructor
public enum InterfaceDictionary implements CodedEnum<Long>, DescribableEnum {

    BATCH_GENE_UUID(101L, "批量生成UUID"),
    GET_LOCAL_IP_INFO(102L, "获取本地IP信息"),
    GET_A_LUCKY_STR(103L, "获取一个随机字符串"),
    ;

    private final Long interfaceId;
    private final String description;


    @Override
    public Long getCode() {
        return getInterfaceId();
    }

    @Override
    public String getDescription() {
        return description;
    }
}
