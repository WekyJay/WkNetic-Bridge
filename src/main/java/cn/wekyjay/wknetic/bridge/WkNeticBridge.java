package cn.wekyjay.wknetic.bridge;

import cn.wekyjay.wknetic.auth.LoginAuthManager;
import cn.wekyjay.wknetic.auth.PremiumAuthManager;
import cn.wekyjay.wknetic.auth.listener.AuthListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class WkNeticBridge extends JavaPlugin {
    private static WkNeticBridge instance;
    private NetworkManager networkManager;
    private PremiumAuthManager premiumAuthManager;
    private LoginAuthManager loginAuthManager;

    @Override
    public void onEnable() {
        instance = this;
        // 初始化认证管理器
        this.premiumAuthManager = new PremiumAuthManager();
        this.loginAuthManager = new LoginAuthManager();

        // 注册认证监听器
        getServer().getPluginManager().registerEvents(new AuthListener(this), this);
        // 注册全局聊天监听器
        getServer().getPluginManager().registerEvents(new PLayerChatListener(this),this);

        // 1. 初始化配置
        saveDefaultConfig();

        // 2. 启动网络连接
        // 放在 runTaskLater 稍微延迟一点启动，防止服务器刚开还没完全就绪
        getServer().getScheduler().runTaskLater(this, () -> {
            this.networkManager = new NetworkManager(this);
            this.networkManager.connect();
        }, 20L);

        getLogger().info("WkNeticBridge 已启动 (Package: cn.wekyjay.wknetic.bridge)");
    }

    @Override
    public void onDisable() {
        if (this.networkManager != null) {
            this.networkManager.disconnect();
        }
    }

    public static WkNeticBridge getInstance() {
        return instance;
    }

    public NetworkManager getNetworkManager() {
        return networkManager;
    }

    public PremiumAuthManager getPremiumAuthManager() {
        return premiumAuthManager;
    }

    public LoginAuthManager getLoginAuthManager() {
        return loginAuthManager;
    }
}
