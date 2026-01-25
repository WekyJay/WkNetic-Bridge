package cn.wekyjay.wknetic.auth;

import cn.wekyjay.wknetic.auth.hook.AuthmeHook;
import cn.wekyjay.wknetic.auth.hook.CustomLoginHook;
import cn.wekyjay.wknetic.auth.hook.FastLoginHook;
import cn.wekyjay.wknetic.auth.hook.ILoginHook;
import cn.wekyjay.wknetic.auth.hook.LoginHooker;

import org.bukkit.Bukkit;
import cn.wekyjay.wknetic.bridge.WkNeticBridge;

public class LoginAuthManager {
    private final ILoginHook loginHook;

    public LoginAuthManager() {

        if (Bukkit.getServer().getOnlineMode()) {
            loginHook = null; // 正版模式，不需要登录验证
            WkNeticBridge.getInstance().getLogger().info("Server is in online-mode, no login verification needed.");
        } else {
            AuthmeHook authmeHook = (AuthmeHook)LoginHooker.getHookByName("AuthMe");
            if (authmeHook != null && authmeHook.isHooked()) {
                loginHook = authmeHook;
                authmeHook.registerEvents();
                WkNeticBridge.getInstance().getLogger().info("[加入监听器]AuthMe hook detected and events registered.");
            } else {
                loginHook = new CustomLoginHook();
                WkNeticBridge.getInstance().getLogger().info("Using CustomLogin for login verification.");
            }
            
        }
    }

    public ILoginHook getLoginHook() {
        return loginHook;
    }

    public boolean hasLoginHook() {
        return loginHook != null;
    }

    public String getLoginHookName() {
        return loginHook != null ? loginHook.getHookName() : null;
    }
}