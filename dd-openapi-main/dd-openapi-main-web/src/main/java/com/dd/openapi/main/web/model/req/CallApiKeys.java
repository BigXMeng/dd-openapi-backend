package com.dd.openapi.main.web.model.req;

import com.dd.openapi.common.annotation.MetaInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/7/26 10:49
 * @Description 调用API需要的访问密钥和秘密密钥 使用用户自己的Key
 */
@Data
@Deprecated // 这两个参数放在请求头进行传递
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "调用API需要的访问密钥和秘密密钥 使用用户自己的Key")
public class CallApiKeys {

    @Schema(
            description = "【访问密钥】公开标识用户或应用(类似用户名) 用于标识请求来源",
            example = "AKIAIOSFODNN7EXAMPLE"
    )
    private String accessKey;

    @Schema(
            description = "【秘密密钥】类似密码 用于签名请求 确保请求未被篡改 必须严格保密！",
            example = "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY\n"
    )
    private String secretKey;
}
