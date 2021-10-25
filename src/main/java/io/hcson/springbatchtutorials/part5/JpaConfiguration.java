/*
 * Created by Hochan Son on 2021/10/19
 * As part of Bigin
 *
 * Copyright (C) Bigin (https://bigin.io/main) - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Dev Backend Team <hochan@bigin.io>, 2021/10/19
 */

package io.hcson.springbatchtutorials.part5;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

/**
 * create on 2021/10/19.
 * create by IntelliJ IDEA.
 *
 * <p> 클래스 설명 </p>
 * <p> {@link } and {@link }관련 클래스 </p>
 *
 * @author Hochan Son
 * @version 1.0
 * @see
 * @since 지원하는 자바버전 (ex : 5+ 5이상)
 */
@Configuration
@Slf4j
public class JpaConfiguration {

  private JobBuilderFactory jobBuilderFactory;
  private StepBuilderFactory stepBuilderFactory;
  private EntityManagerFactory entityManagerFactory;


  public JpaConfiguration(
          JobBuilderFactory jobBuilderFactory,
          StepBuilderFactory stepBuilderFactory,
          EntityManagerFactory entityManagerFactory) {
    this.jobBuilderFactory = jobBuilderFactory;
    this.stepBuilderFactory = stepBuilderFactory;
    this.entityManagerFactory = entityManagerFactory;
  }

  @Bean
  public Job JpaJob() throws Exception {
    return this.jobBuilderFactory.get("JpaJob")
            .incrementer(new RunIdIncrementer())
            .start(this.JpaStep())
            .build();
  }

  @Bean
  public Step JpaStep() throws Exception {
    return this.stepBuilderFactory.get("JapStep")
            .<PersonJpa, PersonJpa>chunk(5)
            .reader(jpaReader())
            .writer(personWriter())
            .build();
  }

  private ItemWriter<? super PersonJpa> personWriter() {
    return items -> {
      items.forEach(i -> log.info("person : {}", i));
    };
  }

  private JpaCursorItemReader<? extends PersonJpa> jpaReader() throws Exception {
    var itemReader = new JpaCursorItemReaderBuilder<PersonJpa>()
            .name("jpaReader")
            .entityManagerFactory(entityManagerFactory)
            .queryString("select p from person p")
            .build();
    itemReader.afterPropertiesSet();
    return itemReader;
  }
}