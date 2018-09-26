# 公共组件
## 限流组件
### 描述
> 用于限制WEB接口每秒允许接收的请求数。<br/>
> 目前只可用于用Spring MVC设计的接口。<br/>
> 注意：如果@RequestMapping中配置的Value值包含任意格式的占位符，则不支持此类接口的限流

### Quick Start（SpringBoot方式）
> 通过添加Dam提供的starter集成限流组件，省去配置的麻烦(可参考dam-demo工程)

#### Step1：添加Maven依赖
    基于springboot 2.0.5.RELEASE版本
    <dependency>
        <groupId>one.lordx.common</groupId>
        <artifactId>spring-boot-starter-dam</artifactId>
        <version>1.1.0-RELEASE</version>
    </dependency>

#### Step2：在application.properties中添加需要扫描的controller的包名
    dam.package.scan=one.lordx.dam.damdemo.web

#### Step3：在需要限流的接口上添加注解，即可使用
    这种方式下，当限流时，会返回dam定义的ResponseType，即@RateLimiter的第四个参数
    @RestController
    @RequestMapping("dam")
    public class DamController {

        @RequestMapping("/demo")
        @ResponseBody
        @RateLimiter(permitsPerSecond = 1.0, tryAcquire = true, timeout = 10, responseType = ResponseType.BAD_REQUEST)
        public Object demo() {
            return new ArrayList<>();
        }
    }

#### Step4：限流时，接口返回信息的扩展
    有时，在限流时，前端需要获取服务端定义的一些信息，再针对性的执行相关逻辑。
    这时就需要用户来自定义限流时 接口返回的信息。
    需要自定义时，用户需要实现one.lordx.common.limiter.inter.IResponseObject接口。
    并把实现类配置在application.properties中，限流组件在限流时就会返回用户自定义的数据结构。例如：
    public class CommonResponse implements IResponseObject<Resp> {
        @Override
        public Resp responseObject() {
            Resp resp = new Resp();
            resp.setCode(1001);
            resp.setDesc("HAHAHAHAHAH");
            return resp;
        }
    }
    配置：
    dam.custom.response=one.lordx.dam.damdemo.response.CommonResponse

    具体可以参考dam-demo工程，@RateLimiter的说明在文末。

### Quick Start（原生方式）
#### Step1：添加Maven依赖
    <dependency>
        <groupId>one.lordx.common</groupId>
        <artifactId>lordx-limiter</artifactId>
        <version>1.0.0-RELEASE</version>
    </dependency>
#### Step2：
> 方式1：超过吞吐量的请求，会返回403<br/>

在Spring中配置拦截器

    <mvc:interceptors>
        <mvc:interceptor>
            <mvc:mapping path="/**"/>
            <bean class="one.lordx.common.limiter.interceptor.RateLimiterInterceptor">
                <constructor-arg index="0" value="需要扫描的controller包" />
            </bean>
        </mvc:interceptor>
    </mvc:interceptors>
其中，构造器配置的value是需要扫描的包名。比如配置成one.lordx.ap.web，会在web这个包下面扫描所有配置了限流注解的接口。

然后在需要限制吞吐量的接口上配置注解(RateLimiter，注解的说明请见文章末尾)，如：

    @RequestMapping(value = "/method")
    @ResponseBody
    @RateLimiter(permitsPerSecond = 1, tryAcquire = true, timeout = 10)
    public Object method() {
        // 方法体 ...
    }

> 方式2：超出吞吐量限制的请求，可以定制全局的返回数据结构<br/>

实现IResponseObject接口

    public class RateLimitResponse implements IResponseObject<WebMessage> {
        @Override
        public WebMessage responseObject() {
            return WebMessage.build(403, "拒绝访问", null);
        }
    }
在Spring中配置实现类的bean

    <bean id="rateLimitResponse" class="one.lordx.web.vo.RateLimitResponse" />

在Spring中配置拦截器，构造器第二个参数指定为实现类，将使用实现类中responseObject方法的返回值作为结果返回给客户端

    <mvc:interceptors>
        <mvc:interceptor>
            <mvc:mapping path="/**"/>
            <bean class="one.lordx.common.limiter.interceptor.RateLimiterInterceptor">
                <constructor-arg index="0" value="需要扫描的controller包" />
                <constructor-arg index="1" ref="rateLimitResponse" />
            </bean>
        </mvc:interceptor>
    </mvc:interceptors>
    
在需要限制吞吐量的方法上添加注解

    @RequestMapping(value = "/method")
    @ResponseBody
    @RateLimiter(permitsPerSecond = 1, tryAcquire = true, timeout = 10)
    public Object method() {
        // 方法体 ...
    }
    
> 方式3：为每个接口配置一个已提供的返回协议数据

这种方式可以与全局配置返回共存，优选返回方式3的策略，没有定义方式3，才放回方式2中的策略。

这种方式基本与方式1一致。

在Spring中配置拦截器

    <mvc:interceptors>
        <mvc:interceptor>
            <mvc:mapping path="/**"/>
            <bean class="one.lordx.common.limiter.interceptor.RateLimiterInterceptor">
                <constructor-arg index="0" value="需要扫描的controller包" />
            </bean>
        </mvc:interceptor>
    </mvc:interceptors>
    
然后在需要限制吞吐量的接口上配置注解(RateLimiter，注解的说明请见文章末尾)，如：

    @RequestMapping(value = "/method")
    @ResponseBody
    @RateLimiter(permitsPerSecond = 1, tryAcquire = true, timeout = 10, responseType = ResponseType.FORBIDDEN)
    public Object method() {
        // 方法体 ...
    }
    
与方式1相比，这里的注解里多配置了responseType。这是一个枚举类型，默认值是NONE。

如果在这里配置了非NONE的值，会向客户端放回这里配置的枚举对象。

返回格式示例：

    {
        "desc": "Forbidden",
        "code": 403
    }
    
如果配置的是NONE，则会尝试获取方式2中配置的全局返回配置。

如果方式2中的也没有获取到。则会返回403页面给客户端。

<span id = "jump" ></span>

### RateLimiter注解说明

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
    
*如果tryAcquire没有配置或配置的是false，那么timeout即使配置了，也没有任何作用*