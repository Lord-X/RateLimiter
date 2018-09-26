package one.lordx.dam.damdemo.web;

import one.lordx.common.limiter.annotation.RateLimiter;
import one.lordx.common.limiter.constants.ResponseType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 *  模拟Controller定义了@RequestMapping的情况
 * <hr/>
 * Package Name : one.lordx.dam.damdemo.web
 * Project Name : dam
 * Created by Zhimin Xu on 2018/9/26 上午11:01
 * </pre>
 */
@RestController
@RequestMapping("dam")
public class DamController {

    @RequestMapping("/demo")
    @ResponseBody
    @RateLimiter(permitsPerSecond = 1.0, tryAcquire = true, timeout = 10, responseType = ResponseType.BAD_REQUEST)
    public Object demo() {
        return new ArrayList<>();
    }

    @RequestMapping("/demo1")
    @ResponseBody
    @RateLimiter(permitsPerSecond = 1.0, tryAcquire = true, timeout = 10)
    public Object demo1() {
        return getObject();
    }

    private Object getObject() {
        Map<String, String> retMap = new HashMap<>();
        retMap.put("aaa", "bbb");
        retMap.put("ccc", "ddd");
        retMap.put("eee", "fff");
        return retMap;
    }
}
