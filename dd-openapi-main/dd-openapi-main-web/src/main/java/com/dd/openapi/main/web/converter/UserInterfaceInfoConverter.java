package com.dd.openapi.main.web.converter;

import com.dd.openapi.main.web.common.vo.InterfaceInfoVO;
import com.dd.openapi.main.web.common.vo.UserInterfaceInvokeInfoVO;
import com.dd.openapi.main.web.model.dto.UserInterfaceInfoDTO;
import org.springframework.beans.BeanUtils;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/7/21 19:09
 * @Description 类功能作用说明
 */
public class UserInterfaceInfoConverter {

    public static InterfaceInfoVO DO2VO(UserInterfaceInfoDTO dto) {
        if(dto == null) return null;

        InterfaceInfoVO VO = new InterfaceInfoVO();
        BeanUtils.copyProperties(dto, VO);
        VO.setUserInterfaceInvokeInfoVO(
                UserInterfaceInvokeInfoVO.builder()
                        .invokedNum(dto.getTotalNum() != null ? dto.getTotalNum() : 0)
                        .invokeLeftNum(dto.getLeftNum() != null ? dto.getLeftNum() : 0)
                        .build()
        );
        return VO;
    }
}
