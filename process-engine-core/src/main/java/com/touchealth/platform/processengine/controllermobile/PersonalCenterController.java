package com.touchealth.platform.processengine.controllermobile;

import com.touchealth.api.core.bo.SysBrowsingHistoryBO;
import com.touchealth.api.core.bo.SysCollectionBO;
import com.touchealth.api.core.constant.CollectionEnum;
import com.touchealth.api.core.service.SysBrowsingHistoryService;
import com.touchealth.api.core.service.SysCollectionService;
import com.touchealth.api.core.service.UserService;
import com.touchealth.api.file.bo.DownloadUrlBo;
import com.touchealth.api.file.service.DownloadService;
import com.touchealth.api.ops.service.HospitalService;
import com.touchealth.api.ops.service.SetMealHospitalService;
import com.touchealth.common.bo.core.UserBo;
import com.touchealth.common.bo.ops.HospitalBo;
import com.touchealth.common.bo.ops.SetMealHospitalBo;
import com.touchealth.platform.marketing.client.constant.MarketingConstant;
import com.touchealth.platform.marketing.client.service.QuestionAndAnswerApi;
import com.touchealth.platform.processengine.pojo.dto.Response;
import com.touchealth.platform.processengine.pojo.dto.user.PersonalCenterDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Description:个人中心控制器
 *
 * @author lvx
 * @date 2021/1/15
 */
@RestController
@RequestMapping("/mobile/personalCenter")
@Slf4j
public class PersonalCenterController {

    @Resource
    private UserService userCoreService;

    @Resource
    private SysCollectionService sysCollectionService;

    @Resource
    private SysBrowsingHistoryService sysBrowsingHistoryService;

    @Resource
    private QuestionAndAnswerApi questionAndAnswerApi;

    @Resource
    private DownloadService downloadService;

    @Resource
    private SetMealHospitalService setMealHospitalService;

    @Resource
    private HospitalService hospitalService;

    /**
     * 根据pageId 查询页码埋点相关数据
     * @param userId 用户ID
     * @return 页码埋点数据
     */
    @GetMapping("/getInfo")
    public Response getPageDataStatistics(@RequestAttribute(name = "userId", required = false) Long userId) {
        PersonalCenterDto dto = new PersonalCenterDto();
        dto.setAccountBalance(BigDecimal.ZERO);
        dto.setBrowsingHistoryNum(0);
        dto.setCardVoucherNum(0);
        dto.setCollectionGoodNum(0);
        dto.setCollectionHospitalNum(0);
        dto.setQuestionAndAnswerNum(0);
        dto.setIntegrateNum(0);
        dto.setCouponsNum(0);

        UserBo user = userCoreService.findUserById(userId);
        if (user != null) {
            dto.setUserName(user.getNickName());
            dto.setMobileNo(user.getMobileNo());

            if(StringUtils.isNotBlank(user.getAvatar())){
                DownloadUrlBo file = downloadService.getDownloadUrl(Long.valueOf(user.getAvatar()));
                if (file != null) {
                    dto.setHeadPicUrl(file.getUrl());
                }
            }

            List<SysCollectionBO> setMealCollectionList = sysCollectionService.findAllByUserId(CollectionEnum.SET_MEAL.getCode(),userId);
            List<Long> setMealIds = setMealCollectionList.stream().map(SysCollectionBO::getRelationKey).distinct().collect(Collectors.toList());
            Map<Long, Long> setMealLongMap = setMealCollectionList.stream().collect(Collectors.toMap(SysCollectionBO::getId, SysCollectionBO::getRelationKey));
            List<SysBrowsingHistoryBO> browsingHistoryList = sysBrowsingHistoryService.findAllByUser(userId);
            List<Long> browsingSetMealIds = browsingHistoryList.stream().map(SysBrowsingHistoryBO::getRelationKey).distinct().collect(Collectors.toList());
            Map<Long, Long> browsingLongLongMap = browsingHistoryList.stream().collect(Collectors.toMap(SysBrowsingHistoryBO::getId, SysBrowsingHistoryBO::getRelationKey));

            int hospitalNum = sysCollectionService.findCountByUserIdAndRelationType(userId, CollectionEnum.HSP.getCode());
            int questionAndAnswerNum = questionAndAnswerApi
                    .countMyQuestionAndAnswer(userId,
                            Arrays.asList(MarketingConstant.RELATION_TYPE.HOSPITAL.getCode(), MarketingConstant.RELATION_TYPE.SET_MEAL.getCode()))
                    .getDataOrThrowError();

            dto.setCollectionGoodNum(toSetMealPager(setMealIds,setMealLongMap));
            dto.setCollectionHospitalNum(hospitalNum);
            dto.setQuestionAndAnswerNum(questionAndAnswerNum);
            dto.setBrowsingHistoryNum(toSetMealPager(browsingSetMealIds,browsingLongLongMap));
        }

        return Response.ok(dto);
    }

    private Integer toSetMealPager(List<Long> setMealIds, Map<Long,Long> longLongMap) {
        List<SetMealHospitalBo> mealHospitalBoList = setMealHospitalService.findByIdIn(setMealIds);
        Map<Long, SetMealHospitalBo> boMap = mealHospitalBoList.stream().collect(Collectors.toMap(SetMealHospitalBo::getId, e -> e));
        List<Long> longList = mealHospitalBoList.stream().map(SetMealHospitalBo::getHospitalId).distinct().collect(Collectors.toList());
        List<HospitalBo> hospitalBoList = hospitalService.findHospitalByIdIn(longList);
        Map<Long, HospitalBo> hospitalBoMap = hospitalBoList.stream().collect(Collectors.toMap(HospitalBo::getId, e -> e));
        AtomicInteger num = new AtomicInteger();
        longLongMap.forEach((k,v)->{
            SetMealHospitalBo setMeal = boMap.get(v);
            if (null != setMeal) {
                HospitalBo hospitalBo = hospitalBoMap.get(setMeal.getHospitalId());
                if (null != hospitalBo) {
                    num.getAndIncrement();
                }
            }
        });

        return num.get();
    }

}
