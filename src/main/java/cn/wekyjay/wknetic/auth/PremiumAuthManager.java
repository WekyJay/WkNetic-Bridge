package cn.wekyjay.wknetic.auth;

import cn.wekyjay.wknetic.auth.hook.CustomPremiumHook;
import cn.wekyjay.wknetic.auth.hook.FastLoginHook;
import cn.wekyjay.wknetic.auth.hook.ILoginHook;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PremiumAuthManager {
    private final ILoginHook premiumHook;

    public PremiumAuthManager() {
        if (Bukkit.getServer().getOnlineMode()) {
            premiumHook = null; // 正版模式，所有玩家都是正版的
            Bukkit.getLogger().info("Server is in online-mode, all players are premium.");
        } else {
            FastLoginHook fastLoginHook = new FastLoginHook();
            if (fastLoginHook.isHooked()) {
                premiumHook = fastLoginHook;
                Bukkit.getLogger().info("Using FastLogin for premium check.");
            } else {
                premiumHook = new CustomPremiumHook();
                Bukkit.getLogger().info("Using CustomPremium for premium check.");
            }
        }
    }

    public boolean isPremium(Player player) {
        if (premiumHook == null) {
            return true; // 正版模式
        }
        if ("FastLogin".equals(premiumHook.getHookName())) {
            try {
                Class<?> fastLoginApiClass = Class.forName("com.github.games647.fastlogin.core.api.FastLoginApi");
                Object fastLoginApi = fastLoginApiClass.getMethod("getApi").invoke(null);
                return (boolean) fastLoginApiClass.getMethod("isPremium", Player.class).invoke(fastLoginApi, player);
            } catch (Exception e) {
                Bukkit.getLogger().warning("Failed to check FastLogin premium status: " + e.getMessage());
                return false;
            }
        } else if ("CustomPremium".equals(premiumHook.getHookName())) {
            return ((CustomPremiumHook) premiumHook).isPremium(player);
        }
        return false;
    }
}