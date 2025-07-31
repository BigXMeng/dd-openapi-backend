package com.dd.openapi.main.web.service.internal.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dd.ms.auth.vo.UserVO;
import com.dd.openapi.main.web.mapper.UserInterfaceInfoMapper;
import com.dd.openapi.main.web.model.DO.UserInterfaceInfoDO;
import com.dd.openapi.main.web.model.req.userinterface.EnableInvokeInterfaceReq;
import com.dd.openapi.main.web.service.internal.UserInterfaceInfoService;
import com.dd.openapi.main.web.util.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户调用接口关系 服务实现类
 * </p>
 *
 * @author liuxianmeng
 * @since 2025-07-28
 */
@Service
@RequiredArgsConstructor
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfoDO> implements UserInterfaceInfoService {

    private final UserInterfaceInfoMapper userInterfaceInfoMapper;
    private final AuthUtils authUtils;

    @Override
    public void enableInvoke(EnableInvokeInterfaceReq req) {
        UserVO currUser = authUtils.getCurrUser(false);
        // 1 先查询是否存在 存在则更新 不存在则插入
        LambdaQueryWrapper<UserInterfaceInfoDO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserInterfaceInfoDO::getInterfaceInfoId, req.getInterfaceId());
        lqw.eq(UserInterfaceInfoDO::getUserAccount, currUser.getAccount());
        UserInterfaceInfoDO userInterfaceInfoDO = userInterfaceInfoMapper.selectOne(lqw);
        // 2 不存在 则插入新行数据
        if (userInterfaceInfoDO == null) {
            userInterfaceInfoMapper.insert(
                    UserInterfaceInfoDO.builder()
                            .interfaceInfoId(req.getInterfaceId())
                            .userAccount(currUser.getAccount())
                            .leftNum(req.getInvokeNum())
                            .build()
            );
        } else {
            // 3 存在 则更新数据
            int addNum = req.getInvokeNum();
            LambdaUpdateWrapper<UserInterfaceInfoDO> luw = new LambdaUpdateWrapper<>();
            luw.eq(UserInterfaceInfoDO::getInterfaceInfoId, req.getInterfaceId());
            luw.eq(UserInterfaceInfoDO::getUserAccount, currUser.getAccount());
            // 创建一个空的 UserInterfaceInfoDO 对象作为更新的实体
            UserInterfaceInfoDO updateEntity = new UserInterfaceInfoDO();
            updateEntity.setLeftNum(userInterfaceInfoDO.getLeftNum() + addNum);
            userInterfaceInfoMapper.update(updateEntity, luw);
        }
    }
}
