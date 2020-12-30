package com.lcb.draw.controller;
import com.lcb.draw.core.Result;
import com.lcb.draw.core.ResultGenerator;
import com.lcb.draw.model.SysUser;
import com.lcb.draw.service.SysUserService;
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
@RequestMapping("/sys/user")
public class SysUserController {
    @Resource
    private SysUserService sysUserService;

    @PostMapping("/add")
    public Result add(SysUser sysUser) {
        sysUserService.save(sysUser);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/delete")
    public Result delete(@RequestParam Integer id) {
        sysUserService.deleteById(id);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/update")
    public Result update(SysUser sysUser) {
        sysUserService.update(sysUser);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/detail")
    public Result detail(@RequestParam Integer id) {
        SysUser sysUser = sysUserService.findById(id);
        return ResultGenerator.genSuccessResult(sysUser);
    }

    @PostMapping("/list")
    public Result list(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        List<SysUser> list = sysUserService.findAll();
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }
}
