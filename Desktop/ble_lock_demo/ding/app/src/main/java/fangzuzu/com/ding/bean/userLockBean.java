package fangzuzu.com.ding.bean;

import java.util.List;

/**
 * Created by lingyuan on 2018/6/27.
 */

public class userLockBean {


    /**
     * code : 1001
     * msg : null
     * data : {"parentLock":[],"userLock":[{"id":"627365ce-4c34-4db3-adb1-c7a6d4e3dad6","lockNumber":"DF:04:8C:40:F4:DF","lockName":"袁的锁","allow":"509417","electricity":"42","adminPsw":"39181568","adminUserId":"70744183741","endTime":"2018-09-06 14:18:00.0","startTime":"2018-09-06 14:17:00.0","secretKey":"1570567f14304f7076233a1e04691d37","keyId":"7eba35c0-850d-465f-adb0-b3f31dd686db","updataFlag":null}]}
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
             * id : 627365ce-4c34-4db3-adb1-c7a6d4e3dad6
             * lockNumber : DF:04:8C:40:F4:DF
             * lockName : 袁的锁
             * allow : 509417
             * electricity : 42
             * adminPsw : 39181568
             * adminUserId : 70744183741
             * endTime : 2018-09-06 14:18:00.0
             * startTime : 2018-09-06 14:17:00.0
             * secretKey : 1570567f14304f7076233a1e04691d37
             * keyId : 7eba35c0-850d-465f-adb0-b3f31dd686db
             * updataFlag : null
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
            private Object updataFlag;

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

            public Object getUpdataFlag() {
                return updataFlag;
            }

            public void setUpdataFlag(Object updataFlag) {
                this.updataFlag = updataFlag;
            }
        }
    }
}
