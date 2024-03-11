package com.mybooks.batch.config.database;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * packageName    : com.mybooks.batch.config.database
 * fileName       : DatabaseProperties
 * author         : damho-lee
 * date           : 3/11/24
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 3/11/24          damho-lee          최초 생성
 */
@ConfigurationProperties(prefix = "database.mysql")
@Getter
@Setter
public class DatabaseProperties {
    private String url;
    private String userName;
    private String password;
    private Integer initialSize;
    private Integer maxTotal;
    private Integer minIdle;
    private Integer maxIdle;
    private Integer maxWait;
}