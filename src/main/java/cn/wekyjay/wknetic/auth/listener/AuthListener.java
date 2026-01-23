package cn.wekyjay.wknetic.auth.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import cn.wekyjay.wknetic.bridge.WkNeticBridge;

public class AuthListener implements Listener {
    private final WkNeticBridge plugin;
    private final Set<UUID> unauthenticatedPlayers = new HashSet<>();

    public AuthListener(WkNeticBridge plugin) {
        this.plugin = plugin;
    }

    // --- 玩家加入事件 ---
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // 禁止玩家任何操作
        event.setJoinMessage(null); // 取消默认加入消息

        handlePlayerJoin(event.getPlayer());
    }



    /**
     * 处理玩家加入逻辑
     * @param player
     */

    public void handlePlayerJoin(Player player) {
        // 检查正版
        boolean isPremium = plugin.getPremiumAuthManager().isPremium(player);
        if (isPremium) {
            // 这是正版玩家，直接使用 Mojang UUID 进行社区绑定
            syncToCommunity(player, player.getUniqueId(), "PREMIUM");
            // 正版玩家已认证，无需添加到未登录列表
            return;
        }

        // 如果不是正版，添加到未登录列表
        unauthenticatedPlayers.add(player.getUniqueId());

        // 如果不是正版，根据登录管理器处理
        if (plugin.getLoginAuthManager().hasLoginHook()) {
            String hookName = plugin.getLoginAuthManager().getLoginHookName();
            if ("AuthMe".equals(hookName)) {
                // 等待 AuthMe 登录事件
            } else if ("CustomLogin".equals(hookName)) {
                // TODO: 实现自己的登录验证逻辑，例如提示玩家输入密码
                player.sendMessage("§e[WkNetic] 请在聊天框输入密码进行登录。");
                // 这里需要监听玩家聊天，验证密码等
            }
        }
    }

    // --- AuthMe 登录事件 ---
    @EventHandler
    public void onAuthMeLogin(fr.xephi.authme.events.LoginEvent event) {
        Player player = event.getPlayer();
        // 从未登录列表中移除
        unauthenticatedPlayers.remove(player.getUniqueId());
        syncToCommunity(player, player.getUniqueId(), "CRACKED");
    }

    // --- 防止未登录玩家移动 ---
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (unauthenticatedPlayers.contains(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage("§c[WkNetic] 请先完成登录验证！");
        }
    }

    // --- 防止未登录玩家聊天 ---
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (unauthenticatedPlayers.contains(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage("§c[WkNetic] 请先完成登录验证！");
        }
    }

    // --- 防止未登录玩家执行命令 ---
    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (unauthenticatedPlayers.contains(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage("§c[WkNetic] 请先完成登录验证！");
        }
    }

    // --- 玩家离开时清理 ---
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        unauthenticatedPlayers.remove(player.getUniqueId());
    }


    // 同步到社区
    private void syncToCommunity(Player player, java.util.UUID uuid, String type) {
        // 异步处理
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String uuidStr = uuid.toString();
            String name = player.getName();
            
            // 发送同步消息到后端
            String json = "{\"type\": \"SYNC_PLAYER\", \"uuid\": \"" + uuidStr + "\", \"name\": \"" + name + "\", \"authType\": \"" + type + "\"}";
            
            plugin.getNetworkManager().send(json);
            
            player.sendMessage("§a[WkNetic] 社区账号已成功同步！");
        });
    }
    
}
