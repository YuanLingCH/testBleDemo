package fangzuzu.com.ding.bean;

/**
 * Created by lingyuan on 2018/8/10.
 */

public class mqttBean {

    /**
     * code : 10086
     * data : {"from":"69712753416","time":"2018_06","text":"发内容","to":"ios69880820011"}
     */

    private String code;
    private DataBean data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * from : 69712753416
         * time : 2018_06
         * text : 发内容
         * to : ios69880820011
         */

        private String from;
        private String time;
        private String text;
        private String to;

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }
    }
}
