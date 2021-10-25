/*
 * Created by Hochan Son on 2021/10/19
 * As part of Bigin
 *
 * Copyright (C) Bigin (https://bigin.io/main) - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Dev Backend Team <hochan@bigin.io>, 2021/10/19
 */

package io.hcson.springbatchtutorials.report;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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
@Entity(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class Member {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private int age;

  private String part;

  private String name;

  public Member(int age, String part, String name) {
    this.age = age;
    this.part = part;
    this.name = name;
  }

  public Member(long id, int age, String part, String name) {
    this.age = age;
    this.part = part;
    this.name = name;
  }
}
