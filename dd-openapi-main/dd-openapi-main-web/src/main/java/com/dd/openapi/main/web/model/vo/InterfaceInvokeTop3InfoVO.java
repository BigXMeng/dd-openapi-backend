package com.dd.openapi.main.web.model.vo;

import com.dd.openapi.common.annotation.MetaInfo;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "接口调用top3信息DTO")
public class InterfaceInvokeTop3InfoVO {
    @Schema(description = "接口名")
    private String interfaceName;
    @Schema(description = "接口名id标识")
    private Long interfaceInfoId;
    @Schema(description = "接口被调用总数")
    private Integer invokedTotalNum;
}
