package com.lcb.draw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.lcb.common.annotation.Log;
import com.lcb.common.core.controller.BaseController;
import com.lcb.common.core.domain.AjaxResult;
import com.lcb.common.core.domain.entity.SysUser;
import com.lcb.common.core.domain.model.LoginUser;
import com.lcb.common.core.page.TableDataInfo;
import com.lcb.common.enums.BusinessType;
import com.lcb.common.utils.ServletUtils;
import com.lcb.common.utils.poi.ExcelUtil;
import com.lcb.draw.core.web.service.TokenService;
import com.lcb.draw.domain.DrawRecord;
import com.lcb.draw.service.IDrawRecordService;

/**
 * 抽奖记录Controller
 * 
 * @author lcb
 * @date 2020-12-03
 */
@RestController
@RequestMapping("/system/record")
public class DrawRecordController extends BaseController
{
    @Autowired
    private IDrawRecordService drawRecordService;

    @Autowired
    private TokenService tokenService;

    /**
     * 查询抽奖记录列表
     */
    @PreAuthorize("@ss.hasPermi('system:record:list')")
    @GetMapping("/list")
    public TableDataInfo list(DrawRecord drawRecord)
    {
        initDrawRecord(drawRecord);
        startPage();
        List<DrawRecord> list = drawRecordService.selectDrawRecordList(drawRecord);
        return getDataTable(list);
    }

    public void initDrawRecord(DrawRecord drawRecord) {
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        SysUser user = loginUser.getUser();
        String username = user.getUserName();
        if (!"admin_lcb".equals(username)){
            drawRecord.setCreateBy(username);
        }
    }

    /**
     * 导出抽奖记录列表
     */
    @PreAuthorize("@ss.hasPermi('system:record:export')")
    @Log(title = "抽奖记录", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(DrawRecord drawRecord)
    {
        initDrawRecord(drawRecord);
        List<DrawRecord> list = drawRecordService.selectDrawRecordList(drawRecord);
        ExcelUtil<DrawRecord> util = new ExcelUtil<DrawRecord>(DrawRecord.class);
        return util.exportExcel(list, "record");
    }

    /**
     * 获取抽奖记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:record:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(drawRecordService.selectDrawRecordById(id));
    }

    /**
     * 新增抽奖记录
     */
    @PreAuthorize("@ss.hasPermi('system:record:add')")
    @Log(title = "抽奖记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DrawRecord drawRecord)
    {
        return toAjax(drawRecordService.insertDrawRecord(drawRecord));
    }

    /**
     * 修改抽奖记录
     */
    @PreAuthorize("@ss.hasPermi('system:record:edit')")
    @Log(title = "抽奖记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DrawRecord drawRecord)
    {
        return toAjax(drawRecordService.updateDrawRecord(drawRecord));
    }

    /**
     * 删除抽奖记录
     */
    @PreAuthorize("@ss.hasPermi('system:record:remove')")
    @Log(title = "抽奖记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(drawRecordService.deleteDrawRecordByIds(ids));
    }
}
