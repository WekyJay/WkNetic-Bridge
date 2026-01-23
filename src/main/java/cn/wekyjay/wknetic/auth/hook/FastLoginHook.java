package cn.wekyjay.wknetic.auth.hook;

public class FastLoginHook implements ILoginHook {
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

}
