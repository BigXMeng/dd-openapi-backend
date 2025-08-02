package com.dd.openapi.main.web.model.dto;

import com.dd.openapi.common.annotation.MetaInfo;
import lombok.*;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/7/31 23:19
 * @Description 接口调用top3信息DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@MetaInfo(value = "接口调用top3信息DTO")
public class InterfaceInvokeTop3InfoDTO {
    @MetaInfo(value = "接口名")
    private String interfaceName;
    @MetaInfo(value = "接口名id标识")
    private Long interfaceInfoId;
    @MetaInfo(value = "接口被调用总数")
    private Integer invokedTotalNum;
}
