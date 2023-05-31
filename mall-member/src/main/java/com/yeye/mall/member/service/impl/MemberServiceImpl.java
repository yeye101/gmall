package com.yeye.mall.member.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.Method;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yeye.common.exception.BizCodeEnum;
import com.yeye.common.to.MemberRes;
import com.yeye.common.utils.PageUtils;
import com.yeye.common.utils.Query;
import com.yeye.common.utils.R;
import com.yeye.mall.member.dao.MemberDao;
import com.yeye.mall.member.entity.MemberEntity;
import com.yeye.mall.member.entity.MemberLevelEntity;
import com.yeye.mall.member.entity.MemberReceiveAddressEntity;
import com.yeye.mall.member.exception.RegisterCommonException;
import com.yeye.mall.member.interceptor.LoginUserInterceptor;
import com.yeye.mall.member.service.MemberLevelService;
import com.yeye.mall.member.service.MemberReceiveAddressService;
import com.yeye.mall.member.service.MemberService;
import com.yeye.mall.member.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {


  @Autowired
  private MemberLevelService memberLevelService;

  @Autowired
  private MemberReceiveAddressService memberReceiveAddressService;


  @Override
  public PageUtils queryPage(Map<String, Object> params) {
    IPage<MemberEntity> page = this.page(
      new Query<MemberEntity>().getPage(params),
      new QueryWrapper<MemberEntity>()
    );

    return new PageUtils(page);
  }

  @Override
  public R register(MemberRegisterVo vo) {


    try {
      MemberEntity memberEntity = new MemberEntity();

      // 验证唯一
      this.checkUniqueUserByName(vo);
      this.checkUniqueUserByPhone(vo);


      memberEntity.setMobile(vo.getPhone());
      memberEntity.setUsername(vo.getUserName());

      // 存密码
      BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
      String md5Passwords = passwordEncoder.encode(vo.getPassword());
      memberEntity.setPassword(md5Passwords);

      // 默认会员等级id
      MemberLevelEntity defaultLevel = memberLevelService.getOne(
        new LambdaQueryWrapper<MemberLevelEntity>().eq(MemberLevelEntity::getDefaultStatus, 1));
      memberEntity.setLevelId(defaultLevel.getId());

      // 设置默认值
      memberEntity.setNickname("小开" + UUID.randomUUID().toString().substring(0, 4));
      memberEntity.setBirth(DateUtil.parse("2001-1-1", "yyyy-MM-dd"));
      memberEntity.setEmail("aabbcc@mall");
      memberEntity.setGender(1);

      // 积分
      memberEntity.setIntegration(0);
      memberEntity.setGrowth(0);


      // 系统状态
      memberEntity.setStatus(1);
      memberEntity.setCreateTime(new Date());


      this.baseMapper.insert(memberEntity);

    } catch (Exception e) {
      log.error(e.getMessage());
      return R.error(BizCodeEnum.REG_EXIST_EXCEPTION.getCode(), e.getMessage());
    }
    return R.ok();
  }

  @Override
  public void checkUniqueUserByName(MemberRegisterVo vo) throws RegisterCommonException {
    Integer integer = this.baseMapper.selectCount(new LambdaQueryWrapper<MemberEntity>()
      .eq(MemberEntity::getUsername, vo.getUserName()));


    if (integer > 0) {
      throw new RegisterCommonException("名字已存在");

    }
  }

  @Override
  public void checkUniqueUserByPhone(MemberRegisterVo vo) throws RegisterCommonException {
    Integer integer = this.baseMapper.selectCount(new LambdaQueryWrapper<MemberEntity>()
      .eq(MemberEntity::getMobile, vo.getPhone()));

    if (integer > 0) {
      throw new RegisterCommonException("电话号码已存在");

    }

  }

  @Override
  public R login(MemberLoginVo vo) {

    try {

      MemberEntity memberEntity = this.baseMapper.selectOne(new LambdaQueryWrapper<MemberEntity>()
        .eq(MemberEntity::getUsername, vo.getAccount())
        .or()
        .eq(MemberEntity::getMobile, vo.getAccount()));

      if (null == memberEntity) {
        return R.error(BizCodeEnum.LOGIN_ERROR.getCode(), BizCodeEnum.LOGIN_ERROR.getMsg());
      }

      BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
      if (!passwordEncoder.matches(vo.getPassword(), memberEntity.getPassword())) {
        return R.error(BizCodeEnum.LOGIN_ERROR.getCode(), BizCodeEnum.LOGIN_ERROR.getMsg());
      }


      return R.ok().put("data", memberEntity);

    } catch (Exception e) {
      e.printStackTrace();
      return R.error(BizCodeEnum.LOGIN_ERROR.getCode(), BizCodeEnum.LOGIN_ERROR.getMsg());
    }

  }

  /**
   * 注册和登陆一并进行
   *
   * @param vo
   * @return
   */
  @Override
  public R socialLogin(MemberSocialLoginVo vo) {

    try {
      HashMap<String, Object> formMap = new HashMap<>();
      formMap.put("access_token", vo.getAccess_token());
      // 网络请求，需要防止意外
      HttpResponse response = HttpRequest.get("https://gitee.com/api/v5/user")
        .method(Method.GET)
        .form(formMap)
        .execute();
      // 不为200则失败
      if (response.getStatus() != 200) {
        log.error("三方登陆获取用户信息失败，请重试");
        return R.error(BizCodeEnum.LOGIN_SOCIAL_ERROR.getCode(), BizCodeEnum.LOGIN_SOCIAL_ERROR.getMsg());
      }

      MemberSocialLoginRes res = JSON.parseObject(response.body(), MemberSocialLoginRes.class);

      MemberEntity memberEntity = this.getOne(new LambdaQueryWrapper<MemberEntity>().eq(MemberEntity::getGiteeId, res.getId()));
      if (memberEntity != null) {
        // 已经注册了就直接返回
        return R.ok().put("data", memberEntity);
      }

      // 否则注册
      memberEntity = new MemberEntity();
      memberEntity.setUsername(res.getName());

      memberEntity.setHeader(res.getAvatar_url());
      memberEntity.setSign(res.getBio());
      memberEntity.setGiteeId(res.getId().toString());

      // 默认会员等级id
      MemberLevelEntity defaultLevel = memberLevelService.getOne(
        new LambdaQueryWrapper<MemberLevelEntity>().eq(MemberLevelEntity::getDefaultStatus, 1));
      memberEntity.setLevelId(defaultLevel.getId());

      // 设置默认值
      memberEntity.setNickname("小开" + UUID.randomUUID().toString().substring(0, 4));
      memberEntity.setBirth(DateUtil.parse("2001-1-1", "yyyy-MM-dd"));
      memberEntity.setEmail("aabbcc@mall");
      memberEntity.setGender(1);

      // 积分
      memberEntity.setIntegration(0);
      memberEntity.setGrowth(0);


      // 系统状态
      memberEntity.setStatus(1);
      memberEntity.setCreateTime(new Date());
      // 保存
      this.baseMapper.insert(memberEntity);


      return R.ok().put("data", memberEntity);


    } catch (HttpException e) {

      log.error("三方登陆获取用户信息失败，请重试,原因{}", e);
      return R.error(BizCodeEnum.LOGIN_SOCIAL_ERROR.getCode(), BizCodeEnum.LOGIN_SOCIAL_ERROR.getMsg());
    }

  }

  @Override
  public R memberModify(MemberModifyVo vo) {
    MemberRes memberRes = LoginUserInterceptor.loginUser.get();

    MemberEntity entity = new MemberEntity();
    entity.setId(memberRes.getId());
    entity.setBirth(vo.getBirth());
    entity.setSign(vo.getSign());
    entity.setNickname(vo.getNickName());
    this.updateById(entity);

    return R.ok();
  }

  @Override
  public MemberInfoVo getMemberInfo(Long id) {
    MemberInfoVo memberInfoVo = new MemberInfoVo();
    MemberEntity byId = this.getById(id);
    BeanUtils.copyProperties(byId, memberInfoVo);

    List<MemberReceiveAddressEntity> addressById = memberReceiveAddressService.getAddressById(id);
    memberInfoVo.setAddressVos(addressById);
    return memberInfoVo;
  }

}