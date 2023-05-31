package com.yeye.mall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import lombok.Data;

/**
 * 商品评价回复关系
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 13:33:43
 */
@Data
@TableName("pms_comment_replay")
public class CommentReplayEntity implements Serializable {
  private static final long serialVersionUID = 1L;

  /**
   * id
   */
  @TableId
  private Long id;
  /**
   * 评论id
   */
  private Long commentId;
  /**
   * 回复id
   */
  private Long replyId;

}
