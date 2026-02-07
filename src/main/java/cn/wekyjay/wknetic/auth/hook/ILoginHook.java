package cn.wekyjay.wknetic.auth.hook;


/**
 * 登录钩子接口，定义了登录钩子的基本行为
 */
public interface ILoginHook {
    boolean isHooked();
    boolean registerEvents();
    String getHookName();
}
