package com.dd.openapi.main.web.service.internal.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dd.ms.auth.api.UserInfoService;
import com.dd.ms.auth.vo.UserVO;
import com.dd.openapi.common.exception.DomainException;
import com.dd.openapi.main.web.common.vo.InterfaceInfoVO;
import com.dd.openapi.main.web.common.vo.UserInterfaceInvokeInfoVO;
import com.dd.openapi.main.web.converter.InterfaceInfoConverter;
import com.dd.openapi.main.web.converter.InterfaceInfoQueryBuilder;
import com.dd.openapi.main.web.converter.UserInterfaceInfoConverter;
import com.dd.openapi.main.web.converter.UserInterfaceInfoQueryBuilder;
import com.dd.openapi.main.web.mapper.InterfaceInfoMapper;
import com.dd.openapi.main.web.mapper.UserInterfaceInfoMapper;
import com.dd.openapi.main.web.model.DO.InterfaceInfoDO;
import com.dd.openapi.main.web.model.DO.UserInterfaceInfoDO;
import com.dd.openapi.main.web.model.dto.UserInterfaceInfoDTO;
import com.dd.openapi.main.web.model.req.interfaceinfo.InterfaceInfoAddReq;
import com.dd.openapi.main.web.model.req.interfaceinfo.InterfaceInfoDeleteReq;
import com.dd.openapi.main.web.model.req.interfaceinfo.InterfaceInfoQueryReq;
import com.dd.openapi.main.web.model.req.interfaceinfo.InterfaceInfoUpdateReq;
import com.dd.openapi.main.web.service.internal.InterfaceInfoService;
import com.dd.openapi.main.web.util.AuthUtils;
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

    private final AuthUtils authUtils;
    private final UserInterfaceInfoMapper userInterfaceInfoMapper;

    @DubboReference(interfaceClass = UserInfoService.class, group = "DUBBO_DD_MS_AUTH", version = "1.0")
    private UserInfoService userInfoService;

    @Override
    public void addOne(InterfaceInfoAddReq req, String token) throws DomainException {
        UserVO userVO = userInfoService.getUserInfoByToken(token, false);
        InterfaceInfoDO interfaceInfoDO = InterfaceInfoConverter.req2DO(req);
        interfaceInfoDO.setUserAccount(userVO.getAccount());
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
    public IPage<InterfaceInfoVO> pageUseLambdaQueryWrapper(InterfaceInfoQueryReq req) {
        Page<InterfaceInfoDO> page = new Page<>(req.getPageParams().getPageNum(), req.getPageParams().getPageSize());
        LambdaQueryWrapper<InterfaceInfoDO> lqw = InterfaceInfoQueryBuilder.buildLQW(req.getQueryParams());

        // 1 根据用户类型返回不同状态的接口 不是管理员 则只能请求已上线的接口
        if(!Objects.requireNonNull(authUtils.getCurrUser(true)).getRolesList().contains("admin")) {
            // 用户只能查询上线的API
            lqw.eq(InterfaceInfoDO::getStatus, 1);
        }
        Page<InterfaceInfoDO> interfaceInfoDOPage = this.page(page, lqw);

        // 2 填充接口的用户调用信息
        Map<Long, InterfaceInfoVO> interfaceInfoVOMap = interfaceInfoDOPage.getRecords().stream()
                .map(InterfaceInfoConverter::DO2VO)
                .collect(Collectors.toMap(InterfaceInfoVO::getId, e -> e));
        Set<Long> interfaceInfoIds = interfaceInfoVOMap.keySet();
        LambdaQueryWrapper<UserInterfaceInfoDO> lqw2 = new LambdaQueryWrapper<>();
        // ids不为空才拼接条件
        Optional.of(interfaceInfoIds).filter(ids -> !ids.isEmpty())
                .ifPresent(ids -> lqw2.in(UserInterfaceInfoDO::getInterfaceInfoId, ids));

        // 2.2 查询接口的用户调用信息
        List<UserInterfaceInfoDO> userInterfaceInfoDOS = userInterfaceInfoMapper.selectList(lqw2);
        Map<Long, UserInterfaceInfoDO> userInterfaceInfoDOMap = userInterfaceInfoDOS.stream()
                .collect(Collectors.toMap(UserInterfaceInfoDO::getInterfaceInfoId, e -> e));
        for (Long interfaceInfoId : interfaceInfoIds) {
            UserInterfaceInfoDO info = userInterfaceInfoDOMap.get(interfaceInfoId);
            interfaceInfoVOMap.get(interfaceInfoId).setUserInterfaceInvokeInfoVO(
                    UserInterfaceInvokeInfoVO.builder()
                            .invokedNum(Optional.ofNullable(info).map(UserInterfaceInfoDO::getTotalNum).orElse(0))
                            .invokeLeftNum(Optional.ofNullable(info).map(UserInterfaceInfoDO::getLeftNum).orElse(0))
                            .build()
            );
        }

        // 3 返回数据
        Page<InterfaceInfoVO> interfaceInfoVOPage = new Page<>();
        BeanUtils.copyProperties(interfaceInfoDOPage, interfaceInfoVOPage);
        return interfaceInfoVOPage.setRecords(new ArrayList<>(interfaceInfoVOMap.values()));
    }

    @Override
    public IPage<InterfaceInfoVO> pageUseCorrelatedQuery(InterfaceInfoQueryReq req) {

        Page<UserInterfaceInfoDTO> page = new Page<>(req.getPageParams().getPageNum(), req.getPageParams().getPageSize());

        // 1 构造查询条件
        Map<String, Object> queryConditions = UserInterfaceInfoQueryBuilder.buildQueryConditions(req);
        // 1.2 根据用户类型返回不同状态的接口 不是管理员 则只能请求已上线的接口
        if(!Objects.requireNonNull(authUtils.getCurrUser(true)).getRolesList().contains("admin")) {
            // 用户只能查询上线的API
            queryConditions.put("status", 1);
        }

        // 2 调用 Mapper 层的分页查询方法
        IPage<UserInterfaceInfoDTO> userInterfaceInfoDTOIPage = userInterfaceInfoMapper.pageUserInterfaceInfoDTO(page, queryConditions);
        List<InterfaceInfoVO> interfaceInfoVOList = userInterfaceInfoDTOIPage.getRecords().stream()
                .map(UserInterfaceInfoConverter::DO2VO).collect(Collectors.toList());

        IPage<InterfaceInfoVO> rst = new Page<>();
        BeanUtils.copyProperties(userInterfaceInfoDTOIPage, rst);
        return rst.setRecords(interfaceInfoVOList);
    }

    @Override
    public InterfaceInfoVO get(Long id) {
        return InterfaceInfoConverter.DO2VO(this.getById(id));
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




