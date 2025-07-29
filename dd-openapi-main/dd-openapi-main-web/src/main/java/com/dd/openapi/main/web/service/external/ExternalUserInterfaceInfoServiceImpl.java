package com.dd.openapi.main.web.service.external;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.dd.openapi.main.web.common.api.ExternalUserInterfaceInfoService;
import com.dd.openapi.main.web.config.exception.DomainException;
import com.dd.openapi.main.web.mapper.UserInterfaceInfoMapper;
import com.dd.openapi.main.web.model.DO.UserInterfaceInfoDO;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/7/29 13:35
 * @Description 类功能作用说明
 */
@DubboService
@RequiredArgsConstructor
public class ExternalUserInterfaceInfoServiceImpl implements ExternalUserInterfaceInfoService {

    private UserInterfaceInfoMapper userInterfaceInfoMapper;

    @Override
    public void invokeCount(Long interfaceInfoId, String userAccount) {
        // 1 查询用户是否满足调用条件
        LambdaQueryWrapper<UserInterfaceInfoDO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserInterfaceInfoDO::getInterfaceInfoId, interfaceInfoId);
        lqw.eq(UserInterfaceInfoDO::getUserAccount, userAccount);
        UserInterfaceInfoDO userInterfaceInfoDO = userInterfaceInfoMapper.selectOne(lqw);
        if (userInterfaceInfoDO == null) {
            throw new DomainException(401, "当前用户暂未申请此接口调用额度，请申请调用额度。");
        }
        if (userInterfaceInfoDO.getLeftNum() == 0) {
            throw new DomainException(401, "当前用户此接口调用额度已用完，请再次申请调用额度。");
        }

        // 2 执行调用信息统计
        LambdaUpdateWrapper<UserInterfaceInfoDO> luw = new LambdaUpdateWrapper<>();
        luw.eq(UserInterfaceInfoDO::getInterfaceInfoId, interfaceInfoId);
        luw.eq(UserInterfaceInfoDO::getUserAccount, userAccount);
        luw.setSql("totalNum  = totalNum + 1, leftNum = leftNum - 1");
        userInterfaceInfoMapper.update(null, luw);
    }
}
