package com.lcb.draw.service.impl;

import com.lcb.draw.dao.SysUserMapper;
import com.lcb.draw.model.SysUser;
import com.lcb.draw.service.SysUserService;
import com.lcb.draw.core.AbstractService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
 * Created by lcb on 2020/12/30.
 */
@Service
@Transactional
public class SysUserServiceImpl extends AbstractService<SysUser> implements SysUserService {
    @Resource
    private SysUserMapper sysUserMapper;

}
