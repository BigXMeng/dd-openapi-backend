package com.dd.openapi.main.web.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dd.ms.auth.api.AuthServiceOutside;
import com.dd.ms.auth.vo.UserInfoVO;
import com.dd.openapi.main.web.config.exception.DomainException;
import com.dd.openapi.main.web.converter.InterfaceInfoConverter;
import com.dd.openapi.main.web.converter.InterfaceInfoQueryBuilder;
import com.dd.openapi.main.web.mapper.InterfaceInfoMapper;
import com.dd.openapi.main.web.model.DO.InterfaceInfoDO;
import com.dd.openapi.main.web.model.req.interfaceinfo.InterfaceInfoAddReq;
import com.dd.openapi.main.web.model.req.interfaceinfo.InterfaceInfoDeleteReq;
import com.dd.openapi.main.web.model.req.interfaceinfo.InterfaceInfoQueryReq;
import com.dd.openapi.main.web.model.req.interfaceinfo.InterfaceInfoUpdateReq;
import com.dd.openapi.main.web.model.vo.InterfaceInfoVO;
import com.dd.openapi.main.web.service.InterfaceInfoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
* @author liuxianmeng
* @description 针对表【interface_info(接口信息)】的数据库操作Service实现
* @createDate 2025-07-21 18:26:18
*/
@Service
@RequiredArgsConstructor
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfoDO> implements InterfaceInfoService {

    @DubboReference(interfaceClass = AuthServiceOutside.class, group = "DUBBO_DD_MS_AUTH", version = "1.0")
    private AuthServiceOutside authServiceOutside;

    @Override
    public void addOne(InterfaceInfoAddReq req, String token) throws DomainException {
        UserInfoVO userInfoVO = authServiceOutside.getUserInfoByToken(token);
        InterfaceInfoDO interfaceInfoDO = InterfaceInfoConverter.req2DO(req);
        interfaceInfoDO.setUserAccount(userInfoVO.getAccount());
        baseMapper.insert(interfaceInfoDO);
    }

    @Override
    public void delete(InterfaceInfoDeleteReq req) {
        this.removeBatchByIds(req.getIds());
    }

    @Override
    public void updateOne(InterfaceInfoUpdateReq req) {
        if(req.getId() == null){
            throw new DomainException(400, "id不可为空");
        }
        // 初步得到要插入的dataObjectDO对象
        InterfaceInfoDO DO = InterfaceInfoConverter.updateReq2DO(req);
        // 执行更新并返回结果
        this.updateById(DO);
    }

    @Override
    public IPage<InterfaceInfoVO> pageQuery(InterfaceInfoQueryReq req) {
        Page<InterfaceInfoDO> page = new Page<>(req.getPageParams().getPageNum(), req.getPageParams().getPageSize());
        Page<InterfaceInfoDO> interfaceInfoDOPage = this.page(page, InterfaceInfoQueryBuilder.buildLQW(req.getQueryParams()));
        List<InterfaceInfoVO> interfaceInfoVOList = interfaceInfoDOPage.getRecords().stream()
                .map(InterfaceInfoConverter::DO2VO).collect(Collectors.toList());
        Page<InterfaceInfoVO> interfaceInfoVOPage = new Page<>();
        BeanUtils.copyProperties(interfaceInfoDOPage, interfaceInfoVOPage);
        return interfaceInfoVOPage.setRecords(interfaceInfoVOList);
    }

    private static final Random RANDOM = new Random();
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public int generateInterfaceInfoData(int count, String userIdRange) {
        if (count <= 0 || count > 1000) {
            throw new IllegalArgumentException("生成数量必须在1-1000之间");
        }

        List<InterfaceInfoDO> dataList = new ArrayList<>();
        int minUserId = 1;
        int maxUserId = 500;

        // 解析用户ID范围
        if (StringUtils.isNotBlank(userIdRange)) {
            String[] range = userIdRange.split("-");
            if (range.length == 2) {
                minUserId = Integer.parseInt(range[0]);
                maxUserId = Integer.parseInt(range[1]);
            }
        }

        for (int i = 0; i < count; i++) {
            dataList.add(generateInterfaceInfo(minUserId, maxUserId));
        }

        // 使用MyBatis-Plus批量插入
        this.saveBatch(dataList);
        return dataList.size();
    }

    private InterfaceInfoDO generateInterfaceInfo(int minUserId, int maxUserId) {
        InterfaceInfoDO info = new InterfaceInfoDO();

        String name = generateApiName();
        info.setName(name);
        info.setDescription(generateDescription(name));
        info.setUrl(generateUrl(name));
        info.setRequestParams(generateRequestParams(name));
        info.setRequestHeader(generateRequestHeader());
        info.setResponseHeader(generateResponseHeader());
        info.setStatus(RANDOM.nextDouble() > 0.2 ? 1 : 0); // 80%开启
        info.setMethod(generateHttpMethod());
        info.setUserAccount((long) (RANDOM.nextInt(maxUserId - minUserId + 1) + minUserId) + "");
        info.setIsDelete(RANDOM.nextDouble() > 0.9 ? 1 : 0); // 10%删除

        // 生成时间戳
        long now = System.currentTimeMillis();
        long createTime = now - (long)(RANDOM.nextDouble() * 365L * 24 * 60 * 60 * 1000); // 1年内
        long updateTime = createTime + (long)(RANDOM.nextDouble() * 30L * 24 * 60 * 60 * 1000); // 30天内

        info.setCreateTime(new Date(createTime));
        info.setUpdateTime(new Date(updateTime));

        return info;
    }

    private String generateApiName() {
        String[] prefixes = {"用户", "订单", "商品", "支付", "库存", "物流", "消息", "权限"};
        String[] suffixes = {"查询", "创建", "更新", "删除", "状态", "列表", "详情", "验证"};
        return prefixes[RANDOM.nextInt(prefixes.length)] +
                suffixes[RANDOM.nextInt(suffixes.length)] + "接口";
    }

    private String generateDescription(String name) {
        String[] verbs = {"管理", "获取", "处理", "控制", "查询", "更新"};
        return "用于" + verbs[RANDOM.nextInt(verbs.length)] + name.replace("接口", "") + "的服务";
    }

    private String generateUrl(String name) {
        String base = "/api/" + name.replace("接口", "").replace(" ", "-").toLowerCase();
        if (name.contains("详情") || name.contains("查询")) {
            return base + "/{id}";
        }
        if (name.contains("列表")) {
            return base + "/list";
        }
        return base;
    }

    private String generateRequestParams(String name) {
        Map<String, Object> params = new LinkedHashMap<>();

        if (name.contains("用户")) {
            if (name.contains("创建") || name.contains("更新")) {
                params.put("username", "string");
                params.put("email", "user@example.com");
                params.put("phone", "13800138000");
                params.put("age", RANDOM.nextInt(50) + 18);
            } else {
                params.put("userId", RANDOM.nextInt(10000));
            }
        } else if (name.contains("订单")) {
            params.put("orderId", "ORD" + (100000 + RANDOM.nextInt(900000)));
            if (name.contains("创建")) {
                params.put("productIds", Arrays.asList(RANDOM.nextInt(1000), RANDOM.nextInt(1000)));
                params.put("amount", RANDOM.nextDouble() * 1000);
            }
        } else if (name.contains("商品")) {
            params.put("productId", RANDOM.nextInt(10000));
            if (name.contains("更新")) {
                params.put("price", RANDOM.nextDouble() * 500);
                params.put("stock", RANDOM.nextInt(1000));
            }
        } else {
            params.put("id", RANDOM.nextInt(10000));
            params.put("timestamp", System.currentTimeMillis());
        }

        // 添加通用参数
        if (RANDOM.nextBoolean()) {
            params.put("page", RANDOM.nextInt(5) + 1);
            params.put("pageSize", 10);
        }

        return toJson(params);
    }

    private String generateRequestHeader() {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", "application/json");

        if (RANDOM.nextBoolean()) {
            headers.put("Authorization", "Bearer " + UUID.randomUUID().toString().substring(0, 16));
        }

        if (RANDOM.nextBoolean()) {
            headers.put("X-Request-Id", UUID.randomUUID().toString());
        }

        return toJson(headers);
    }

    private String generateResponseHeader() {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", "application/json;charset=UTF-8");
        headers.put("Cache-Control", "no-cache");

        if (RANDOM.nextBoolean()) {
            headers.put("X-RateLimit-Limit", "1000");
            headers.put("X-RateLimit-Remaining", String.valueOf(RANDOM.nextInt(1000)));
        }

        return toJson(headers);
    }

    private String generateHttpMethod() {
        String[] methods = {"GET", "POST", "PUT", "DELETE", "PATCH"};
        return methods[RANDOM.nextInt(methods.length)];
    }

    private String toJson(Map<?, ?> map) {
        try {
            return new ObjectMapper().writeValueAsString(map);
        } catch (JsonProcessingException e) {
            log.error("生成JSON失败", e);
            return "{}";
        }
    }
}




