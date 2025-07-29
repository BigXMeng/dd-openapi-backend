package com.dd.openapi.main.web.service.internal;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dd.openapi.main.web.common.vo.InterfaceInfoVO;
import com.dd.openapi.main.web.model.DO.InterfaceInfoDO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dd.openapi.main.web.model.req.interfaceinfo.InterfaceInfoAddReq;
import com.dd.openapi.main.web.model.req.interfaceinfo.InterfaceInfoDeleteReq;
import com.dd.openapi.main.web.model.req.interfaceinfo.InterfaceInfoQueryReq;
import com.dd.openapi.main.web.model.req.interfaceinfo.InterfaceInfoUpdateReq;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/7/21 18:34
 * @Description 针对表【interface_info(接口信息)】的数据库操作Service
 */
public interface InterfaceInfoService extends IService<InterfaceInfoDO> {

    void addOne(InterfaceInfoAddReq req, String token);

    void updateOne(InterfaceInfoUpdateReq req);

    int generateInterfaceInfoData(int count, String userIdRange);

    void delete(InterfaceInfoDeleteReq req);

    /**
     * 使用非关联查询
     *
     * @param req
     * @return
     */
    IPage<InterfaceInfoVO> pageUseLambdaQueryWrapper(InterfaceInfoQueryReq req);

    /**
     * 使用关联查询
     *
     * @param req
     * @return
     */
    IPage<InterfaceInfoVO> pageUseCorrelatedQuery(InterfaceInfoQueryReq req);

    InterfaceInfoVO get(Long id);
}
