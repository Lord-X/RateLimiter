package one.lordx.common.limiter.inter;

/**
 * <pre>
 *
 * <hr/>
 * Package Name : one.lordx.common.limiter.inter
 * Project Name : dam
 * Created by Zhimin Xu on 2017/10/24 下午6:09
 * </pre>
 */
public interface IResponseObject<T> {

    /**
     * <pre>
     * 实现该接口，并配置在拦截器中，当请求数超出限流而导致拒绝访问时
     * 将使用实现了这个方法的子类来处理异常返回数据。
     * 返回给客户端的数据将解析成JSONObject返回。
     *
     * 如果不实现该接口（没有配置在拦截器bean的构造方法中），对于超出限流的请求，将返回403 - forbidden
     * </pre>
     * @return 返回给客户端的定制数据结构
     */
    T responseObject();
}
