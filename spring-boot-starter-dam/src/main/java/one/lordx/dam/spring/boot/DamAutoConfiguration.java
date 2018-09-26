package one.lordx.dam.spring.boot;

import one.lordx.common.limiter.inter.IResponseObject;
import one.lordx.common.limiter.interceptor.RateLimiterInterceptor;
import one.lordx.dam.spring.config.DamConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * <pre>
 *  拦截器自动配置，会拦截所有的请求
 * <hr/>
 * Package Name : one.lordx.dam.spring.boot
 * Project Name : dam
 * Created by Zhimin Xu on 2018/9/21 下午5:11
 * </pre>
 */
@Configuration
public class DamAutoConfiguration extends WebMvcConfigurerAdapter {

    @Autowired
    private Environment environment;

    /**
     * 添加限流拦截器
     * 如果没有配置需要扫描的包名，则不会添加这个拦截器，此时限流不生效
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        super.addInterceptors(registry);
        try {
            String damPackageScan = environment.getProperty(DamConfig.DAM_PACKAGE_SCAN);
            String damCustomResponse = environment.getProperty(DamConfig.DAM_CUSTOM_RESPONSE);
            if (StringUtils.isEmpty(damPackageScan)) {
                return;
            }
            if (StringUtils.isEmpty(damCustomResponse)) {
                registry.addInterceptor(new RateLimiterInterceptor(damPackageScan, null)).addPathPatterns("/**");
            } else {
                registry.addInterceptor(new RateLimiterInterceptor(damPackageScan, customResponse())).addPathPatterns("/**");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 如果用户配置了自定义的Response，则会使用用户自定义的
     */
    @Bean
    @ConditionalOnProperty(DamConfig.DAM_CUSTOM_RESPONSE)
    public IResponseObject customResponse() {
        String damCustomResponse = environment.getProperty(DamConfig.DAM_CUSTOM_RESPONSE);
        try {
            Class clazz = Class.forName(damCustomResponse);
            Object resp = clazz.newInstance();
            if (resp instanceof IResponseObject) {
                return (IResponseObject) resp;
            }

            return null;
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }
}
