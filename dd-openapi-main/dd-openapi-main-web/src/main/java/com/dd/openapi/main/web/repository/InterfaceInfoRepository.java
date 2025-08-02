package com.dd.openapi.main.web.repository;

import com.dd.openapi.main.web.mapper.UserInterfaceInfoMapper;
import com.dd.openapi.main.web.model.dto.InterfaceInvokeTop3InfoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/7/31 23:17
 * @Description 类功能作用说明
 */
@Repository
@RequiredArgsConstructor
public class InterfaceInfoRepository {

    private final UserInterfaceInfoMapper userInterfaceInfoMapper;

    public List<InterfaceInvokeTop3InfoDTO> interfaceInvokeTop3InfoDTO() {
        return userInterfaceInfoMapper.interfaceInvokeTop3InfoDTO();
    }
}
