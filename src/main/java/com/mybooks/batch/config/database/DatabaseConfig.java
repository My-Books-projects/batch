package com.mybooks.batch.config.database;

import com.mybooks.batch.config.key.KeyConfig;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * packageName    : com.mybooks.batch.config.database
 * fileName       : DatabaseConfig
 * author         : damho-lee
 * date           : 3/11/24
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 3/11/24          damho-lee          최초 생성
 */
@Configuration
public class DatabaseConfig {
    private final KeyConfig keyConfig;
    private final DatabaseProperties databaseProperties;

    @Autowired
    public DatabaseConfig(KeyConfig keyConfig, DatabaseProperties databaseProperties) {
        this.keyConfig = keyConfig;
        this.databaseProperties = databaseProperties;
    }

    /**
     * methodName : dataSource <br>
     * author : damho-lee <br>
     * description : DataSource 설정 파일.<br>
     *
     * @return DataSource
     */
    @Bean
    public DataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();

        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl(keyConfig.keyStore(databaseProperties.getUrl()));
        dataSource.setUsername(keyConfig.keyStore(databaseProperties.getUserName()));
        dataSource.setPassword(keyConfig.keyStore(databaseProperties.getPassword()));

        dataSource.setInitialSize(databaseProperties.getInitialSize());
        dataSource.setMaxTotal(databaseProperties.getMaxTotal());
        dataSource.setMinIdle(databaseProperties.getMinIdle());
        dataSource.setMaxIdle(databaseProperties.getMaxIdle());

        dataSource.setMaxWaitMillis(databaseProperties.getMaxWait());

        dataSource.setTestOnBorrow(true);
        dataSource.setTestOnReturn(true);
        dataSource.setTestWhileIdle(true);

        return dataSource;
    }

}
