package com.touchealth.platform.processengine.controller.page;

import com.alibaba.fastjson.JSON;
import com.touchealth.platform.processengine.constant.CommonConstant;
import com.touchealth.platform.processengine.constant.PageCenterConsts;
import com.touchealth.platform.processengine.entity.module.common.PageTemplate;
import com.touchealth.platform.processengine.entity.page.PageManager;
import com.touchealth.platform.processengine.entity.page.PageModule;
import com.touchealth.platform.processengine.entity.page.PlatformChannel;
import com.touchealth.platform.processengine.entity.page.PlatformVersion;
import com.touchealth.platform.processengine.entity.user.User;
import com.touchealth.platform.processengine.pojo.dto.Response;
import com.touchealth.platform.processengine.pojo.dto.page.PageDetailsDto;
import com.touchealth.platform.processengine.pojo.dto.page.PageManagerDto;
import com.touchealth.platform.processengine.pojo.dto.page.PageModuleDto;
import com.touchealth.platform.processengine.pojo.dto.page.PageTemplateDto;
import com.touchealth.platform.processengine.pojo.query.PageManagerQuery;
import com.touchealth.platform.processengine.pojo.request.page.ApplyPageTemplateRequest;
import com.touchealth.platform.processengine.pojo.request.page.PageManagerRequest;
import com.touchealth.platform.processengine.pojo.request.page.PageManagerUpdateRequest;
import com.touchealth.platform.processengine.pojo.request.page.PageModuleRequest;
import com.touchealth.platform.processengine.service.common.RedisService;
import com.touchealth.platform.processengine.service.module.common.PageTemplateService;
import com.touchealth.platform.processengine.service.page.PageManagerService;
import com.touchealth.platform.processengine.service.page.PageModuleService;
import com.touchealth.platform.processengine.service.page.PlatformChannelService;
import com.touchealth.platform.processengine.service.page.PlatformVersionService;
import com.touchealth.platform.processengine.service.user.UserService;
import com.touchealth.platform.processengine.utils.BaseHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.touchealth.platform.processengine.constant.PageCenterConsts.ROUTER_NAME_HOME;
import static com.touchealth.platform.processengine.constant.RedisConstant.PageCenter.*;

/**
 * 页面管理
 *
 * @author liufengqiang
 * @date 2020-11-13 14:08:53
 */
@RestController
@RequestMapping("/page-manager")
@Slf4j
public class PageManagerController {

    @Resource
    private PageManagerService pageManagerService;
    @Resource
    private PlatformVersionService platformVersionService;
    @Resource
    private PageModuleService pageModuleService;
    @Resource
    private RedisService redisService;
    @Resource
    private UserService userService;
    @Resource
    private PlatformChannelService platformChannelService;
    @Resource
    private PageTemplateService pageTemplateService;

    /**
     * 页面列表
     *
     * @param versionId  版本号 没传默认为最新草稿版本
     * @param queryType  查询类型 0/空.默认 1.仅显示改动项
     * @param moduleType 组件类型
     */
    @GetMapping
    public Response list(@RequestHeader String channelNo, @RequestParam(defaultValue = "0") Integer queryType,
                         Long versionId, Integer businessType, Integer moduleType) {
        return Response.ok(pageManagerService.pageList(channelNo, queryType, versionId, businessType, moduleType));
    }

    /**
     * 新增页面
     */
    @PostMapping
    public Response addPage(@RequestAttribute Long userId, @RequestHeader String channelNo, @RequestBody @Valid PageManagerRequest request) {
        PlatformChannel platformChannel = platformChannelService.getByChannelNo(channelNo);
        Assert.notNull(platformChannel, "平台不存在");

        Long versionId = versionVerify(channelNo, request.getVersionId());

        if (request.getFolderId() != null) {
            Assert.isTrue(!request.getIsFolder(), "文件夹不能有多级");
            PageManager pageManager = pageManagerService.getById(request.getFolderId());
            Assert.notNull(pageManager, "文件夹不存在");
            Assert.isTrue(pageManager.getIsFolder(), "所属非文件夹");
        }

        PageManager pageManager = BaseHelper.r2t(request, PageManager.class);
        pageManager.setChannelNo(channelNo);
        pageManager.setVersionId(versionId);
        pageManager.setOperatorIds(String.valueOf(userId));
        pageManagerService.savePage(userId, pageManager);
        return Response.ok(BaseHelper.r2t(pageManager, PageManagerDto.class));
    }

    /**
     * 编辑页面
     * 三种情况：编辑页面名称、拖拽位置、拖到文件夹里
     */
    @PutMapping("/{id}")
    public Response update(@RequestAttribute Long userId, @RequestHeader String channelNo,
                           @PathVariable Long id, @RequestBody PageManagerUpdateRequest request) {
        PageManager pageManager = pageVerify(userId, channelNo, id);

        if (request.getFolderId() != null) {
            PageManager folderPage = pageManagerService.getById(request.getFolderId());
            Assert.isTrue(folderPage.getIsFolder(), "不能拖到非文件夹");
        }

        if (StringUtils.isNotBlank(request.getPageName())) {
            Assert.isTrue(PageCenterConsts.BusinessType.COMMON.getCode().equals(pageManager.getBusinessType()), "业务类型不能修改页面名");
            pageManagerService.updateByName(userId, pageManager, request.getPageName());
        } else {
            // 重排序
            List<PageManager> pageManagers = pageManagerService.listByVersionId(pageManager.getVersionId(), false, null);

            // 拖拽页面
            int swap0 = 0;
            // 拖拽目的
            Integer swap1 = null;
            for (int i = 0; i < pageManagers.size(); i++) {
                if (pageManagers.get(i).getId().equals(id)) {
                    swap0 = i;
                }
                if (pageManagers.get(i).getId().equals(request.getLastId())) {
                    swap1 = i;
                }
            }

            PageManager addPage = pageManagers.get(swap0);
            addPage.setFolderId(request.getFolderId());

            if (swap1 != null && swap0 < swap1) {
                pageManagers.add(swap1 + 1, addPage);
                pageManagers.remove(swap0);
            } else {
                pageManagers.add(swap1 == null ? 0 : swap1 + 1, addPage);
                pageManagers.remove(swap0 + 1);
            }

            Collections.reverse(pageManagers);
            pageManagers.forEach(o -> {
                o.setSortNo(System.currentTimeMillis());
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            pageManagerService.updateBatchById(pageManagers);

            // 更新所属文件夹
            pageManagerService.updateFolder(pageManager, request.getFolderId());

            // 更新草稿版本时间
            platformVersionService.updateVersionTime(pageManager.getVersionId());
        }
        return Response.success;
    }

    public PageManager pageVerify(Long userId, String channelNo, Long pageId) {
        PageManager pageManager = pageManagerService.getById(pageId);
        Assert.notNull(pageManager, "页面不存在");

        versionVerify(channelNo, pageManager.getVersionId());

        String lockUserId = redisService.getValue(getPageLockKeyByPageId(pageId));
        if (StringUtils.isNotBlank(lockUserId)) {
            Assert.isTrue(String.valueOf(userId).equals(lockUserId), "此页面其他人正在编辑中");
        }
        return pageManager;
    }

    private Long versionVerify(String channelNo, Long versionId) {
        PlatformVersion platformVersion = platformVersionService.getDefaultVersion(channelNo, null);
        Assert.isTrue(platformVersion.getId().equals(versionId), "当前版本已发布，请刷新页面");
        Assert.isTrue(!PageCenterConsts.VersionStatus.LOCK.getCode().equals(platformVersion.getStatus()),
                "当前版本已进入锁定状态，不能编辑");
        return platformVersion.getId();
    }

    /**
     * 页面解锁
     */
    @PutMapping("/{pageId}/unlock")
    public Response unlock(@RequestAttribute Long userId, @PathVariable Long pageId) {
        String pageLockId = redisService.getValue(getPageLockKeyByUserId(userId));
        if (String.valueOf(pageId).equals(pageLockId)) {
            redisService.del(getPageLockKeyByUserId(userId));
            redisService.del(getPageLockKeyByPageId(Long.parseLong(pageLockId)));
        }
        return Response.success;
    }

    /**
     * 获取当前渠道routerName对应页面详情
     * 正常只有一个页面，万一有多个取第一个
     */
    @GetMapping("/details")
    public Response pageDetails(@RequestHeader String channelNo, @RequestParam String routerName, Long versionId) {
        PageManager pageManager = pageManagerService.getByRouterName(routerName, channelNo, versionId);
        return Response.ok(BaseHelper.r2t(pageManager, PageDetailsDto.class));
    }

    /**
     * 页面详情
     */
    @GetMapping("/{pageId}")
    public Response details(@RequestHeader String channelNo, @PathVariable String pageId, Long versionId, HttpServletRequest request) {
        PageDetailsDto dto = new PageDetailsDto();
        PlatformChannel platformChannel = platformChannelService.getByChannelNo(channelNo);
        Assert.notNull(platformChannel, "平台不存在");
        dto.setChannelName(platformChannel.getChannelName());

        PlatformVersion platformVersion = platformVersionService.getDefaultVersion(channelNo, versionId);
        PageManager pageManager = pageManagerService.getById(Long.parseLong(pageId));
        Assert.notNull(pageManager, "页面不存在");

        // PC端判断页面编辑锁定
        if (platformVersion.getId().equals(pageManager.getVersionId())
                && CommonConstant.STATUS.DRAFT.getCode().equals(platformVersion.getStatus())) {
            Long userId = (Long) request.getAttribute("userId");

            // 用户之前锁定的页面
            String pageLockId = redisService.getValue(getPageLockKeyByUserId(userId));
            if (StringUtils.isNotBlank(pageLockId)) {
                redisService.del(getPageLockKeyByUserId(userId));
                redisService.del(getPageLockKeyByPageId(Long.parseLong(pageLockId)));
            }

            // 页面对应的锁定用户
            String lockUserId = redisService.getValue(getPageLockKeyByPageId(pageManager.getId()));
            if (StringUtils.isBlank(lockUserId)) {
                redisService.setValue(getPageLockKeyByPageId(pageManager.getId()), String.valueOf(userId), PAGE_CENTER_PAGE_LOCK_TIME);
                redisService.setValue(getPageLockKeyByUserId(userId), String.valueOf(pageManager.getId()), PAGE_CENTER_PAGE_LOCK_TIME);
            } else if (!userId.equals(Long.parseLong(lockUserId))) {
                User user = userService.getById(Long.parseLong(lockUserId));
                dto.setNotEditReason(user.getRealName() + "编辑中");
                dto.setAvatar(user.getAvatar());
                dto.setRealName(user.getRealName());
            }
        }

        // 判断是否发布中
        if (PageCenterConsts.VersionStatus.LOCK.getCode().equals(platformVersion.getStatus())) {
            dto.setNotEditReason("正在发布审核不可编辑");
            User user = userService.getById(platformVersion.getAuthUserId());
            if (user != null) {
                dto.setAvatar(user.getAvatar());
                dto.setRealName(user.getRealName());
            }
        }

        dto.setBusinessType(PageCenterConsts.BusinessType.getNameByCode(pageManager.getBusinessType()));
        dto.setRouterName(pageManager.getRouterName());
        dto.setPageUniqueId(pageManager.getPageUniqueId());
        dto.setPageName(pageManager.getPageName());

        List<PageModule> pageModules = pageModuleService.listByPageId(pageManager.getId());
        dto.setModules(pageModules.stream().map(o -> {
            PageModuleDto pageModuleDto = new PageModuleDto();
            pageModuleDto.setId(o.getId());
            pageModuleDto.setModuleType(o.getModuleType());
            pageModuleDto.setWebJson(JSON.parseObject(o.getWebJson()));
            return pageModuleDto;
        }).collect(Collectors.toList()));

        // 是pc端并且可以配置模板时，返回对应的模板信息（目前只有首页）
        if (pageManager.getIsSupportTemplate()) {
            if (ROUTER_NAME_HOME.equals(pageManager.getRouterName())) {
                List<PageTemplate> templateList = pageTemplateService.listByType(ROUTER_NAME_HOME);
                dto.setPageTemplates(BaseHelper.r2t(templateList, PageTemplateDto.class));
            }
        }
        return Response.ok(dto);
    }

    /**
     * 页面预览 页面30分钟可见
     */
    @PostMapping("/preview")
    public Response preview(@RequestHeader String channelNo, Long versionId) {
        versionId = platformVersionService.getDefaultVersionId(channelNo, versionId);
        redisService.setValue(getPagePreviewKey(channelNo, versionId), true, PAGE_CENTER_PAGE_PREVIEW_TIME);

        PageManagerQuery query = new PageManagerQuery();
        query.setVersionId(versionId);
        query.setIsFolder(false);
        List<PageManager> pageManagers = pageManagerService.listByParam(query);
        if (CollectionUtils.isNotEmpty(pageManagers)) {
            PageManagerDto dto = BaseHelper.r2t(pageManagers.get(0), PageManagerDto.class);
            dto.setBusinessType(PageCenterConsts.BusinessType.getNameByCode(pageManagers.get(0).getBusinessType()));
            dto.setRouterName(pageManagers.get(0).getRouterName());
            return Response.ok(dto);
        }
        return Response.success;
    }

    /**
     * 删除页面/文件夹（进入回收站）
     */
    @DeleteMapping("/{id}")
    public Response delete(@RequestHeader String channelNo, @RequestAttribute Long userId, @PathVariable Long id) {
        PageManager pageManager = pageVerify(userId, channelNo, id);
        Assert.isTrue(!pageManager.getIsSign(), "当前页面禁止删除！");
        pageManagerService.recycleBin(userId, id);
        return Response.success;
    }

    /**
     * 恢复页面/文件夹
     */
    @PutMapping("/{id}/restore")
    public Response restore(@RequestHeader String channelNo, @RequestAttribute Long userId, @PathVariable Long id) {
        pageVerify(userId, channelNo, id);
        pageManagerService.restore(channelNo, userId, id);
        return Response.success;
    }

    /**
     * 新增组件
     */
    @PostMapping("/{pageId}/module")
    public Response addModule(@RequestHeader String channelNo, @RequestAttribute Long userId,
                              @PathVariable Long pageId, @RequestBody PageModuleRequest request) {
        PageManager pageManager = pageVerify(userId, channelNo, pageId);
        PageModule pageModule = pageModuleService.saveModule(userId, pageManager, request);
        PageModuleDto pageModuleDto = BaseHelper.r2t(pageModule, PageModuleDto.class);
        pageModuleDto.setWebJson(JSON.parseObject(pageModule.getWebJson()));
        return Response.ok(pageModuleDto);
    }

    /**
     * 编辑组件
     */
    @PutMapping("/module/{id}")
    public Response module(@RequestHeader String channelNo, @RequestAttribute Long userId,
                           @PathVariable Long id, @RequestBody PageModuleRequest request) {
        PageModule pageModule = pageModuleService.getById(id);
        Assert.notNull(pageModule, "组件不存在");
        pageVerify(userId, channelNo, pageModule.getPageId());
        String webJson = pageModuleService.updateModule(userId, pageModule, request);

        PageModuleDto pageModuleDto = BaseHelper.r2t(pageModule, PageModuleDto.class);
        pageModuleDto.setWebJson(JSON.parseObject(webJson));
        return Response.ok(pageModuleDto);
    }

    /**
     * 拖拽组件
     */
    @PutMapping("/module/{moduleId}/drag")
    public Response drag(@RequestHeader String channelNo, @RequestAttribute Long userId,
                         @PathVariable Long moduleId, @RequestBody PageModuleRequest request) {
        PageModule pageModule = pageModuleService.getById(moduleId);
        Assert.notNull(pageModule, "组件不存在");
        pageVerify(userId, channelNo, pageModule.getPageId());

        List<PageModule> pageModules = pageModuleService.listByPageId(pageModule.getPageId());
        Assert.isTrue(CollectionUtils.isNotEmpty(pageModules), "组件不能为空");

        // 拖拽页面
        int swap0 = 0;
        // 拖拽目的
        Integer swap1 = null;
        for (int i = 0; i < pageModules.size(); i++) {
            if (pageModules.get(i).getId().equals(moduleId)) {
                swap0 = i;
            }
            if (pageModules.get(i).getId().equals(request.getLastId())) {
                swap1 = i;
            }
        }

        PageModule addModule = pageModules.get(swap0);
        if (swap1 != null && swap0 < swap1) {
            pageModules.add(swap1 + 1, addModule);
            pageModules.remove(swap0);
        } else {
            pageModules.add(swap1 == null ? 0 : swap1 + 1, addModule);
            pageModules.remove(swap0 + 1);
        }
        Collections.reverse(pageModules);
        pageModules.forEach(o -> {
            o.setSortNo(System.currentTimeMillis());
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        pageModuleService.updateBatchById(pageModules);

        // 更新草稿版本时间
        platformVersionService.updateVersionTime(pageModule.getVersionId());
        return Response.success;
    }

    /**
     * 删除组件
     */
    @DeleteMapping("/module/{moduleId}")
    public Response deleteModule(@RequestHeader String channelNo, @RequestAttribute Long userId, @PathVariable Long moduleId) {
        PageModule pageModule = pageModuleService.getById(moduleId);
        Assert.notNull(pageModule, "组件不存在");
        PageManager pageManager = pageVerify(userId, channelNo, pageModule.getPageId());
        Assert.isTrue(!pageManager.getIsSign(), "当前组件禁止删除！");
        pageModuleService.deleteModule(userId, moduleId);
        return Response.success;
    }

    /**
     * 页面应用模板内容
     *
     * @param pageId 页面id
     * @param userId 用户id
     * @return Response
     */
    @PostMapping("/{pageId}/applyTemplate")
    public Response update(@PathVariable Long pageId, @RequestAttribute Long userId, @RequestHeader String channelNo,
                           @RequestBody @Valid ApplyPageTemplateRequest request) {
        //校验页面编辑信息权限
        PageManager pageManager = pageVerify(userId, channelNo, pageId);
        //获取对应的模板内容
        PageTemplate pageTemplate = pageTemplateService.getById(request.getTemplateId());
        Assert.notNull(pageTemplate, "找不到对应的模板");
        pageManager.setUpdatedBy(userId);
        pageManagerService.applyPageTemplate(pageManager, pageTemplate);
        return Response.success;
    }
}
