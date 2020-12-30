package com.lcb.draw.controller;
import com.lcb.draw.core.Result;
import com.lcb.draw.core.ResultGenerator;
import com.lcb.draw.model.SysRole;
import com.lcb.draw.service.SysRoleService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
* Created by lcb on 2020/12/30.
*/
@RestController
@RequestMapping("/sys/role")
public class SysRoleController {
    @Resource
    private SysRoleService sysRoleService;

    @PostMapping("/add")
    public Result add(SysRole sysRole) {
        sysRoleService.save(sysRole);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/delete")
    public Result delete(@RequestParam Integer id) {
        sysRoleService.deleteById(id);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/update")
    public Result update(SysRole sysRole) {
        sysRoleService.update(sysRole);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/detail")
    public Result detail(@RequestParam Integer id) {
        SysRole sysRole = sysRoleService.findById(id);
        return ResultGenerator.genSuccessResult(sysRole);
    }

    @PostMapping("/list")
    public Result list(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        List<SysRole> list = sysRoleService.findAll();
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }
}
