package cn.wekyjay.wknetic.auth.hook;

public class CustomLoginHook implements ILoginHook {
    @Override
    public boolean isHooked() {
        // 总是可用，作为兜底
        return true;
    }

    @Override
    public String getHookName() {
        return "CustomLogin";
    }

    // TODO: 实现自己的登录验证逻辑
}