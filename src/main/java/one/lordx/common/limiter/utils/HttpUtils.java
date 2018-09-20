package one.lordx.common.limiter.utils;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * <pre>
 *
 * <hr/>
 * Package Name : one.lordx.common.limiter.utils
 * Project Name : dam
 * Created by Zhimin Xu on 2017/10/24 下午6:53
 * </pre>
 */
public class HttpUtils {

    public static void writeJson(HttpServletResponse response, Object responseObject) {
        JSONObject responseJsonObject = JSONObject.parseObject(JSONObject.toJSONString(responseObject));
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=utf-8");
        PrintWriter writer = null;
        try {
            writer = response.getWriter();
            writer.print(responseJsonObject.toString());
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer!= null) {
                writer.close();
            }
        }
    }
}
