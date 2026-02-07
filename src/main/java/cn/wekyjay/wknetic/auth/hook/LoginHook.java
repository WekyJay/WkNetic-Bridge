package cn.wekyjay.wknetic.auth.hook;

/**
 * 抽象登录钩子类，实现了 ILoginHook 接口的基本功能
 */
public abstract class LoginHook implements ILoginHook {
    public LoginHook() {
        if (isHooked()) {
            LoginHooker.getRegisteredhooks().put(getHookName(), this);
        }
    }

    @Override
    public boolean registerEvents() {
        return true;
    }

    @Override
    public abstract boolean isHooked();

    @Override
    public abstract String getHookName();

}
