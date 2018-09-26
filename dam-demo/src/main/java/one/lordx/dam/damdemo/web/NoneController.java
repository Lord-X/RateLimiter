package one.lordx.dam.damdemo.web;

import one.lordx.common.limiter.annotation.RateLimiter;
import one.lordx.common.limiter.constants.ResponseType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

/**
 * <pre>
 *  模拟Controller没有定义@RequestMapping的情况
 * <hr/>
 * Package Name : one.lordx.dam.damdemo.web
 * Project Name : dam
 * Created by Zhimin Xu on 2018/9/26 下午2:29
 * </pre>
 */
@RestController
public class NoneController {

    @RequestMapping("hhh")
    @ResponseBody
    @RateLimiter(permitsPerSecond = 1.0, tryAcquire = true, timeout = 10, responseType = ResponseType.BAD_GATEWAY)
    public Object demo() {
        return new HashMap<>();
    }
}
