package com.user9527.shopping.product.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author yangtao
 * @version 1.0
 * @date 2021/7/16 8:32
 */
@ConfigurationProperties(prefix = "shopping.thread")
@Component
@Data
public class ThreadPoolConfigProperties {
    private Integer coreSize;
    private Integer maxSize;
    private Integer keepAliveTime;
}
