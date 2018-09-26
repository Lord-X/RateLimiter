package one.lordx.common.limiter.annotation;


import one.lordx.common.limiter.constants.ResponseType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 *  限流
 *      该注解被用在方法上
 *      该方法应该是一个SprintMVC的接口，即用@RequestMapping注解的方法。
 *      限流功能会根据指定的吞吐量，控制接口每秒的请求数量。
 * <hr/>
 * Package Name : one.lordx.common.limiter.annotation
 * Project Name : dam
 * Created by Zhimin Xu on 2017/10/23 下午3:24
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimiter {

    /**
     * 每秒允许的吞吐量，默认500个
     * @return
     */
    double permitsPerSecond() default 500.0;

    /**
     * 是否开启无阻塞获取令牌策略
     * 开启后，该许可可以在无延迟下的情况下立即获取得
     * @return true - 获取成功，false - 获取失败
     */
    boolean tryAcquire() default false;

    /**
     * 开启无阻塞获取令牌策略时，获取令牌的超时时间
     * @return 等待指定毫秒后，如果还获取不到，将返回false , 获取成功则返回true
     */
    long timeout() default 0L;

    /**
     * 当超出吞吐量限制时，返回给客户端的数据
     * @return 定义一个提供的异常状态。默认是 NONE
     */
    ResponseType responseType() default ResponseType.NONE;

}
