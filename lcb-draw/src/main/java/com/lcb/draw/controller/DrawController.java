package com.lcb.draw.controller;

import static com.lcb.draw.bean.UrlsBean.GM_6_URL;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson.JSONObject;
import com.lcb.common.core.controller.BaseController;
import com.lcb.common.core.domain.AjaxResult;
import com.lcb.common.core.domain.entity.SysUser;
import com.lcb.common.core.domain.model.LoginUser;
import com.lcb.common.utils.ServletUtils;
import com.lcb.common.utils.http.HttpUtils;
import com.lcb.draw.bean.ItemBean;
import com.lcb.draw.bean.RoleBean;
import com.lcb.draw.core.web.service.TokenService;
import com.lcb.draw.domain.DrawItem;
import com.lcb.draw.domain.DrawRecord;
import com.lcb.draw.domain.GameAccount;
import com.lcb.draw.service.IDrawItemService;
import com.lcb.draw.service.IDrawRecordService;
import com.lcb.draw.service.IGameAccountService;
import com.lcb.draw.utils.RandomUtils;
import com.lcb.draw.utils.SignUtils;

/**
 * @author lcb
 * @date 2020-11-30
 */
@RestController
@RequestMapping("/draw")
public class DrawController extends BaseController {

    private static final String gameUrl = "http://81.70.236.13:8090/role/findByAccount";
    private static final String gameUrlUpdate = "http://81.70.236.13:8090/role/updateById";
    private static final String gmUrl_day = "http://fl123.xiaoheigame.com";

    private static Map<String, Integer> holidayMap = new HashMap();

    static {
        holidayMap.put("50918",1);
        holidayMap.put("39523",1);
        holidayMap.put("39017",30);
    }

    @Autowired
    private TokenService tokenService;

    @Autowired
    private IGameAccountService gameAccountService;

    @Autowired
    private IDrawItemService drawItemService;

    @Autowired
    private IDrawRecordService drawRecordService;

    /**
     * 开始抽奖，
     * 1.抽奖
     * 2.异步减少用户的快乐币数量
     * 3.异步给用户发送物品邮件
     */
    @PreAuthorize("@ss.hasPermi('draw:getReward')")
    @GetMapping("/getReward")
    public AjaxResult getReward(@RequestParam Integer id, @RequestParam Integer money, @RequestParam String roleName) {

        // 查询物品列表
        List<DrawItem> drawItems = drawItemService.selectDrawItemList(new DrawItem());
        // 获取角色id
        String uid = getSysUser().getUserName();

        Map<DrawItem, Integer> itemMap = new HashMap<>();
        drawItems.stream().forEach(drawItem -> {
            itemMap.put(drawItem, (int) (drawItem.getRate()*1000));
        });
        DrawItem drawItem = RandomUtils.chanceSelect(itemMap);

        boolean flag = true;
        String type = "daoju";
        DrawRecord record = initRecordLog(roleName, uid, drawItem.getName(), drawItem.getItemNum());
        Long count = null;
        try {
            GameAccount gameAccount = gameAccountService.selectGameAccountByName(uid);
            count = gameAccount.getCount();
            if (count == 0 && money < 50) {
                record.setStatus("failed,没有次数了!");
                flag = false;
            }
            Long version = gameAccount.getVersion();
            if (count > 0) {
                // 不消耗快乐币发送邮件
                String emailResult = sendEmail(drawItem.getNum()+"", uid, drawItem.getItemNum(), type);
                if ("ok！设置成功".equals(emailResult) || "发送成功".equals(emailResult)) {
                    int i = 0;
                    while (i == 0) {
                        // 并发版本控制
                        i = gameAccountService.updateGameAccountByVersion(--count, version, uid);
                    }
                }
            } else {
                // 1.减少快乐币
                money = money - 50;
                String params = "id=" + id + "&money=" + money;
                JSONObject result = JSONObject.parseObject(HttpUtils.sendGet(gameUrlUpdate, params + "&sign=" + SignUtils
                    .getSign(params)));
                if (result.getInteger("code") == 200) {
                    sendEmail(drawItem.getNum()+"", uid, drawItem.getItemNum(), type);
                }
            }
            record.setStatus("ok");
        } catch (Exception e) {
            e.printStackTrace();
            record.setStatus("fail:" + e.getMessage());
        } finally {
            drawRecordService.insertDrawRecord(record);
        }



        int totalCount = drawRecordService.getTotalCount(uid);


        if (!flag){
            return new AjaxResult(503, "请求失败！", new ItemBean(Math.toIntExact(drawItem.getLevel()), drawItem.getName(), Math.toIntExact(count), money, totalCount));
        }
        return new AjaxResult(200, "请求成功！", new ItemBean(Math.toIntExact(drawItem.getLevel()), drawItem.getName()+drawItem.getItemNum(), Math.toIntExact(count), money, totalCount));
    }

    private DrawRecord initRecordLog(@RequestParam String roleName, String uid, String itemName, int num) {
        DrawRecord record = new DrawRecord();
        record.setRoleName(roleName);
        record.setPrize(itemName + num);
        record.setCreateBy(uid);
        record.setCreateTime(new Date());
        return record;
    }


    /**
     * 开始抽奖，
     * 1.抽奖
     * 2.异步减少用户的快乐币数量
     * 3.异步给用户发送物品邮件
     */
    @PreAuthorize("@ss.hasPermi('draw:getReward')")
    @GetMapping("/getReward10")
    public AjaxResult getReward10(@RequestParam Integer id, @RequestParam Integer money, @RequestParam String roleName) {

        // 查询物品列表
        List<DrawItem> drawItems = drawItemService.selectDrawItemList(new DrawItem());
        Map<DrawItem, Integer> itemMap = new HashMap<>();
        drawItems.stream().forEach(drawItem -> {
            itemMap.put(drawItem, (int) (drawItem.getRate()*1000));
        });

        DrawItem drawItem = RandomUtils.chanceSelect(itemMap);
        String uid = getSysUser().getUserName();

        String itemName = drawItem.getName();
        String item = drawItem.getNum()+"";
        int num = drawItem.getItemNum();
        String type = "daoju";
        DrawRecord record = initRecordLog(roleName, uid, itemName, num);
        Long count = null;
        int totalCount = drawRecordService.getTotalCount(uid);
        try {
            GameAccount gameAccount = gameAccountService.selectGameAccountByName(uid);
            count = gameAccount.getCount();
            if (count == 0 && money < 50) {
                record.setStatus("failed,没有次数了!");
                return new AjaxResult(200, "请求成功！", new ItemBean(Math.toIntExact(drawItem.getLevel()), itemName, Math.toIntExact(count), money, totalCount));
            }
            Long version = gameAccount.getVersion();
            if (count > 0) {
                // 不消耗快乐币发送邮件
                String emailResult = sendEmail(item, uid, num, type);
                if ("ok！设置成功".equals(emailResult) || "发送成功".equals(emailResult)) {
                    int i = 0;
                    while (i == 0) {
                        // 并发版本控制
                        i = gameAccountService.updateGameAccountByVersion(--count, version, uid);
                    }
                }
            } else {
                // 1.减少快乐币
                money = money - 50;
                String params = "id=" + id + "&money=" + money;
                JSONObject result = JSONObject.parseObject(HttpUtils.sendGet(gameUrlUpdate, params + "&sign=" + SignUtils.getSign(params)));
                if (result.getInteger("code") == 200) {
                    sendEmail(item, uid, num, type);
                }
            }
            record.setStatus("ok");
        } catch (Exception e) {
            e.printStackTrace();
            record.setStatus("fail:" + e.getMessage());
        } finally {
            drawRecordService.insertDrawRecord(record);
        }
        return new AjaxResult(200, "请求成功！", new ItemBean(Math.toIntExact(drawItem.getLevel()), itemName+num, Math.toIntExact(count), money, totalCount));
    }

    /**
     * 发送每日福利
     *
     * @return
     */
    @GetMapping("/dayFuli")
    public AjaxResult getDayFuli() {
        SysUser user = getSysUser();
        String userName = user.getUserName();
        GameAccount gameAccount = gameAccountService.selectGameAccountByName(userName);
        String password = gameAccount.getPassword();
        String params = "qu=1&submit=oneday&user=" + userName + "&password=" + password;
        String result = HttpUtils.sendPost(gmUrl_day, params);
        result = result.substring(result.indexOf("script>") + 14, result.indexOf("</script>") - 2);
        return new AjaxResult(200, "success", result);
    }

    /**
     * 发送特殊福利
     *
     * 10连抽
     *
     * @return
     */
    @GetMapping("/holidayFuli")
    public synchronized AjaxResult holidayFuli() {
        SysUser user = getSysUser();
        String userName = user.getUserName();
        GameAccount gameAccount = gameAccountService.selectGameAccountByName(userName);

        String regId = gameAccount.getRegIp();
        if ("1".equals(regId)){
            return new AjaxResult(200,"success","时间到了吗，就领取，夜深了早点休息！");
        }
        if ("3".equals(regId)){
            return new AjaxResult(200,"success","福利已被领取，重复领取奖励华为P40一部吗？ 不可能~");
        }
        gameAccount.setRegIp("3");
        gameAccountService.updateGameAccount(gameAccount);
        StringBuilder sb = null;
        try {
            sb = new StringBuilder();
            StringBuilder finalSb = sb;
            holidayMap.forEach((item,num) ->{
                String emailResult = sendEmail(item, userName, num, "daoju");
                System.out.println(emailResult);
                finalSb.append( (item+num+"个，"));
            });

//            String moneySend = sendEmail("", userName, 10000, "charge");
//            System.out.println(moneySend);

        } catch (Exception e) {
            e.printStackTrace();
            // 异常回滚数据
            gameAccount.setRegIp("0");
            gameAccountService.updateGameAccount(gameAccount);
        }

        System.out.println(sb.toString());
        return new AjaxResult(200, "success", "福利领取成功，到游戏中查收邮件！");
    }

    public String sendEmail(String item, String uid, int num, String type) {
        // 发送邮件
        String paramEmail = "type=" + type + "&qu=1&checknum=952000&item=" + item + "&uid=" + uid + "&num=" + num;
        String sign = SignUtils.getSign(paramEmail);
        return HttpUtils.sendPost(GM_6_URL, paramEmail + "&sign=" + sign);
    }


    /**
     * 获取玩家主要信息
     * <p>
     * 获取抽奖物品信息
     *
     * @return
     */
//    @PreAuthorize("@ss.hasPermi('draw:getPlayerInfo')")
    @GetMapping("/getPlayerInfo")
    public AjaxResult getPlayerInfo() {
        SysUser user = getSysUser();
        String paramKey = "account=" + user.getUserName();
        String param = paramKey + "&sign=" + SignUtils.getSign(paramKey);
        JSONObject result = JSONObject.parseObject(HttpUtils.sendGet(gameUrl, param));
        JSONObject resultAjax = new JSONObject();
        // 查询物品列表
        List<DrawItem> drawItems = drawItemService.selectDrawItemList(new DrawItem());
        GameAccount gameAccount = gameAccountService.selectGameAccountByName(user.getUserName());
        int totalCount = drawRecordService.getTotalCount(user.getUserName());
        resultAjax.put("items", drawItems);
        resultAjax.put("count", gameAccount.getCount());
        if (result.getInteger("code") == 200) {
            RoleBean role = result.getJSONObject("data").toJavaObject(RoleBean.class);
            resultAjax.put("id", role.getRid());
            resultAjax.put("money", role.getGameFzb());
            resultAjax.put("roleName", role.getName());
            resultAjax.put("totalCount", totalCount);
            return new AjaxResult(200, "请求成功！", resultAjax);
        }

        return new AjaxResult(500, "请求失败！", resultAjax);
    }

    private SysUser getSysUser() {
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        SysUser user = loginUser.getUser();
        return user;
    }

    public static void main(String[] args) {
        String param = "account=hahayu" + "&sign=" + SignUtils.getSign("account=hahayu");
        String s = HttpUtils.sendGet("http://81.70.236.13:8090/role/findByAccount", param);
        System.out.println(s);
    }
}
