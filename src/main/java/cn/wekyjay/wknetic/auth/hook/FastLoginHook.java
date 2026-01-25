package cn.wekyjay.wknetic.auth.hook;

public class FastLoginHook extends LoginHook {


    @Override
    public boolean isHooked() {
        try {
            Class.forName("com.github.games647.fastlogin.bukkit.FastLoginBukkit");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    public String getHookName() {
        return "FastLogin";
    }

    @Override
    public boolean registerEvents() {
        // FastLogin 不需要额外注册事件
        return true;
    }

}
