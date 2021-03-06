package com.lcb.draw.service;

import java.util.List;

import com.lcb.draw.bean.DrawBaoDiBean;
import com.lcb.draw.domain.DrawRecord;

/**
 * 抽奖记录Service接口
 * 
 * @author lcb
 * @date 2020-12-03
 */
public interface IDrawRecordService 
{
    /**
     * 查询抽奖记录
     * 
     * @param id 抽奖记录ID
     * @return 抽奖记录
     */
    public DrawRecord selectDrawRecordById(Long id);

    /**
     * 查询抽奖记录列表
     * 
     * @param drawRecord 抽奖记录
     * @return 抽奖记录集合
     */
    public List<DrawRecord> selectDrawRecordList(DrawRecord drawRecord);

    /**
     * 新增抽奖记录
     * 
     * @param drawRecord 抽奖记录
     * @return 结果
     */
    public int insertDrawRecord(DrawRecord drawRecord);

    /**
     * 修改抽奖记录
     * 
     * @param drawRecord 抽奖记录
     * @return 结果
     */
    public int updateDrawRecord(DrawRecord drawRecord);

    /**
     * 批量删除抽奖记录
     * 
     * @param ids 需要删除的抽奖记录ID
     * @return 结果
     */
    public int deleteDrawRecordByIds(Long[] ids);

    /**
     * 删除抽奖记录信息
     * 
     * @param id 抽奖记录ID
     * @return 结果
     */
    public int deleteDrawRecordById(Long id);

    int getTotalCount(String userName);

    List<DrawBaoDiBean> selectBaodi();
}
