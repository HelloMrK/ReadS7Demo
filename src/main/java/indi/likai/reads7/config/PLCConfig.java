package indi.likai.reads7.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * PLC S7配置实例
 */
@Component
@Data
@ConfigurationProperties(prefix = "plcs7")
public class PLCConfig {
    String host;
    Integer port;
    Integer slot;
    Integer rack;
}