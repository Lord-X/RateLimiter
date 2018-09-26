package one.lordx.dam.damdemo.web;

import one.lordx.common.limiter.annotation.RateLimiter;
import one.lordx.common.limiter.constants.ResponseType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 *  模拟Controller定义了@RequestMapping且定义的是根路径的情况
 * <hr/>
 * Package Name : one.lordx.dam.damdemo.web
 * Project Name : dam
 * Created by Zhimin Xu on 2018/9/26 下午2:22
 * </pre>
 */
@RestController
@RequestMapping("/")
public class RootController {

    @RequestMapping("demo1")
    @ResponseBody
    @RateLimiter(permitsPerSecond = 1.0, tryAcquire = true, timeout = 10, responseType = ResponseType.BAD_GATEWAY)
    public Object demo() {
        Map<String, String> retMap = new HashMap<>();
        retMap.put("aaa", "a");
        return retMap;
    }
}
