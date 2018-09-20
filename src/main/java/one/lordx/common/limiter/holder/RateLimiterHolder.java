package one.lordx.common.limiter.holder;

import com.google.common.util.concurrent.RateLimiter;
import one.lordx.common.limiter.constants.ResponseType;
import one.lordx.common.limiter.inter.IResponseObject;

/**
 * <pre>
 *
 * <hr/>
 * Package Name : one.lordx.common.limiter.holder
 * Project Name : dam
 * Created by Zhimin Xu on 2017/10/23 下午7:02
 * </pre>
 */
public class RateLimiterHolder {

    private RateLimiter rateLimiter;

    private double permitsPerSecond = 500.0;

    private boolean tryAcquire = false;

    private long timeout = 0L;

    private IResponseObject responseObject;

    private ResponseType responseType;

    public RateLimiterHolder(double permitsPerSecond, boolean tryAcquire, long timeout, IResponseObject responseObject, ResponseType responseType) {
        this.permitsPerSecond = permitsPerSecond;
        this.tryAcquire = tryAcquire;
        this.timeout = timeout;
        this.responseObject = responseObject;
        this.rateLimiter = RateLimiter.create(permitsPerSecond);
        this.responseType = responseType;
    }

    public RateLimiter getRateLimiter() {
        return rateLimiter;
    }

    public void setRateLimiter(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    public double getPermitsPerSecond() {
        return permitsPerSecond;
    }

    public void setPermitsPerSecond(double permitsPerSecond) {
        this.permitsPerSecond = permitsPerSecond;
    }

    public boolean isTryAcquire() {
        return tryAcquire;
    }

    public void setTryAcquire(boolean tryAcquire) {
        this.tryAcquire = tryAcquire;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public IResponseObject getResponseObject() {
        return responseObject;
    }

    public void setResponseObject(IResponseObject responseObject) {
        this.responseObject = responseObject;
    }

    public ResponseType getResponseType() {
        return responseType;
    }

    public void setResponseType(ResponseType responseType) {
        this.responseType = responseType;
    }

    @Override
    public String toString() {
        return "RateLimiterHolder{" +
                "rateLimiter=" + rateLimiter +
                ", permitsPerSecond=" + permitsPerSecond +
                ", tryAcquire=" + tryAcquire +
                ", timeout=" + timeout +
                ", responseObject=" + responseObject +
                ", responseType=" + responseType +
                '}';
    }
}
