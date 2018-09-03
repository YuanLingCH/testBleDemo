package fangzuzu.com.ding.bean;

/**
 * Created by lingyuan on 2018/8/30.
 */

public class appBean {

    /**
     * code : 1001
     * msg : null
     * data : {"id":"d594d2f4-ac2c-11e8-9505-00163e06d99e","lockId":null,"forVersionNum":null,"newVersionNum":"1.3.0","versionFileUrl":"http://47.106.207.56:8099/group1/M00/00/00/rBI2xVuHqrWAJvzBADedCtUsgMA112.apk","createtime":"2018-08-30 16:28:37","appId":1,"appVersion":4}
     */

    private int code;
    private Object msg;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : d594d2f4-ac2c-11e8-9505-00163e06d99e
         * lockId : null
         * forVersionNum : null
         * newVersionNum : 1.3.0
         * versionFileUrl : http://47.106.207.56:8099/group1/M00/00/00/rBI2xVuHqrWAJvzBADedCtUsgMA112.apk
         * createtime : 2018-08-30 16:28:37
         * appId : 1
         * appVersion : 4
         */

        private String id;
        private Object lockId;
        private Object forVersionNum;
        private String newVersionNum;
        private String versionFileUrl;
        private String createtime;
        private int appId;
        private int appVersion;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Object getLockId() {
            return lockId;
        }

        public void setLockId(Object lockId) {
            this.lockId = lockId;
        }

        public Object getForVersionNum() {
            return forVersionNum;
        }

        public void setForVersionNum(Object forVersionNum) {
            this.forVersionNum = forVersionNum;
        }

        public String getNewVersionNum() {
            return newVersionNum;
        }

        public void setNewVersionNum(String newVersionNum) {
            this.newVersionNum = newVersionNum;
        }

        public String getVersionFileUrl() {
            return versionFileUrl;
        }

        public void setVersionFileUrl(String versionFileUrl) {
            this.versionFileUrl = versionFileUrl;
        }

        public String getCreatetime() {
            return createtime;
        }

        public void setCreatetime(String createtime) {
            this.createtime = createtime;
        }

        public int getAppId() {
            return appId;
        }

        public void setAppId(int appId) {
            this.appId = appId;
        }

        public int getAppVersion() {
            return appVersion;
        }

        public void setAppVersion(int appVersion) {
            this.appVersion = appVersion;
        }
    }
}
