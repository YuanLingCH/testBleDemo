package fangzuzu.com.ding.bean;

import java.util.List;

/**
 * Created by lingyuan on 2018/6/27.
 */

public class userLockBean {


    /**
     * code : 1001
     * msg : null
     * data : {"parentLock":[],"userLock":[{"id":"88dc616f-f3e7-44d7-83cd-c140da04f47f","lockNumber":"C7:85:4A:22:8D:7D","lockName":"测试袁","allow":"002793","electricity":"0","adminPsw":"123456789","adminUserId":"69712753416","endTime":"2018-07-23 18:04:00.0","startTime":"2018-07-23 18:04:00.0","secretKey":"464f2c026b4d50716d410b0c53621502","keyId":"0310ae95-ea45-4daa-8aa4-765ec4a12dd9"}]}
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
        private List<?> parentLock;
        private List<UserLockBean> userLock;

        public List<?> getParentLock() {
            return parentLock;
        }

        public void setParentLock(List<?> parentLock) {
            this.parentLock = parentLock;
        }

        public List<UserLockBean> getUserLock() {
            return userLock;
        }

        public void setUserLock(List<UserLockBean> userLock) {
            this.userLock = userLock;
        }

        public static class UserLockBean {
            /**
             * id : 88dc616f-f3e7-44d7-83cd-c140da04f47f
             * lockNumber : C7:85:4A:22:8D:7D
             * lockName : 测试袁
             * allow : 002793
             * electricity : 0
             * adminPsw : 123456789
             * adminUserId : 69712753416
             * endTime : 2018-07-23 18:04:00.0
             * startTime : 2018-07-23 18:04:00.0
             * secretKey : 464f2c026b4d50716d410b0c53621502
             * keyId : 0310ae95-ea45-4daa-8aa4-765ec4a12dd9
             */

            private String id;
            private String lockNumber;
            private String lockName;
            private String allow;
            private String electricity;
            private String adminPsw;
            private String adminUserId;
            private String endTime;
            private String startTime;
            private String secretKey;
            private String keyId;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getLockNumber() {
                return lockNumber;
            }

            public void setLockNumber(String lockNumber) {
                this.lockNumber = lockNumber;
            }

            public String getLockName() {
                return lockName;
            }

            public void setLockName(String lockName) {
                this.lockName = lockName;
            }

            public String getAllow() {
                return allow;
            }

            public void setAllow(String allow) {
                this.allow = allow;
            }

            public String getElectricity() {
                return electricity;
            }

            public void setElectricity(String electricity) {
                this.electricity = electricity;
            }

            public String getAdminPsw() {
                return adminPsw;
            }

            public void setAdminPsw(String adminPsw) {
                this.adminPsw = adminPsw;
            }

            public String getAdminUserId() {
                return adminUserId;
            }

            public void setAdminUserId(String adminUserId) {
                this.adminUserId = adminUserId;
            }

            public String getEndTime() {
                return endTime;
            }

            public void setEndTime(String endTime) {
                this.endTime = endTime;
            }

            public String getStartTime() {
                return startTime;
            }

            public void setStartTime(String startTime) {
                this.startTime = startTime;
            }

            public String getSecretKey() {
                return secretKey;
            }

            public void setSecretKey(String secretKey) {
                this.secretKey = secretKey;
            }

            public String getKeyId() {
                return keyId;
            }

            public void setKeyId(String keyId) {
                this.keyId = keyId;
            }
        }
    }
}
