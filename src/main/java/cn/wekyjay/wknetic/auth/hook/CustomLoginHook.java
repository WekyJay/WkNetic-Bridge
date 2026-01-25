package cn.wekyjay.wknetic.auth.hook;

public class CustomLoginHook extends LoginHook {
    @Override
    public boolean isHooked() {
        // 总是可用，作为兜底
        return true;
    }

    @Override
    public String getHookName() {
        return "CustomLogin";
    }


    @Override   
    public boolean registerEvents() {
        // CustomLogin 不需要额外注册事件
        return true;
    }
    // TODO: 实现自己的登录验证逻辑
}