package fangzuzu.com.ding.bean;

import java.util.List;

/**
 * Created by lingyuan on 2018/6/29.
 */

public class openLockRecoderBean {


    /**
     * code : 1001
     * msg : null
     * data : {"recordsTotal":18,"pageSize":10,"totalPage":2,"data":[{"id":"0fe45218-7ddf-11e8-9505-00163e06d99e","unlockPwd":"0108040307030806","unlockTime":"2018-07-02 18:02:44.0","unlockType":0},{"id":"1136360b-7ddf-11e8-9505-00163e06d99e","unlockPwd":"0108040307030806","unlockTime":"2018-07-02 18:02:46.0","unlockType":0},{"id":"1323207e-7ddf-11e8-9505-00163e06d99e","unlockPwd":"0108040307030806","unlockTime":"2018-07-02 18:02:50.0","unlockType":0},{"id":"14f7bca9-7ddf-11e8-9505-00163e06d99e","unlockPwd":"0108040307030806","unlockTime":"2018-07-02 18:02:53.0","unlockType":0},{"id":"166a12db-7ddf-11e8-9505-00163e06d99e","unlockPwd":"0108040307030806","unlockTime":"2018-07-02 18:02:55.0","unlockType":0},{"id":"17be0f24-7dd2-11e8-9505-00163e06d99e","unlockPwd":"0108040307030806","unlockTime":"2018-07-02 16:29:54.0","unlockType":0},{"id":"1aa1aa1f-7dd2-11e8-9505-00163e06d99e","unlockPwd":"0108040307030806","unlockTime":"2018-07-02 16:29:59.0","unlockType":0},{"id":"1cf3d0c3-7ddf-11e8-9505-00163e06d99e","unlockPwd":"0108040307030806","unlockTime":"2018-07-02 18:03:06.0","unlockType":0},{"id":"1ff3e4eb-7ddf-11e8-9505-00163e06d99e","unlockPwd":"0108040307030806","unlockTime":"2018-07-02 18:03:11.0","unlockType":0},{"id":"3bfc4564-7ddf-11e8-9505-00163e06d99e","unlockPwd":"0108040307030806","unlockTime":"2018-07-02 18:03:58.0","unlockType":0}]}
     */

    private int code;
    private Object msg;
    private DataBeanX data;

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

    public DataBeanX getData() {
        return data;
    }

    public void setData(DataBeanX data) {
        this.data = data;
    }

    public static class DataBeanX {
        /**
         * recordsTotal : 18
         * pageSize : 10
         * totalPage : 2
         * data : [{"id":"0fe45218-7ddf-11e8-9505-00163e06d99e","unlockPwd":"0108040307030806","unlockTime":"2018-07-02 18:02:44.0","unlockType":0},{"id":"1136360b-7ddf-11e8-9505-00163e06d99e","unlockPwd":"0108040307030806","unlockTime":"2018-07-02 18:02:46.0","unlockType":0},{"id":"1323207e-7ddf-11e8-9505-00163e06d99e","unlockPwd":"0108040307030806","unlockTime":"2018-07-02 18:02:50.0","unlockType":0},{"id":"14f7bca9-7ddf-11e8-9505-00163e06d99e","unlockPwd":"0108040307030806","unlockTime":"2018-07-02 18:02:53.0","unlockType":0},{"id":"166a12db-7ddf-11e8-9505-00163e06d99e","unlockPwd":"0108040307030806","unlockTime":"2018-07-02 18:02:55.0","unlockType":0},{"id":"17be0f24-7dd2-11e8-9505-00163e06d99e","unlockPwd":"0108040307030806","unlockTime":"2018-07-02 16:29:54.0","unlockType":0},{"id":"1aa1aa1f-7dd2-11e8-9505-00163e06d99e","unlockPwd":"0108040307030806","unlockTime":"2018-07-02 16:29:59.0","unlockType":0},{"id":"1cf3d0c3-7ddf-11e8-9505-00163e06d99e","unlockPwd":"0108040307030806","unlockTime":"2018-07-02 18:03:06.0","unlockType":0},{"id":"1ff3e4eb-7ddf-11e8-9505-00163e06d99e","unlockPwd":"0108040307030806","unlockTime":"2018-07-02 18:03:11.0","unlockType":0},{"id":"3bfc4564-7ddf-11e8-9505-00163e06d99e","unlockPwd":"0108040307030806","unlockTime":"2018-07-02 18:03:58.0","unlockType":0}]
         */

        private int recordsTotal;
        private int pageSize;
        private int totalPage;
        private List<DataBean> data;

        public int getRecordsTotal() {
            return recordsTotal;
        }

        public void setRecordsTotal(int recordsTotal) {
            this.recordsTotal = recordsTotal;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public int getTotalPage() {
            return totalPage;
        }

        public void setTotalPage(int totalPage) {
            this.totalPage = totalPage;
        }

        public List<DataBean> getData() {
            return data;
        }

        public void setData(List<DataBean> data) {
            this.data = data;
        }

        public static class DataBean {
            /**
             * id : 0fe45218-7ddf-11e8-9505-00163e06d99e
             * unlockPwd : 0108040307030806
             * unlockTime : 2018-07-02 18:02:44.0
             * unlockType : 0
             */

            private String id;
            private String unlockPwd;
            private String unlockTime;
            private int unlockType;
            private String userId;

            public String getUserId() {
                return userId;
            }

            public void setUserId(String userId) {
                this.userId = userId;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getUnlockPwd() {
                return unlockPwd;
            }

            public void setUnlockPwd(String unlockPwd) {
                this.unlockPwd = unlockPwd;
            }

            public String getUnlockTime() {
                return unlockTime;
            }

            public void setUnlockTime(String unlockTime) {
                this.unlockTime = unlockTime;
            }

            public int getUnlockType() {
                return unlockType;
            }

            public void setUnlockType(int unlockType) {
                this.unlockType = unlockType;
            }
        }
    }
}
