/*
 * Created by Hochan Son on 2021/10/07
 * As part of Bigin
 *
 * Copyright (C) Bigin (https://bigin.io/main) - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Dev Backend Team <hochan@bigin.io>, 2021/10/07
 */

package io.hcson.springbatchtutorials.part1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
public class SimpleJobConfiguration {
  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;
  private final SimpleTasklet simpleTasklet;

  @Bean
  public Job simpleJob() {
    return jobBuilderFactory.get("simpleJob")
            .incrementer(new RunIdIncrementer())
            .start(firstStep())
            .next(secondStep())

            .build();
  }

  private Step secondStep() {
    return stepBuilderFactory.get("secondStep")
            .tasklet((contribution, chunkContext) -> {
              log.info("this is second tasklet");
              return RepeatStatus.FINISHED;
            })
            .build();
  }

  @Bean
  public Step firstStep() {
    return stepBuilderFactory.get("firstStep")
            .tasklet(simpleTasklet)
            .build();
  }
}
