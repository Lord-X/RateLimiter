package one.lordx.dam.spring.config;

/**
 * <pre>
 *  配置名称
 *  需要配置在application.properties中
 * <hr/>
 * Package Name : one.lordx.dam.spring.config
 * Project Name : dam
 * Created by Zhimin Xu on 2018/9/26 上午10:40
 * </pre>
 */
public class DamConfig {
    /**
     * KEY:
     * 需要扫描的Controller所在的包名根路径
     */
    public static final String DAM_PACKAGE_SCAN = "dam.package.scan";
    /**
     * KEY:
     * 用户自定义的 当限流时 接口返回的信息
     */
    public static final String DAM_CUSTOM_RESPONSE = "dam.custom.response";
}
