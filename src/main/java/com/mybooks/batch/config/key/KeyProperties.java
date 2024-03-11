package com.mybooks.batch.config.key;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * packageName    : store.mybooks.resource.config
 * fileName       : KeyProperties
 * author         : Fiat_lux
 * date           : 2/20/24
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2/20/24        Fiat_lux       최초 생성
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "key.manager")
public class KeyProperties {
    private String url;
    private String path;
    private String appKey;
    private String password;
}
