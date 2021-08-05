package com.touchealth.platform.processengine.service.user;

import com.touchealth.common.bo.core.UserBo;
import com.touchealth.platform.processengine.entity.user.User;
import com.touchealth.platform.processengine.pojo.bo.user.PostJobUserCountBo;
import com.touchealth.platform.processengine.pojo.bo.user.TokenBo;
import com.touchealth.platform.processengine.pojo.dto.BooleanDto;
import com.touchealth.platform.processengine.pojo.dto.PageData;
import com.touchealth.platform.processengine.pojo.dto.user.PermDto;
import com.touchealth.platform.processengine.pojo.dto.user.UserDto;
import com.touchealth.platform.processengine.pojo.request.user.LoginRequest;
import com.touchealth.platform.processengine.pojo.request.user.SignUpRequest;
import com.touchealth.platform.processengine.pojo.request.user.UserRequest;
import com.touchealth.platform.processengine.pojo.request.user.UserSearchRequest;
import com.touchealth.platform.processengine.service.BaseService;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface UserService extends BaseService<User> {

    /**
     * 创建管理后台用户
     * @param user
     */
    void createUser(User user);

    /**
     * 管理后台用户登录
     * @param email 账号
     * @param inputPassword 密码
     * @param userTypes 用户类型
     * @return
     */
    TokenBo login(String email, String inputPassword, Collection<Integer> userTypes);

    /**
     * 客户端用户登录
     * @param signUpRequest 登录请求参数
     * @return
     */
    TokenBo signUp(SignUpRequest signUpRequest);

    User findByEmail(String email, int userType);

    User findByEmail(String email, List<Integer> userTypes);

    List<User> listByEmail(List<String> emails, int userType);

    List<User> listByMobile(List<String> mobileList, List<Integer> userTypes);

    User findById(Long userId);

    UserBo findByMobileAndType(String mobile,int userType);

    List<User> findByMobileAndType(String mobile, List<Integer> userTypes);

    /**
     * 根据手机号获取登录验证码
     * @param mobile 手机号
     * @param bgUser 是否为后台用户
     */
    void getVerificationCode(String mobile, Boolean bgUser);

    /**
     * 根据手机号获取用户信息
     * @param mobile 手机号
     * @param userTypes 用户类型
     * @return
     */
    List<User> findUserByMobileAndUserTypes(String mobile, List<Integer> userTypes);

    /**
     * 用户登录
     * @param request 登录请求参数
     * @return
     */
    TokenBo login(LoginRequest request);

    /**
     * 根据用户ID获取对应的有效token
     * @param userId 用户ID
     * @return
     */
    String getUserTicket(Long userId);

    PageData<UserDto> pageList(UserSearchRequest req);

    /**
     * 查询岗位人数
     * @param postJobIdList
     * @return
     */
    List<PostJobUserCountBo> countByPostJobIds(List<Long> postJobIdList);

    /**
     * 更新用户权限。<br>
     * <i>这里会先删除全部的用户权限，然后再全部新增，这样的好处是可以方便恢复上次的权限，弊处就是逻辑删除数据会变多，
     * 不过该功能不是常用功能，而且后期数据过大可以使用定时任务定时物理删除历史数据。</i>
     *
     * @param chooseChannelNo
     * @param userId
     * @param postJobIds
     * @param permList
     * @return
     */
    Boolean updatePerms(String chooseChannelNo, Long userId, List<Long> postJobIds, List<PermDto> permList);

    UserDto getUserDto(User user);
    UserBo updateUser(UserBo userBo);

    /**
     * 校验token
     * @param token
     * @return 如果校验不通过，这里返回null；否则返回token中包含的用户信息
     */
    Map<String, String> validToken(String token);

    /**
     * 添加用户并授予岗位权限
     * @param user
     * @return
     */
    boolean saveAndAuth(User user);

    /**
     * 批量添加用户并授予岗位权限
     * @param saveOrUpdUserList
     * @return
     */
    boolean saveOrUpdateBatchAndAuth(List<User> saveOrUpdUserList);

    /**
     * 修改用户并重新授予岗位权限，如果岗位有变更
     * @param req
     * @param userId
     * @return
     */
    BooleanDto updateStaffAndAuth(UserRequest req, Long userId);

    /**
     * 删除用户并刷新缓存
     * @param id
     * @return
     */
    boolean deleteAndFlushCache(Long id);
}
