/*
 * Created by Hochan Son on 2021/10/07
 * As part of Bigin
 *
 * Copyright (C) Bigin (https://bigin.io/main) - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Dev Backend Team <hochan@bigin.io>, 2021/10/07
 */

package io.hcson.springbatchtutorials.part2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import javax.sql.DataSource;

/**
 * create on 2021/10/07.
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
@RequiredArgsConstructor
public class JdbcCursorJobConfiguration {
  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;
  private static final int chunkSize = 5;
  private final DataSource dataSource;

  @Bean
  public Job jdbcCursorJob() {
    return jobBuilderFactory.get("jdbcCursorJob")
            .incrementer(new RunIdIncrementer())
            .start(step1())
            .build();
  }

  private Step step1() {
    return stepBuilderFactory.get("jdbcStep1")
            .<Person, Person>chunk(chunkSize)
            .reader(jdbcReader())
            .processor(processor())
            .writer(writer())
            .build();
  }

  private ItemProcessor<? super Person, ? extends Person> processor() {


    return item -> item;
  }

  private ItemWriter<? super Person> writer() {
    return list -> {
      log.info("writer Called");
      for (Person person : list) {

        log.info(person.toString());
      }
    };
  }

  public ItemReader<Person> jdbcReader() {
    return new JdbcCursorItemReaderBuilder<Person>()
            .fetchSize(chunkSize)
            .dataSource(dataSource)
            .rowMapper(new BeanPropertyRowMapper<>(Person.class))
            .sql("select name, age, address from person")
            .name("jdbcReader")
            .build();
  }
}
