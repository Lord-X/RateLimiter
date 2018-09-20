package one.lordx.common.limiter;

import one.lordx.common.limiter.annotation.RateLimiter;
import one.lordx.common.limiter.holder.RateLimiterHolder;
import one.lordx.common.limiter.inter.IResponseObject;
import one.lordx.common.limiter.utils.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * <pre>
 *      限流
 *      限制接口每秒的吞吐量
 * <hr/>
 * Package Name : one.lordx.common.limiter
 * Project Name : dam
 * Created by Zhimin Xu on 2017/10/23 下午3:39
 * </pre>
 */
public class RateLimiterFramework {

    private Map<String, RateLimiterHolder> limiterMap = new HashMap<String, RateLimiterHolder>();

    private String packageName;

    private IResponseObject responseObject;

    public RateLimiterFramework(String packageName, IResponseObject responseObject) throws ClassNotFoundException {
        this.packageName = packageName;
        this.responseObject = responseObject;
        init();
    }

    private void init() {
        String[] packageNames = this.packageName.split(",");
        for (String name : packageNames) {
            Set<Class<?>> classes = ClassUtils.getClasses(name);
            initLimiterMap(classes);
        }
    }

    private void initLimiterMap(Set<Class<?>> classes) {
        for (Class<?> clazz : classes) {
            Method[] methods = clazz.getMethods();
            String[] headerPaths = {};
            if (clazz.isAnnotationPresent(RequestMapping.class)) {
                RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
                headerPaths = requestMapping.value();
            }

            if (headerPaths.length > 0) {
                for (String headerPath : headerPaths) {
                    parseMethodAnnotation(methods, headerPath);
                }
            } else {
                parseMethodAnnotation(methods, "");
            }
        }
    }

    private void parseMethodAnnotation(Method[] methods, String headerPath) {
        for (Method method : methods) {
            if (method.isAnnotationPresent(RateLimiter.class) && method.isAnnotationPresent(RequestMapping.class)) {
                RateLimiter rateLimiter = method.getAnnotation(RateLimiter.class);
                RequestMapping methodRequestMapping = method.getAnnotation(RequestMapping.class);
                for (String methodPath : methodRequestMapping.value()) {
                    String key = fullPath(headerPath) + fullPath(methodPath);
                    RateLimiterHolder holder = new RateLimiterHolder(rateLimiter.permitsPerSecond(), rateLimiter.tryAcquire(), rateLimiter.timeout(), this.responseObject, rateLimiter.responseType());
                    this.limiterMap.put(key, holder);
                }
            }
        }
    }

    private String fullPath(String path) {
        if (StringUtils.isEmpty(path)) {
            return "";
        }

        String result = path;
        if (!path.startsWith("/")) {
            result = "/" + result;
        }

        return result;
    }

    public Map<String, RateLimiterHolder> limiter() {
        return this.limiterMap;
    }


}
