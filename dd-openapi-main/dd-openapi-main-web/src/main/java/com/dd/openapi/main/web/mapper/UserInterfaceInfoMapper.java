package com.dd.openapi.main.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dd.openapi.main.web.model.DO.UserInterfaceInfoDO;
import com.dd.openapi.main.web.model.dto.UserInterfaceInfoDTO;
import org.apache.ibatis.annotations.Param;

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

    IPage<UserInterfaceInfoDTO> pageUserInterfaceInfoDTO(Page<UserInterfaceInfoDTO> page, @Param("params") Map<String, Object> params);
}
