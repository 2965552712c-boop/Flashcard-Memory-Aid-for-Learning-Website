package com.ch.zishan.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ch.zishan.common.BaseContext;
import com.ch.zishan.common.Result;
import com.ch.zishan.pojo.CardGroup;
import com.ch.zishan.service.*;
import com.ch.zishan.utils.SysUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@CrossOrigin
@RestController
@Transactional
@RequestMapping("/cardGroup")
public class CardGroupController {
    @Resource
    private CardGroupService cardGroupService;
    @Resource
    private CardService cardService;
    @Resource
    private ChapterService chapterService;
    @Resource
    private CollectService collectService;
    @Resource
    private LearnedCardGroupService learnedCardGroupService;

    @GetMapping("/collect/{id}")
    public Result<CardGroup> getCardGroupById(@PathVariable Long id) {
        log.info("查询卡片集，id：" + id);
        if (id == null) {
            return Result.error("id不能为空");
        }
        CardGroup group = cardGroupService.getCardGroupById(id);
        if (group == null) {
            return Result.error("卡片集不存在");
        }
        if (group.getIsDeleted() == 1 || group.getIsPublic() == 0) {
            return Result.error("卡片集不存在");
        }

        return Result.success(group);
    }

    @GetMapping("/search/{key}")
    public Result<List<CardGroup>> search(@PathVariable String key) {
        log.info("搜索卡片集，关键字：" + key);
        if (StringUtils.isBlank(key)) {
            return Result.error("关键字不能为空");
        }
        List<CardGroup> list = cardGroupService.search(key);
        return Result.success(list);
    }

    @PutMapping("/open")
    public Result<String> isPublic(@RequestBody CardGroup cardGroup) {
        log.info("设置卡片集是否公开，id：" + cardGroup.getId());
        // 只有创建卡片集者才可以设置
        CardGroup group = cardGroupService.getById(cardGroup.getId());

        if (group == null) {
            return Result.error("卡片集不存在");
        }

        if (!SysUtils.checkUser(BaseContext.get(), group.getCreateUser())) {
            return Result.error("无权限设置");
        }

        UpdateWrapper<CardGroup> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", cardGroup.getId())
                .set("is_public", cardGroup.getIsPublic());
        cardGroupService.update(updateWrapper);
        log.info("设置卡片集是否公开成功，id：" + cardGroup.getId());
        return Result.success("设置成功");
    }

    @PutMapping("/recover")
    public Result<String> recoverCardGroup(@RequestBody CardGroup cardGroup) {
        log.info("恢复卡片集，id：" + cardGroup.getId());

        // 只有创建卡片集者才可以恢复
        QueryWrapper<CardGroup> wrapper = new QueryWrapper<>();
        wrapper.eq("id", cardGroup.getId()).eq("is_deleted", 1);
        CardGroup group = cardGroupService.getOne(wrapper);
        if (!SysUtils.checkUser(BaseContext.get(), group.getCreateUser())) {
            return Result.error("无权限恢复");
        }

        cardGroupService.recoverCardGroup(cardGroup.getId());
        return Result.success("恢复成功");
    }

    @DeleteMapping
    public Result<String> deleteCardGroup(@RequestParam Long id) {
        log.info("删除卡片集，id：" + id);
        // 只有创建卡片集者才可以删除
        CardGroup cardGroup = cardGroupService.getById(id);

        if (cardGroup == null) {
            return Result.error("卡片集不存在");
        }

        if (!SysUtils.checkUser(BaseContext.get(), cardGroup.getCreateUser())) {
            return Result.error("无权限删除");
        }
        cardGroupService.deleteCardGroup(id);
        log.info("删除卡片集成功，id：" + id);
        return Result.success("删除成功");
    }

    @DeleteMapping("/all")
    public Result<String> allDeleteCardGroup(@RequestParam Long id) {
        log.info("永久删除卡片集，id：" + id);
        // 只有创建卡片集者才可以删除
        CardGroup cardGroup = cardGroupService.getById(id);

        if (cardGroup == null) {
            return Result.error("卡片集不存在");
        }

        if (!SysUtils.checkUser(BaseContext.get(), cardGroup.getCreateUser())) {
            return Result.error("无权限删除");
        }
        cardGroupService.allDeleteCardGroup(id);
        log.info("永久删除卡片集成功，id：" + id);
        return Result.success("删除成功");
    }

    @PutMapping
    // 更改卡片集名称
    public Result<String> updateCardGroup(@RequestBody CardGroup cardGroup) {
        log.info("更新卡片集，id：" + cardGroup.getId());

        // 只有创建卡片集者才可以修改
        CardGroup group = cardGroupService.getById(cardGroup.getId());

        if (group == null) {
            return Result.error("卡片集不存在");
        }

        if (!SysUtils.checkUser(BaseContext.get(), group.getCreateUser())) {
            return Result.error("无权限恢复");
        }

        UpdateWrapper<CardGroup> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", cardGroup.getId())
                .set("name", cardGroup.getName());

        cardGroupService.update(wrapper);
        log.info("更新卡片集成功，id：" + cardGroup.getId());
        return Result.success("更新成功");
    }

    @PostMapping
    public Result<Long> addCardGroup() {
        log.info("添加卡片集");
        CardGroup cardGroup = new CardGroup();
        cardGroup.setName("无标题卡片集");
        cardGroup.setUser(BaseContext.get());
        cardGroupService.addCardGroup(cardGroup);
        log.info("添加卡片集成功，id：" + cardGroup.getId());
        return Result.success(cardGroup.getId());
    }
}
