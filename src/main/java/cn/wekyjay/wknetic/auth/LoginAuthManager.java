package cn.wekyjay.wknetic.auth;

import cn.wekyjay.wknetic.auth.hook.AuthmeHook;
import cn.wekyjay.wknetic.auth.hook.CustomLoginHook;
import cn.wekyjay.wknetic.auth.hook.FastLoginHook;
import cn.wekyjay.wknetic.auth.hook.ILoginHook;
import org.bukkit.Bukkit;

public class LoginAuthManager {
    private final ILoginHook loginHook;

    public LoginAuthManager() {
        if (Bukkit.getServer().getOnlineMode()) {
            loginHook = null; // 正版模式，不需要登录验证
            System.out.println("Server is in online-mode, no login verification needed.");
        } else {
            FastLoginHook fastLoginHook = new FastLoginHook();
            if (fastLoginHook.isHooked()) {
                loginHook = fastLoginHook;
                System.out.println("Using FastLogin for login verification.");
            } else {
                AuthmeHook authmeHook = new AuthmeHook();
                if (authmeHook.isHooked()) {
                    loginHook = authmeHook;
                    System.out.println("Using AuthMe for login verification.");
                } else {
                    loginHook = new CustomLoginHook();
                    System.out.println("Using CustomLogin for login verification.");
                }
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