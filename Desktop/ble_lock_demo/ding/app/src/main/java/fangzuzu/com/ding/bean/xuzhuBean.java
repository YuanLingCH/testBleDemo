package fangzuzu.com.ding.bean;

import java.util.List;

/**
 * Created by lingyuan on 2018/9/6.
 */

public class xuzhuBean {

    /**
     * code : 1001
     * msg : null
     * data : [{"id":"3f460011-b1ab-11e8-aa22-00163e06d99e","unlockName":null,"unlockFlag":"7dc2f302","addPerson":"18365408378","forWay":"","startTime":"2018-09-06 16:02:00.0","endTime":"2018-09-06 16:04:00.0","addType":2,"unlockType":0},{"id":"5b0cc123-b1a7-11e8-aa22-00163e06d99e","unlockName":null,"unlockFlag":"7dc2f302","addPerson":"18365408378","forWay":"","startTime":"2018-09-06 15:34:00.0","endTime":"2018-09-06 15:36:00.0","addType":0,"unlockType":0},{"id":"7411e569-b1aa-11e8-aa22-00163e06d99e","unlockName":null,"unlockFlag":"7dc2f302","addPerson":"18365408378","forWay":"","startTime":"2018-09-06 15:56:00.0","endTime":"2018-09-06 15:58:00.0","addType":1,"unlockType":0}]
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
         * id : 3f460011-b1ab-11e8-aa22-00163e06d99e
         * unlockName : null
         * unlockFlag : 7dc2f302
         * addPerson : 18365408378
         * forWay :
         * startTime : 2018-09-06 16:02:00.0
         * endTime : 2018-09-06 16:04:00.0
         * addType : 2
         * unlockType : 0
         */

        private String id;
        private Object unlockName;
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

        public Object getUnlockName() {
            return unlockName;
        }

        public void setUnlockName(Object unlockName) {
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
