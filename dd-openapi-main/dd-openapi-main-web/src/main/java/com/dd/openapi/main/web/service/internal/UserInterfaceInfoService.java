package com.dd.openapi.main.web.service.internal;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dd.openapi.main.web.model.DO.UserInterfaceInfoDO;
import com.dd.openapi.main.web.model.req.userinterface.EnableInvokeInterfaceReq;

/**
 * <p>
 * 用户调用接口关系 服务类
 * </p>
 *
 * @author liuxianmeng
 * @since 2025-07-28
 */
public interface UserInterfaceInfoService extends IService<UserInterfaceInfoDO> {

    /**
     * 用户开通API调用次数
     *
     * @param req
     */
    void enableInvoke(EnableInvokeInterfaceReq req);
}
