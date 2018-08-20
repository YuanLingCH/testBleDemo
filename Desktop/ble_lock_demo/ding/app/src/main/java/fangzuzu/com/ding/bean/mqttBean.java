package fangzuzu.com.ding.bean;

/**
 * Created by lingyuan on 2018/8/10.
 */

public class mqttBean {


    /**
     * topic : fzzchat.PTP
     * topicId :
     * msg : {"data":{"size":"3.78","text1":"","text4":"2.xxxxx","text3":"1.xxxxx","text2":"更新内容","version":"1.1"},"code":"1003"}
     */

    private String topic;
    private String topicId;
    private MsgBean msg;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public MsgBean getMsg() {
        return msg;
    }

    public void setMsg(MsgBean msg) {
        this.msg = msg;
    }

    public static class MsgBean {
        /**
         * data : {"size":"3.78","text1":"","text4":"2.xxxxx","text3":"1.xxxxx","text2":"更新内容","version":"1.1"}
         * code : 1003
         */

        private DataBean data;
        private String code;

        public DataBean getData() {
            return data;
        }

        public void setData(DataBean data) {
            this.data = data;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public static class DataBean {
            /**
             * size : 3.78
             * text1 :
             * text4 : 2.xxxxx
             * text3 : 1.xxxxx
             * text2 : 更新内容
             * version : 1.1
             */

            private String size;
            private String text1;
            private String text4;
            private String text3;
            private String text2;
            private String version;

            public String getSize() {
                return size;
            }

            public void setSize(String size) {
                this.size = size;
            }

            public String getText1() {
                return text1;
            }

            public void setText1(String text1) {
                this.text1 = text1;
            }

            public String getText4() {
                return text4;
            }

            public void setText4(String text4) {
                this.text4 = text4;
            }

            public String getText3() {
                return text3;
            }

            public void setText3(String text3) {
                this.text3 = text3;
            }

            public String getText2() {
                return text2;
            }

            public void setText2(String text2) {
                this.text2 = text2;
            }

            public String getVersion() {
                return version;
            }

            public void setVersion(String version) {
                this.version = version;
            }
        }
    }
}
