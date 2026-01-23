package cn.wekyjay.wknetic.auth.hook;


public interface ILoginHook {
    boolean isHooked();
    String getHookName();
}
