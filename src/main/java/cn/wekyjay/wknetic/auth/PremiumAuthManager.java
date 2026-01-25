package cn.wekyjay.wknetic.auth;

import cn.wekyjay.wknetic.auth.hook.CustomPremiumHook;
import cn.wekyjay.wknetic.auth.hook.FastLoginHook;
import cn.wekyjay.wknetic.auth.hook.ILoginHook;
import cn.wekyjay.wknetic.auth.hook.LoginHooker;
import cn.wekyjay.wknetic.bridge.WkNeticBridge;

import java.lang.reflect.Method;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PremiumAuthManager {
    private final ILoginHook premiumHook;

    public PremiumAuthManager() {
        if (Bukkit.getServer().getOnlineMode()) {
            premiumHook = null; // 正版模式，所有玩家都是正版的
            WkNeticBridge.getInstance().getLogger().info("Server is in online-mode, all players are premium.");
        } else {
            FastLoginHook fastLoginHook = (FastLoginHook) LoginHooker.getHookByName("FastLogin");
            if (fastLoginHook != null && fastLoginHook.isHooked()) {
                premiumHook = fastLoginHook;
                WkNeticBridge.getInstance().getLogger().info("Using FastLogin for premium check.");
            } else { // 使用自定义的正版验证
                premiumHook = new CustomPremiumHook();
                WkNeticBridge.getInstance().getLogger().info("Using CustomPremium for premium check.");
            }
        }
    }

    public boolean isPremium(Player player) {
        if (premiumHook == null) {
            return true; // 正版模式
        }
        // 使用反射调用 FastLogin 的 API 来检查正版状态
        if ("FastLogin".equals(premiumHook.getHookName())) {
            WkNeticBridge.getInstance().getLogger().info("开始检查状态：Checking premium status for player " + player.getName() + " using FastLogin.");
            try {
                // 1. 获取 FastLogin 插件实例
                Plugin fastLogin = Bukkit.getPluginManager().getPlugin("FastLogin");
        
                // 如果插件没装或者没启用，直接返回 false
                if (fastLogin == null || !fastLogin.isEnabled()) {
                    return false;
                }

                // 2. 获取 getStatus 方法
                // 方法签名: public PremiumStatus getStatus(UUID uuid)
                Method getStatusMethod = fastLogin.getClass().getMethod("getStatus", UUID.class);

                // 3. 执行方法
                // 相当于: Object statusObj = fastLogin.getStatus(player.getUniqueId());
                Object statusObj = getStatusMethod.invoke(fastLogin, player.getUniqueId());

                // 4. 处理返回值 (最关键的一步)
                // 因为我们没有 import PremiumStatus 枚举类，所以不能直接强转。
                // 必须使用 toString() 来比较枚举的名字。
                if (statusObj != null) {
                    // PremiumStatus 枚举里，正版的状态名通常是 "PREMIUM"
                    if ("PREMIUM".equalsIgnoreCase(statusObj.toString())) {
                        WkNeticBridge.getInstance().getLogger().info("Player " + player.getName() + " is identified as PREMIUM by FastLogin.【正版通过】");
                        return true;
                    }
                    WkNeticBridge.getInstance().getLogger().info("Player " + player.getName() + " is not PREMIUM according to FastLogin. =>" + statusObj.toString());
                    return false;
                }

            } catch (Exception e) {
                WkNeticBridge.getInstance().getLogger().warning("Failed to check FastLogin premium status: " + e.getMessage());
                return false;
            }
            // 使用自己的验证逻辑
        } else if ("CustomPremium".equals(premiumHook.getHookName())) {
            return ((CustomPremiumHook) premiumHook).isPremium(player);
        }
        return false;
    }
}