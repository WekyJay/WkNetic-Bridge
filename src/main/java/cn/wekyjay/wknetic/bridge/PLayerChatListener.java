package cn.wekyjay.wknetic.bridge;

import cn.wekyjay.wknetic.api.model.packet.PlayerChatPacket;
import cn.wekyjay.wknetic.api.utils.PacketUtils;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;

import com.google.gson.Gson;

public class PLayerChatListener implements Listener {
    private final Plugin plugin;


    PLayerChatListener (Plugin plugin) {
        this.plugin = plugin;
    }



    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent e) {

        PlayerChatPacket packet = new PlayerChatPacket();
    
        packet.setPlayer(e.getPlayer().getName());
        packet.setMsg(e.getMessage());
        packet.setServerName(plugin.getConfig().getString("Common.server-name", Bukkit.getServer().getName() + "-" + Bukkit.getServer().getVersion()));
        packet.setUuid(e.getPlayer().getUniqueId().toString());
        packet.setWorld(e.getPlayer().getWorld().getName());
        packet.setTime(System.currentTimeMillis());
        
        String json = "";
        
        Gson gson = new Gson();
        json = gson.toJson(packet);
    
    

        // 2. 【异步发送】通过 Netty 发送
        // NetworkManager 内部是 channel.writeAndFlush，线程安全的
        WkNeticBridge.getInstance().getNetworkManager().send(json);
    }
}
