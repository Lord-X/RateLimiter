package one.lordx.dam.damdemo.response;

import one.lordx.common.limiter.inter.IResponseObject;
import one.lordx.dam.damdemo.vo.Resp;

/**
 * <pre>
 *
 * <hr/>
 * Package Name : one.lordx.dam.damdemo.response
 * Project Name : dam
 * Created by Zhimin Xu on 2018/9/26 下午2:42
 * </pre>
 */
public class CommonResponse implements IResponseObject<Resp> {


    @Override
    public Resp responseObject() {
        Resp resp = new Resp();
        resp.setCode(1001);
        resp.setDesc("HAHAHAHAHAH");
        return resp;
    }
}
