/*
 * Created by Hochan Son on 2021/10/19
 * As part of Bigin
 *
 * Copyright (C) Bigin (https://bigin.io/main) - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Dev Backend Team <hochan@bigin.io>, 2021/10/19
 */

package io.hcson.springbatchtutorials.part6;

import io.hcson.springbatchtutorials.part5.PersonJpa;
import io.hcson.springbatchtutorials.util.CustomItemReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
public class JpaJob2Configuration {

  private JobBuilderFactory jobBuilderFactory;
  private StepBuilderFactory stepBuilderFactory;
  private EntityManagerFactory entityManagerFactory;

  public JpaJob2Configuration(
          JobBuilderFactory jobBuilderFactory,
          StepBuilderFactory stepBuilderFactory,
          EntityManagerFactory entityManagerFactory) {
    this.jobBuilderFactory = jobBuilderFactory;
    this.stepBuilderFactory = stepBuilderFactory;
    this.entityManagerFactory = entityManagerFactory;
  }

  @Bean
  public Job jpaWriterJob() throws Exception {
    return this.jobBuilderFactory.get("jpaWriterJob")
            .incrementer(new RunIdIncrementer())
            .start(this.jpaWriterStep())
            .build();
  }

  @Bean
  public Step jpaWriterStep() throws Exception {
    return this.stepBuilderFactory.get("jpaWriterStep")
            .<PersonJpa, PersonJpa>chunk(5)
            .reader(readRandomPerson())
            .writer(jpaWriter())
            .build();
  }

  private ItemWriter<? super PersonJpa> jpaWriter() throws Exception {
    JpaItemWriter itemWriter = new JpaItemWriterBuilder<PersonJpa>()
            .entityManagerFactory(entityManagerFactory)
            .build();
    itemWriter.afterPropertiesSet();
    return itemWriter;
  }

  private ItemReader<? extends PersonJpa> readRandomPerson() {
    return new CustomItemReader<PersonJpa>(getRandomItems());
  }

  private List<PersonJpa> getRandomItems() {
    List<PersonJpa> items = new ArrayList<>();

    for (int i = 0; i < 100; i++) {
      items.add(new PersonJpa("testName", new Random().nextInt(70), "seoul"));
    }
    return items;
  }
}