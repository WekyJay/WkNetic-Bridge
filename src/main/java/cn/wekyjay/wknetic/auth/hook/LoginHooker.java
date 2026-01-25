package cn.wekyjay.wknetic.auth.hook;

import java.util.HashMap;
import cn.wekyjay.wknetic.bridge.WkNeticBridge;


public class LoginHooker {
    private final static HashMap<String, ILoginHook> registeredHooks = new HashMap<>();

    public static HashMap<String, ILoginHook> getRegisteredhooks() {
        return registeredHooks;
    }

    static {
        // 初始化并注册所有已知的登录钩子
        new FastLoginHook();
        new AuthmeHook();
        new CustomLoginHook();
        new CustomPremiumHook();
    }




    public static ILoginHook getHookByName(String name) {
        return registeredHooks.getOrDefault(name, null);
    }



    public LoginHooker() {
        // 遍历已注册的钩子并注册事件
        for (ILoginHook hook : registeredHooks.values()) {
            // 已注册的钩子
            WkNeticBridge.getInstance().getLogger().info("Registering events for hook: " + hook.getHookName());
        }
    }



}
