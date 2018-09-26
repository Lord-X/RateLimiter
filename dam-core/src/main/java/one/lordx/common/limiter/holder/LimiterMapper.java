package one.lordx.common.limiter.holder;

import java.util.Map;

/**
 * <pre>
 *
 * <hr/>
 * Package Name : one.lordx.common.limiter.holder
 * Project Name : dam
 * Created by Zhimin Xu on 2018/9/26 上午11:30
 * </pre>
 */
public class LimiterMapper {
    private Map<String, RateLimiterHolder> limiter;

    public LimiterMapper(Map<String, RateLimiterHolder> limiter) {
        this.limiter = limiter;
    }

    public RateLimiterHolder getHolder(String uri) {
        String subStr = uri;
        while (subStr.startsWith("/")) {
            RateLimiterHolder holder = limiter.get(subStr);
            if (holder != null) {
                return holder;
            }

            if (subStr.length() > 1) {
                subStr = subStr.substring(subStr.indexOf("/") + 1);
                if (subStr.contains("/")) {
                    subStr = subStr.substring(subStr.indexOf("/"));
                } else {
                    break;
                }
            } else {
                break;
            }
        }
        return null;
    }
}
