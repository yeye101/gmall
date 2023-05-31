package com.yeye.mall.member.vo;

import lombok.Data;

import java.util.Date;

@Data
public class MemberSocialLoginRes {

  private Long id;
  private String login;
  private String name;
  private String avatar_url;
  private String url;
  private String html_url;
  private String remark;
  private String followers_url;
  private String following_url;
  private String gists_url;
  private String starred_url;
  private String subscriptions_url;
  private String organizations_url;
  private String repos_url;
  private String events_url;
  private String received_events_url;
  private String type;
  private String blog;
  private String weibo;
  private String bio;
  private Integer public_repos;
  private Integer public_gists;
  private Integer followers;
  private Integer following;
  private Integer stared;
  private Integer watched;
  private Date created_at;
  private Date updated_at;
  private String email;

}
