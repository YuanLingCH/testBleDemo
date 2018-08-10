package fangzuzu.com.ding.bean;

import java.util.List;

/**
 * Created by lingyuan on 2018/7/5.
 */

public class passwordManagerBean {

    /**
     * code : 1001
     * msg : null
     * data : {"recordsTotal":22,"pageSize":10,"totalPage":3,"data":[{"id":"17043e44-7f63-11e8-9505-00163e06d99e","unlockName":"jiji","unlockFlag":"99566400","addPerson":"18617145277","forWay":"","startTime":"2018-06-21 18:00:00.0","endTime":"2018-12-21 18:00:00.0","addType":3,"unlockType":1},{"id":"19e20882-7f63-11e8-9505-00163e06d99e","unlockName":"jiji","unlockFlag":"46566400","addPerson":"18617145277","forWay":"","startTime":"2018-06-21 18:00:00.0","endTime":"2018-12-21 18:00:00.0","addType":3,"unlockType":1},{"id":"1a7ec95e-7ffb-11e8-9505-00163e06d99e","unlockName":"","unlockFlag":"1778554401","addPerson":"18617145277","forWay":"","startTime":"2018-07-05 10:28:00.0","endTime":"2018-07-05 10:28:00.0","addType":3,"unlockType":0},{"id":"1ed11272-7ffb-11e8-9505-00163e06d99e","unlockName":"","unlockFlag":"3868763401","addPerson":"18617145277","forWay":"","startTime":"2018-07-01 18:00:00.0","endTime":"2018-07-01 18:00:00.0","addType":3,"unlockType":0},{"id":"2201de71-7ffb-11e8-9505-00163e06d99e","unlockName":"","unlockFlag":"1778554401","addPerson":"18617145277","forWay":"","startTime":"2018-07-05 10:28:00.0","endTime":"2018-07-05 10:28:00.0","addType":3,"unlockType":0},{"id":"5dd8506a-7f63-11e8-9505-00163e06d99e","unlockName":"锁测试","unlockFlag":"864161100","addPerson":"18617145277","forWay":"","startTime":"2018-07-04 16:22:00.0","endTime":"2023-06-02 10:54:00.0","addType":3,"unlockType":1},{"id":"5e49626e-7f63-11e8-9505-00163e06d99e","unlockName":"锁测试","unlockFlag":"624161100","addPerson":"18617145277","forWay":"","startTime":"2018-07-04 16:22:00.0","endTime":"2023-06-02 10:54:00.0","addType":3,"unlockType":1},{"id":"5ea16966-7f63-11e8-9505-00163e06d99e","unlockName":"锁测试","unlockFlag":"334161100","addPerson":"18617145277","forWay":"","startTime":"2018-07-04 16:22:00.0","endTime":"2023-06-02 10:54:00.0","addType":3,"unlockType":1},{"id":"5ebea7c3-7f63-11e8-9505-00163e06d99e","unlockName":"锁测试","unlockFlag":"454161100","addPerson":"18617145277","forWay":"","startTime":"2018-07-04 16:22:00.0","endTime":"2023-06-02 10:54:00.0","addType":3,"unlockType":1},{"id":"5edc2f85-7f63-11e8-9505-00163e06d99e","unlockName":"锁测试","unlockFlag":"784161100","addPerson":"18617145277","forWay":"","startTime":"2018-07-04 16:22:00.0","endTime":"2023-06-02 10:54:00.0","addType":3,"unlockType":1}]}
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
         * recordsTotal : 22
         * pageSize : 10
         * totalPage : 3
         * data : [{"id":"17043e44-7f63-11e8-9505-00163e06d99e","unlockName":"jiji","unlockFlag":"99566400","addPerson":"18617145277","forWay":"","startTime":"2018-06-21 18:00:00.0","endTime":"2018-12-21 18:00:00.0","addType":3,"unlockType":1},{"id":"19e20882-7f63-11e8-9505-00163e06d99e","unlockName":"jiji","unlockFlag":"46566400","addPerson":"18617145277","forWay":"","startTime":"2018-06-21 18:00:00.0","endTime":"2018-12-21 18:00:00.0","addType":3,"unlockType":1},{"id":"1a7ec95e-7ffb-11e8-9505-00163e06d99e","unlockName":"","unlockFlag":"1778554401","addPerson":"18617145277","forWay":"","startTime":"2018-07-05 10:28:00.0","endTime":"2018-07-05 10:28:00.0","addType":3,"unlockType":0},{"id":"1ed11272-7ffb-11e8-9505-00163e06d99e","unlockName":"","unlockFlag":"3868763401","addPerson":"18617145277","forWay":"","startTime":"2018-07-01 18:00:00.0","endTime":"2018-07-01 18:00:00.0","addType":3,"unlockType":0},{"id":"2201de71-7ffb-11e8-9505-00163e06d99e","unlockName":"","unlockFlag":"1778554401","addPerson":"18617145277","forWay":"","startTime":"2018-07-05 10:28:00.0","endTime":"2018-07-05 10:28:00.0","addType":3,"unlockType":0},{"id":"5dd8506a-7f63-11e8-9505-00163e06d99e","unlockName":"锁测试","unlockFlag":"864161100","addPerson":"18617145277","forWay":"","startTime":"2018-07-04 16:22:00.0","endTime":"2023-06-02 10:54:00.0","addType":3,"unlockType":1},{"id":"5e49626e-7f63-11e8-9505-00163e06d99e","unlockName":"锁测试","unlockFlag":"624161100","addPerson":"18617145277","forWay":"","startTime":"2018-07-04 16:22:00.0","endTime":"2023-06-02 10:54:00.0","addType":3,"unlockType":1},{"id":"5ea16966-7f63-11e8-9505-00163e06d99e","unlockName":"锁测试","unlockFlag":"334161100","addPerson":"18617145277","forWay":"","startTime":"2018-07-04 16:22:00.0","endTime":"2023-06-02 10:54:00.0","addType":3,"unlockType":1},{"id":"5ebea7c3-7f63-11e8-9505-00163e06d99e","unlockName":"锁测试","unlockFlag":"454161100","addPerson":"18617145277","forWay":"","startTime":"2018-07-04 16:22:00.0","endTime":"2023-06-02 10:54:00.0","addType":3,"unlockType":1},{"id":"5edc2f85-7f63-11e8-9505-00163e06d99e","unlockName":"锁测试","unlockFlag":"784161100","addPerson":"18617145277","forWay":"","startTime":"2018-07-04 16:22:00.0","endTime":"2023-06-02 10:54:00.0","addType":3,"unlockType":1}]
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
             * id : 17043e44-7f63-11e8-9505-00163e06d99e
             * unlockName : jiji
             * unlockFlag : 99566400
             * addPerson : 18617145277
             * forWay :
             * startTime : 2018-06-21 18:00:00.0
             * endTime : 2018-12-21 18:00:00.0
             * addType : 3
             * unlockType : 1
             */

            private String id;
            private String unlockName;
            private String unlockFlag;
            private String addPerson;
            private String forWay;
            private String startTime;
            private String endTime;
            private int addType;
            private int unlockType;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getUnlockName() {
                return unlockName;
            }

            public void setUnlockName(String unlockName) {
                this.unlockName = unlockName;
            }

            public String getUnlockFlag() {
                return unlockFlag;
            }

            public void setUnlockFlag(String unlockFlag) {
                this.unlockFlag = unlockFlag;
            }

            public String getAddPerson() {
                return addPerson;
            }

            public void setAddPerson(String addPerson) {
                this.addPerson = addPerson;
            }

            public String getForWay() {
                return forWay;
            }

            public void setForWay(String forWay) {
                this.forWay = forWay;
            }

            public String getStartTime() {
                return startTime;
            }

            public void setStartTime(String startTime) {
                this.startTime = startTime;
            }

            public String getEndTime() {
                return endTime;
            }

            public void setEndTime(String endTime) {
                this.endTime = endTime;
            }

            public int getAddType() {
                return addType;
            }

            public void setAddType(int addType) {
                this.addType = addType;
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
