package fangzuzu.com.ding.bean;

import java.util.List;

/**
 * Created by lingyuan on 2018/6/5.
 */

public class permisonBean {

    /**
     * code : 1001
     * msg : null
     * data : [{"id":"f3761167-686d-11e8-b04f-00163e0c1269","description":"发送钥匙"},{"id":"f37618bc-686d-11e8-b04f-00163e0c1269","description":"发送密码"},{"id":"f376192b-686d-11e8-b04f-00163e0c1269","description":"钥匙管理"},{"id":"f376195c-686d-11e8-b04f-00163e0c1269","description":"密码管理"},{"id":"f3761988-686d-11e8-b04f-00163e0c1269","description":"IC卡"},{"id":"f37619bd-686d-11e8-b04f-00163e0c1269","description":"指纹"},{"id":"f37619e9-686d-11e8-b04f-00163e0c1269","description":"操作记录"},{"id":"f3761a1b-686d-11e8-b04f-00163e0c1269","description":"设置"}]
     */

    private int code;
    private Object msg;
    private List<DataBean> data;



    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getMsg() {
        return msg;
    }

    public void setMsg(Object msg) {
        this.msg = msg;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : f3761167-686d-11e8-b04f-00163e0c1269
         * description : 发送钥匙
         */

        private String id;
        private String description;
        private boolean isFlag;

        public boolean isFlag() {
            return isFlag;
        }

        public void setFlag(boolean flag) {
            isFlag = flag;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
