package cn.wekyjay.wknetic.auth.hook;

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
