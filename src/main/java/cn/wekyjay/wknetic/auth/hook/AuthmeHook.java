package cn.wekyjay.wknetic.auth.hook;


import org.bukkit.Bukkit;

import cn.wekyjay.wknetic.auth.listener.AuthmeListener;
import cn.wekyjay.wknetic.bridge.WkNeticBridge;

public class AuthmeHook extends LoginHook {

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

    @Override
    public boolean registerEvents() {
        WkNeticBridge instance = WkNeticBridge.getInstance();

        instance.getServer().getScheduler().runTaskAsynchronously(instance, () -> {
            instance.getLogger().info("Registering AuthMe events asynchronously.");
            Bukkit.getServer().getPluginManager().registerEvents(new AuthmeListener(), instance);
            instance.getLogger().info("AuthMe events registered.");
        });
        return true;
    }

}
