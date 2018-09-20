package one.lordx.common.limiter.interceptor;

import com.google.common.util.concurrent.RateLimiter;
import one.lordx.common.limiter.RateLimiterFramework;
import one.lordx.common.limiter.holder.RateLimiterHolder;
import one.lordx.common.limiter.inter.IResponseObject;
import one.lordx.common.limiter.utils.HttpUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <pre>
 *     限流拦截器
 *     <hr/>
 *     DEMO：（不指定因限流拒绝访问时，返回给客户端的数据，将返回http状态码：403）
 *       &lt;mvc:interceptor&gt;
            &lt;mvc:mapping path="/**"/&gt;
            &lt;bean class="one.lordx.common.limiter.interceptor.RateLimiterInterceptor"&gt;
                &lt;constructor-arg index="0" value="Your package names to be scaned" /&gt;
            &lt;/bean&gt;
         &lt;/mvc:interceptor&gt;
        构造函数的value需要指定，指定为需要扫描限流注解的包名，多个包名用逗号隔开
        在需要限流的方法上：

        | @RequestMapping(value = "/method")
        | @ResponseBody
        | @RateLimiter(permitsPerSecond = 1000.0) // 每秒1000的吞吐量
        | public Object method() {
        |    ...
        | }

        <hr/>

        DEMO：（指定异常时给客户端的返回数据，需要定义一个类，并实现one.lordx.common.limiter.inter.IResponseObject接口）
        Step1: 实现接口
        | public class RateLimitResponse implements IResponseObject&lt;WebMessage&gt; {
        |    @Override
        |    public WebMessage responseObject() {
        |       return WebMessage.build(403, "拒绝访问", null);
        |    }
        | }
        Step2：将实现累配置在Spring中
        | &lt;bean id="rateLimitResponse" class="one.lordx.web.vo.RateLimitResponse" /&gt;
        Step3：配置拦截器
        | &lt;mvc:interceptor&gt;
        |    &lt;mvc:mapping path="/**"/&gt;
        |    &lt;bean class="one.lordx.common.limiter.interceptor.RateLimiterInterceptor"&gt;
        |        &lt;constructor-arg index="0" value="Your package names to be scaned" /&gt;
        |        &lt;constructor-arg index="1" ref="rateLimitResponse" /*gt;
        |    &lt;/bean&gt;
        | &lt;/mvc:interceptor&gt;
        Step4：在需要限流的方法上配置注解
         | @RequestMapping(value = "/method")
         | @ResponseBody
         | @RateLimiter(permitsPerSecond = 1000.0) // 每秒1000的吞吐量
         | public Object method() {
         |    ...
         | }

        PS：
            配置注解的方式
            注解有三个值可配置：
                - permitsPerSecond 每秒允许的吞吐量，默认500个
                - tryAcquire 是否开启无阻塞获取令牌策略, 开启后，该许可可以在无延迟下的情况下立即获取得，如果获取失败，会返回客户端拒绝访问，默认：false
                - timeout 开启无阻塞获取令牌策略时，获取令牌的超时时间（配合tryAcquire使用），默认：0，单位：毫秒
            如果tryAcquire配置成false，那么即使配置了timeout，也是不生效的

            | @RateLimiter(permitsPerSecond = 1000.0) // 每秒1000的吞吐量
            | @RateLimiter(permitsPerSecond = 1000.0, tryAcquire = true, timeout = 10)

 *
 * <hr/>
 * Package Name : one.lordx.common.limiter.interceptor
 * Project Name : dam
 * Created by Zhimin Xu on 2017/10/23 下午5:59
 * </pre>
 */
public class RateLimiterInterceptor extends HandlerInterceptorAdapter {

    private String packageNames;

    private RateLimiterFramework framework;

    private Map<String, RateLimiterHolder> limiter;

    public RateLimiterInterceptor(String packageNames) throws ClassNotFoundException {
        this(packageNames, null);
    }

    public RateLimiterInterceptor(String packageNames, IResponseObject responseObject) throws ClassNotFoundException {
        this.packageNames = packageNames;
        this.framework = new RateLimiterFramework(this.packageNames, responseObject);
        this.limiter = framework.limiter();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 解析请求访问路径
        String requestUri = request.getRequestURI().substring(1);
        String path = requestUri.substring(requestUri.indexOf("/"));

        if (StringUtils.isEmpty(path)) {
            System.out.println("[Error] Rate limiter error. Parse request uri path is empty.");
            return true;
        }

        RateLimiterHolder holder = limiter.get(path);
        RateLimiter rateLimiter = holder.getRateLimiter();
        boolean tryAcquire = holder.isTryAcquire();
        long timeout = holder.getTimeout();

        if (tryAcquire) {
            boolean acquireResult = false;
            if (timeout > 0) {
                acquireResult = rateLimiter.tryAcquire(timeout, TimeUnit.MILLISECONDS);
            } else if (timeout == 0) {
                acquireResult = rateLimiter.tryAcquire();
            }

            /*
            处理返回值
            1）优先，返回定义在注解中的返回对象
            2）其次，如果拦截器中返回对象配置的不是空，则用拦截器中的返回
            3）再次，返回客户端403
             */
            if (!acquireResult) {
                System.out.println("[Error] Wait require timeout.");
                if (holder.getResponseType().getCode() != 0) {
                    HttpUtils.writeJson(response, holder.getResponseType().type());
                } else if (holder.getResponseObject() != null) {
                    HttpUtils.writeJson(response, holder.getResponseObject().responseObject());
                } else if (holder.getResponseObject() == null) {
                    response.sendError(HttpStatus.FORBIDDEN.value());
                }
            }

            return acquireResult;
        } else {
            double waitTime = rateLimiter.acquire();
            if (waitTime > 0.0D) {
                System.out.println("acquire wait time : " + waitTime);
            }
        }

        return true;
    }

}
