package com.enodeframework.tests;

import com.enodeframework.ENodeBootstrap;
import com.enodeframework.commanding.impl.DefaultCommandProcessor;
import com.enodeframework.commanding.impl.DefaultProcessingCommandHandler;
import com.enodeframework.eventing.impl.EventCommittingService;
import com.enodeframework.eventing.impl.InMemoryEventStore;
import com.enodeframework.infrastructure.impl.InMemoryPublishedVersionStore;
import com.enodeframework.queue.command.CommandResultProcessor;
import com.google.common.collect.Lists;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import static com.enodeframework.tests.Constants.JDBC_URL;

@ComponentScan(value = "com.enodeframework")
public class EnodeExtensionConfig {

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public CommandResultProcessor commandResultProcessor() {
        CommandResultProcessor processor = new CommandResultProcessor();
        return processor;
    }

    @Bean(initMethod = "init")
    public ENodeBootstrap eNodeBootstrap() {
        ENodeBootstrap bootstrap = new ENodeBootstrap();
        bootstrap.setPackages(Lists.newArrayList("com.enodeframework.tests"));
        return bootstrap;
    }

    @Bean
    public DefaultProcessingCommandHandler defaultProcessingCommandHandler() {
        return new DefaultProcessingCommandHandler();
    }

    @Bean
    public EventCommittingService defaultEventService() {
        return new EventCommittingService();
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public DefaultCommandProcessor defaultCommandProcessor() {
        return new DefaultCommandProcessor();
    }

//    @Bean
//    public MysqlEventStoreVertx mysqlEventStore(HikariDataSource dataSource) {
//        MysqlEventStoreVertx mysqlEventStore = new MysqlEventStoreVertx(dataSource, null);
//        return mysqlEventStore;
//    }
//
//    @Bean
//    public MysqlPublishedVersionStoreVertx mysqlPublishedVersionStore(HikariDataSource dataSource) {
//        return new MysqlPublishedVersionStoreVertx(dataSource, null);
//    }

    @Bean
    public InMemoryEventStore inMemoryEventStore(HikariDataSource dataSource) {
        return new InMemoryEventStore();
    }

    @Bean
    public InMemoryPublishedVersionStore inMemoryPublishedVersionStore(HikariDataSource dataSource) {
        return new InMemoryPublishedVersionStore();
    }

    @Bean
    public HikariDataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(JDBC_URL);
        dataSource.setUsername("root");
        dataSource.setPassword("root");
        dataSource.setDriverClassName(com.mysql.cj.jdbc.Driver.class.getName());
        return dataSource;
    }
}
