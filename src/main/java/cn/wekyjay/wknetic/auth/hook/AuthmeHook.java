package cn.wekyjay.wknetic.auth.hook;

import org.bukkit.Bukkit;

public class AuthmeHook implements ILoginHook {

    public AuthmeHook() {
        if (isHooked()) {
            Bukkit.getLogger().info("AuthMe hook initialized.");
        }
    }

    @Override
    public boolean isHooked() {
        try {
            Class.forName("fr.xephi.authme.AuthMe");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    public String getHookName() {
        return "AuthMe";
    }

}
