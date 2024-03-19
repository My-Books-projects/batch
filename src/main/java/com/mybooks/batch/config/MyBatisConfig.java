package com.mybooks.batch.config;

import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * packageName    : com.mybooks.batch.config
 * fileName       : MyBatisConfig
 * author         : damho-lee
 * date           : 3/12/24
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 3/12/24          damho-lee          최초 생성
 */
@Configuration
@RequiredArgsConstructor
public class MyBatisConfig {
    private final DataSource dataSource;
    private final ApplicationContext applicationContext;

    /**
     * methodName : sqlSessionFactory <br>
     * author : damho-lee <br>
     * description : SqlSessionFactory 빈 등록.<br>
     *
     * @return SqlSessionFactory
     * @throws Exception the exception
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setMapperLocations(
                applicationContext.getResources("classpath*:mapper/*.xml"));
        return sqlSessionFactoryBean.getObject();
    }

    /**
     * methodName : sqlSessionTemplate <br>
     * author : damho-lee <br>
     * description : SqlSessionTemplate 빈 등록.<br>
     *
     * @param sqlSessionFactory SqlSessionFactory
     * @return SqlSessionTemplate
     */
    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
