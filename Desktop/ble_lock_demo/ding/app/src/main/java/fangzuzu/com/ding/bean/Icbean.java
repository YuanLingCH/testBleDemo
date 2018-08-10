package fangzuzu.com.ding.bean;

import java.util.List;

/**
 * Created by lingyuan on 2018/7/9.
 */

public class Icbean {

    /**
     * code : 1001
     * msg : null
     * data : {"recordsTotal":4,"pageSize":10,"totalPage":1,"data":[{"id":"e6dcf116-8331-11e8-9505-00163e06d99e","unlockName":"","unlockFlag":"","addPerson":"18617145277","forWay":"","startTime":"2018-07-09 12:37:00.0","endTime":"2018-07-11 12:37:00.0","addType":0,"unlockType":0},{"id":"8a8420c1-8331-11e8-9505-00163e06d99e","unlockName":"","unlockFlag":"","addPerson":"18617145277","forWay":"","startTime":"2018-07-09 12:33:00.0","endTime":"2018-07-10 12:34:00.0","addType":0,"unlockType":0},{"id":"962dce6d-8325-11e8-9505-00163e06d99e","unlockName":"","unlockFlag":"","addPerson":"18617145277","forWay":"","startTime":"2018-07-09 11:09:00.0","endTime":"2018-07-09 01:10:00.0","addType":0,"unlockType":0},{"id":"c3cd4677-8324-11e8-9505-00163e06d99e","unlockName":"","unlockFlag":"","addPerson":"18617145277","forWay":"","startTime":"2018-07-09 11:03:00.0","endTime":"2018-07-09 11:03:00.0","addType":0,"unlockType":0}]}
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
         * recordsTotal : 4
         * pageSize : 10
         * totalPage : 1
         * data : [{"id":"e6dcf116-8331-11e8-9505-00163e06d99e","unlockName":"","unlockFlag":"","addPerson":"18617145277","forWay":"","startTime":"2018-07-09 12:37:00.0","endTime":"2018-07-11 12:37:00.0","addType":0,"unlockType":0},{"id":"8a8420c1-8331-11e8-9505-00163e06d99e","unlockName":"","unlockFlag":"","addPerson":"18617145277","forWay":"","startTime":"2018-07-09 12:33:00.0","endTime":"2018-07-10 12:34:00.0","addType":0,"unlockType":0},{"id":"962dce6d-8325-11e8-9505-00163e06d99e","unlockName":"","unlockFlag":"","addPerson":"18617145277","forWay":"","startTime":"2018-07-09 11:09:00.0","endTime":"2018-07-09 01:10:00.0","addType":0,"unlockType":0},{"id":"c3cd4677-8324-11e8-9505-00163e06d99e","unlockName":"","unlockFlag":"","addPerson":"18617145277","forWay":"","startTime":"2018-07-09 11:03:00.0","endTime":"2018-07-09 11:03:00.0","addType":0,"unlockType":0}]
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
             * id : e6dcf116-8331-11e8-9505-00163e06d99e
             * unlockName :
             * unlockFlag :
             * addPerson : 18617145277
             * forWay :
             * startTime : 2018-07-09 12:37:00.0
             * endTime : 2018-07-11 12:37:00.0
             * addType : 0
             * unlockType : 0
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
