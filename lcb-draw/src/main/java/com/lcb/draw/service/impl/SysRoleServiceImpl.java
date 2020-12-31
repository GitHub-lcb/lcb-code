package com.lcb.draw.service.impl;

import com.lcb.draw.dao.SysRoleMapper;
import com.lcb.draw.model.SysRole;
import com.lcb.draw.service.SysRoleService;
import com.lcb.draw.core.AbstractService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
 * Created by lcb on 2020/12/30.
 */
@Service
@Transactional
public class SysRoleServiceImpl extends AbstractService<SysRole> implements SysRoleService {
    @Resource
    private SysRoleMapper sysRoleMapper;

}
