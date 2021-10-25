/*
 * Created by Hochan Son on 2021/10/24
 * As part of Bigin
 *
 * Copyright (C) Bigin (https://bigin.io/main) - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Dev Backend Team <hochan@bigin.io>, 2021/10/24
 */

package io.hcson.springbatchtutorials.report;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import javax.persistence.EntityManagerFactory;

/**
 * create on 2021/10/24.
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
public class ReportConfiguration {

  private JobBuilderFactory jobBuilderFactory;
  private StepBuilderFactory stepBuilderFactory;
  private EntityManagerFactory entityManagerFactory;

  public ReportConfiguration(
          JobBuilderFactory jobBuilderFactory,
          StepBuilderFactory stepBuilderFactory,
          EntityManagerFactory entityManagerFactory) {
    this.jobBuilderFactory = jobBuilderFactory;
    this.stepBuilderFactory = stepBuilderFactory;
    this.entityManagerFactory = entityManagerFactory;
  }

  @Bean
  public Job reportJob() throws Exception {
    return this.jobBuilderFactory.get("reportJob")
            .incrementer(new RunIdIncrementer())
            .start(this.reportStep())
            .build();
  }

  @Bean
  public Step reportStep() throws Exception {
    return this.stepBuilderFactory.get("reportStep")
            .<Member, Member>chunk(5)
            .reader(jpaReader())
//            .processor()
            .writer(csvWriter())
            .build();
  }

  private ItemWriter<? super Member> csvWriter() throws Exception {
    DelimitedLineAggregator<Member> lineAggregator = new DelimitedLineAggregator<>();
    lineAggregator.setDelimiter("\t");

    BeanWrapperFieldExtractor<Member> fieldExtractor = new BeanWrapperFieldExtractor<>();
    fieldExtractor.setNames(new String[] {
            "id", "name", "age", "part"
            });
    lineAggregator.setFieldExtractor(fieldExtractor);

    var itemWriter = new FlatFileItemWriterBuilder<Member>()
            .name("csvWriter")
            .encoding("UTF-8")
            .resource(new FileSystemResource("report/report-person.csv"))
            .headerCallback(writer -> writer.write("id\t이름\t나이\t파트"))
            .footerCallback(writer -> writer.write("\n"))
            .append(true)
            .lineAggregator(lineAggregator)
            .build();
    itemWriter.afterPropertiesSet();
    return itemWriter;
  }

  private ItemReader<? extends Member> jpaReader() throws Exception {
    var itemReader = new JpaCursorItemReaderBuilder<Member>()
            .name("jpaReader")
            .entityManagerFactory(entityManagerFactory)
            .queryString("select m from member m")
            .build();
    itemReader.afterPropertiesSet();
    return itemReader;
  }
}