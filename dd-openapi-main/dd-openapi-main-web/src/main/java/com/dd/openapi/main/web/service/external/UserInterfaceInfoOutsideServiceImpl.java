package com.dd.openapi.main.web.service.external;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.dd.openapi.common.exception.DomainException;
import com.dd.openapi.common.response.ApiResponseEnum;
import com.dd.openapi.main.web.common.api.UserInterfaceInfoOutsideService;
import com.dd.openapi.main.web.mapper.UserInterfaceInfoMapper;
import com.dd.openapi.main.web.model.DO.UserInterfaceInfoDO;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/7/29 13:35
 * @Description 类功能作用说明
 */
@Service
@RequiredArgsConstructor
@DubboService(interfaceClass = UserInterfaceInfoOutsideService.class, group = "DUBBO_DD_OPENAPI", version = "1.0")
public class UserInterfaceInfoOutsideServiceImpl implements UserInterfaceInfoOutsideService {

    private final UserInterfaceInfoMapper userInterfaceInfoMapper;

    @Override
    public void invokeCountChange(Long interfaceInfoId, String userAccount) throws DomainException {
        // 1 查询用户是否满足调用条件
        LambdaQueryWrapper<UserInterfaceInfoDO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserInterfaceInfoDO::getInterfaceInfoId, interfaceInfoId);
        lqw.eq(UserInterfaceInfoDO::getUserAccount, userAccount);
        UserInterfaceInfoDO userInterfaceInfoDO = userInterfaceInfoMapper.selectOne(lqw);
        if (userInterfaceInfoDO == null) {
            throw new DomainException(
                    ApiResponseEnum.FORBIDDEN_INVOCATION_PERMISSION_HAS_NOT_BEEN_ENABLED.getCode(),
                    ApiResponseEnum.FORBIDDEN_INVOCATION_PERMISSION_HAS_NOT_BEEN_ENABLED.getMessage()
            );
        }
        if (userInterfaceInfoDO.getLeftNum() == 0) {
            throw new DomainException(
                    ApiResponseEnum.FORBIDDEN_QUOTA_FOR_INTERFACE_CALLS_HAS_BEEN_EXHAUSTED.getCode(),
                    ApiResponseEnum.FORBIDDEN_QUOTA_FOR_INTERFACE_CALLS_HAS_BEEN_EXHAUSTED.getMessage()
            );
        }

        // 2 执行调用信息统计
        LambdaUpdateWrapper<UserInterfaceInfoDO> luw = new LambdaUpdateWrapper<>();
        luw.eq(UserInterfaceInfoDO::getInterfaceInfoId, interfaceInfoId);
        luw.eq(UserInterfaceInfoDO::getUserAccount, userAccount);
        luw.setSql("totalNum  = totalNum + 1, leftNum = leftNum - 1");
        userInterfaceInfoMapper.update(null, luw);
    }
}
