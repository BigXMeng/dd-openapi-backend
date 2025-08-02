package com.dd.openapi.main.web.converter;

import com.dd.openapi.main.web.common.vo.InterfaceInfoVO;
import com.dd.openapi.main.web.model.DO.InterfaceInfoDO;
import com.dd.openapi.main.web.model.dto.InterfaceInvokeTop3InfoDTO;
import com.dd.openapi.main.web.model.req.interfaceinfo.InterfaceInfoAddReq;
import com.dd.openapi.main.web.model.req.interfaceinfo.InterfaceInfoUpdateReq;
import com.dd.openapi.main.web.model.vo.InterfaceInvokeTop3InfoVO;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/7/21 19:09
 * @Description 类功能作用说明
 */
public class InterfaceInfoConverter {

    public static InterfaceInfoDO req2DO(InterfaceInfoAddReq req) {
        if(req == null) return null;

        InterfaceInfoDO DO = new InterfaceInfoDO();
        BeanUtils.copyProperties(req, DO);
        return DO;
    }

    public static InterfaceInfoVO DO2VO(InterfaceInfoDO interfaceInfoDO) {
        if(interfaceInfoDO == null) return null;

        InterfaceInfoVO VO = new InterfaceInfoVO();
        BeanUtils.copyProperties(interfaceInfoDO, VO);
        return VO;
    }

    public static InterfaceInfoDO updateReq2DO(InterfaceInfoUpdateReq req) {
        if(req == null) return null;

        InterfaceInfoDO DO = new InterfaceInfoDO();
        BeanUtils.copyProperties(req, DO);
        return DO;
    }

    public static InterfaceInvokeTop3InfoVO dto2VO(InterfaceInvokeTop3InfoDTO dto) {
        if(dto == null) return null;

        InterfaceInvokeTop3InfoVO VO = new InterfaceInvokeTop3InfoVO();
        BeanUtils.copyProperties(dto, VO);
        return VO;
    }

    public static List<InterfaceInvokeTop3InfoVO> dtos2VOs(List<InterfaceInvokeTop3InfoDTO> dtos) {
        if(dtos == null) return null;

        List<InterfaceInvokeTop3InfoVO> VOs = new ArrayList<>();
        dtos.forEach(dto -> VOs.add(dto2VO(dto)));
        return VOs;
    }
}
