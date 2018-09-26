package one.lordx.dam.damdemo.vo;

/**
 * <pre>
 *
 * <hr/>
 * Package Name : one.lordx.dam.damdemo.vo
 * Project Name : dam
 * Created by Zhimin Xu on 2018/9/26 下午2:42
 * </pre>
 */
public class Resp {

    private int code;

    private String desc;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "Resp{" +
                "code=" + code +
                ", desc='" + desc + '\'' +
                '}';
    }
}
