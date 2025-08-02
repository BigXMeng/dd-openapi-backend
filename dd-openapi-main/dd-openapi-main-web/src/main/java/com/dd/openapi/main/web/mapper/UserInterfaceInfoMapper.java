package com.dd.openapi.main.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dd.openapi.main.web.model.DO.UserInterfaceInfoDO;
import com.dd.openapi.main.web.model.dto.InterfaceInvokeTop3InfoDTO;
import com.dd.openapi.main.web.model.dto.UserInterfaceInfoDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户调用接口关系 Mapper 接口
 * </p>
 *
 * @author liuxianmeng
 * @since 2025-07-28
 */
public interface UserInterfaceInfoMapper extends BaseMapper<UserInterfaceInfoDO> {

    /**
     * 获取用户侧接口分页列表
     * @param page
     * @param params
     * @return
     */
    IPage<UserInterfaceInfoDTO> pageUserInterfaceInfoDTO(Page<UserInterfaceInfoDTO> page, @Param("params") Map<String, Object> params);

    /**
     * 获取接口调用top3统计信息
     * @return
     */
    List<InterfaceInvokeTop3InfoDTO> interfaceInvokeTop3InfoDTO();
}
