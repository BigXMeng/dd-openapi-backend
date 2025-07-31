package com.dd.openapi.main.web.service.external;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dd.openapi.main.web.common.api.InterfaceInfoOutsideService;
import com.dd.openapi.main.web.common.vo.InterfaceInfoVO;
import com.dd.openapi.main.web.converter.InterfaceInfoConverter;
import com.dd.openapi.main.web.mapper.InterfaceInfoMapper;
import com.dd.openapi.main.web.model.DO.InterfaceInfoDO;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/7/29 13:35
 * @Description 类功能作用说明
 */
@DubboService
@RequiredArgsConstructor
public class InterfaceInfoOutsideServiceImpl implements InterfaceInfoOutsideService {

    private final InterfaceInfoMapper interfaceInfoMapper;

    @Override
    public InterfaceInfoVO getInterfaceInfo(String path, String method) {
        LambdaQueryWrapper<InterfaceInfoDO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(InterfaceInfoDO::getUrl, path);
        lqw.eq(InterfaceInfoDO::getMethod, method);
        InterfaceInfoDO interfaceInfoDO = interfaceInfoMapper.selectOne(lqw);
        return InterfaceInfoConverter.DO2VO(interfaceInfoDO);
    }
}
