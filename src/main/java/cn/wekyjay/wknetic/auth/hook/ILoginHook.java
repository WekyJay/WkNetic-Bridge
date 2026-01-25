package cn.wekyjay.wknetic.auth.hook;



public interface ILoginHook {
    boolean isHooked();
    boolean registerEvents();
    String getHookName();
}
