package com.dd.openapi.common.verification;

import com.dd.openapi.common.enumeration.CodedEnum;

import java.util.Arrays;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/1/17 10:45
 * @Description 数据验证
 *              (1) 检查给定对象是否为空 boolean isEmpty(Object object)
 *              (2) 根据枚举类的Class对象和枚举值标识（code）解析获取枚举对象
 */
public class DataVerificationUtil {

    /**
     * 检查给定对象是否为空。
     *
     * @param object 要检查的对象
     * @return 如果对象为null或为基本数据类型的默认值，则返回true，否则返回false
     */
    public static boolean isEmpty(Object object) {
        // 对于引用类型，直接检查是否为null
        if (object == null) {
            return true;
        }
        // 对于字符串，检查是否为空字符串或null
        if (object instanceof String) {
            return ((String) object).isEmpty();
        }
        return false;
    }

    /**
     * 根据枚举类的Class对象和枚举值标识（code）解析获取枚举对象
     *
     * @param enumClass 枚举class对象
     * @param code      枚举对象标识
     * @return          要获取的枚举值
     *
     * @param <CODE>    枚举对象标识泛型
     * @param <ENUM>    枚举泛型
     */
    public static <CODE, ENUM extends Enum<ENUM> & CodedEnum<CODE>> ENUM parseEnum(Class<ENUM> enumClass, CODE code) {
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(e -> e.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("不合法的枚举值标识: " + code));
    }
}
