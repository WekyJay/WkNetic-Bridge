package cn.wekyjay.wknetic.auth.listener;


import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import cn.wekyjay.wknetic.auth.PremiumAuthManager;
import cn.wekyjay.wknetic.bridge.WkNeticBridge;


public class AuthmeListener implements Listener {

        // --- AuthMe 登录事件 ---
    @EventHandler
    public void onAuthMeLogin(fr.xephi.authme.events.LoginEvent event) {
        Player player = event.getPlayer();
        // 从未登录列表中移除
        AuthListener.getUnauthenticatedPlayers().remove(player.getUniqueId());
        // syncToCommunity(player, player.getUniqueId(), "CRACKED");

        PremiumAuthManager premiumAuthManager = WkNeticBridge.getInstance().getPremiumAuthManager();

        premiumAuthManager.isPremium(player);
        
    }
}
