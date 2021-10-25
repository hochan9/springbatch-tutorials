/*
 * Created by Hochan Son on 2021/10/19
 * As part of Bigin
 *
 * Copyright (C) Bigin (https://bigin.io/main) - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Dev Backend Team <hochan@bigin.io>, 2021/10/19
 */

package io.hcson.springbatchtutorials.part4;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import static java.nio.file.Files.write;

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
public class ChunkCsvTaskJobConfiguration {

  private JobBuilderFactory jobBuilderFactory;
  private StepBuilderFactory stepBuilderFactory;

  public ChunkCsvTaskJobConfiguration(
          JobBuilderFactory jobBuilderFactory,
          StepBuilderFactory stepBuilderFactory) {
    this.jobBuilderFactory = jobBuilderFactory;
    this.stepBuilderFactory = stepBuilderFactory;
  }

  @Bean
  public Job csvTaskJob() throws Exception {
    return this.jobBuilderFactory.get("csvTaskJob")
            .incrementer(new RunIdIncrementer())
            .start(this.csvTaskStep())
            .build();
  }

  @Bean
  public Step csvTaskStep() throws Exception {
    return this.stepBuilderFactory.get("csvTaskStep")
            .<Person, Person>chunk(10)
            .reader(csvReader())
            .writer(csvPrinter())
            .build();
  }

  private ItemWriter<? super Person> csvPrinter() throws Exception {
    DelimitedLineAggregator<Person> lineAggregator = new DelimitedLineAggregator<>();
    lineAggregator.setDelimiter(",");
    BeanWrapperFieldExtractor<Person> fieldExtractor = new BeanWrapperFieldExtractor<>();
    fieldExtractor.setNames(new String[] {
            "id", "name", "age", "address"
            });

    lineAggregator.setFieldExtractor(fieldExtractor);



    FlatFileItemWriter itemWriter = new FlatFileItemWriterBuilder<Person>()
            .name("csvWrite")
            .encoding("UTF-8")
            .resource(new FileSystemResource("output/part4-writer-user.csv"))
            .headerCallback(writer -> writer.write("id,이름,나이,주소\n"))
            .footerCallback(writer -> writer.write("<End> \n"))
            .append(true)
            .lineAggregator(lineAggregator)
            .build();
    itemWriter.afterPropertiesSet();
    return itemWriter;
  }

  private FlatFileItemReader<? extends Person> csvReader() throws Exception {

    DefaultLineMapper lineMapper = new DefaultLineMapper();
    // tokenizer 쪽도 고려해야됨 -> DELIMITER 를 추상화 할 수 가없기에 고려해야함
    DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();

    // 컬럼 값에는 고려해야됨
    tokenizer.setNames("id", "name", "age", "address");
    lineMapper.setLineTokenizer(tokenizer);

    // 필드 셋팅하는 부분 중요
    lineMapper.setFieldSetMapper(fieldSet -> {
      int id = fieldSet.readInt("id");
      String name = fieldSet.readString("name");
      int age = fieldSet.readInt("age");
      String address = fieldSet.readString("address");
      return new Person(id, name, age, address);
    });

    var itemReader = new FlatFileItemReaderBuilder<Person>()
            .name("csvREader")
            .encoding("UTF-8")
            .resource(new ClassPathResource("part4-user.csv"))
            .linesToSkip(1)
            .lineMapper(lineMapper)
            .build();

    itemReader.afterPropertiesSet(); // -> 중요!

    return itemReader;
  }
}