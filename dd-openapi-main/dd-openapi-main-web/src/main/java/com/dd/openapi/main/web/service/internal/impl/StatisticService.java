package com.dd.openapi.main.web.service.internal.impl;

import com.dd.openapi.main.web.converter.InterfaceInfoConverter;
import com.dd.openapi.main.web.model.dto.InterfaceInvokeTop3InfoDTO;
import com.dd.openapi.main.web.model.vo.InterfaceInvokeTop3InfoVO;
import com.dd.openapi.main.web.repository.InterfaceInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/7/31 23:16
 * @Description 接口调用信息统计
 */
@Service
@RequiredArgsConstructor
public class StatisticService {

    private final InterfaceInfoRepository interfaceInfoRepository;

    public List<InterfaceInvokeTop3InfoVO> interfaceInvokeTop3InfoVO() {
        List<InterfaceInvokeTop3InfoDTO> interfaceInvokeTop3InfoDTO = interfaceInfoRepository.interfaceInvokeTop3InfoDTO();
        return InterfaceInfoConverter.dtos2VOs(interfaceInvokeTop3InfoDTO);
    }
}
