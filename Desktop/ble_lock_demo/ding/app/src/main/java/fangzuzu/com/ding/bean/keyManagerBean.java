package fangzuzu.com.ding.bean;

import java.util.List;

/**
 * Created by lingyuan on 2018/7/3.
 */

public class keyManagerBean  {


    /**
     * code : 1001
     * msg : null
     * data : {"recordsTotal":1,"pageSize":10,"totalPage":1,"data":[{"id":"3d16e9bd-522a-44bb-a7c7-e700bd00dcfd","lockId":"3352dc20-46d3-40a6-84ba-5f245d48e860","userId":"69880820011","childUsername":"18665261827","parentId":"18365408378","keyName":"测试袁","startTime":"2018-09-27 10:38:00.0","endTime":"2023-01-01 00:00:00.0","statu":0,"createtime":1538015915000}]}
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
         * recordsTotal : 1
         * pageSize : 10
         * totalPage : 1
         * data : [{"id":"3d16e9bd-522a-44bb-a7c7-e700bd00dcfd","lockId":"3352dc20-46d3-40a6-84ba-5f245d48e860","userId":"69880820011","childUsername":"18665261827","parentId":"18365408378","keyName":"测试袁","startTime":"2018-09-27 10:38:00.0","endTime":"2023-01-01 00:00:00.0","statu":0,"createtime":1538015915000}]
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
             * id : 3d16e9bd-522a-44bb-a7c7-e700bd00dcfd
             * lockId : 3352dc20-46d3-40a6-84ba-5f245d48e860
             * userId : 69880820011
             * childUsername : 18665261827
             * parentId : 18365408378
             * keyName : 测试袁
             * startTime : 2018-09-27 10:38:00.0
             * endTime : 2023-01-01 00:00:00.0
             * statu : 0
             * createtime : 1538015915000
             */

            private String id;
            private String lockId;
            private String userId;
            private String childUsername;
            private String parentId;
            private String keyName;
            private String startTime;
            private String endTime;
            private int statu;
            private long createtime;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getLockId() {
                return lockId;
            }

            public void setLockId(String lockId) {
                this.lockId = lockId;
            }

            public String getUserId() {
                return userId;
            }

            public void setUserId(String userId) {
                this.userId = userId;
            }

            public String getChildUsername() {
                return childUsername;
            }

            public void setChildUsername(String childUsername) {
                this.childUsername = childUsername;
            }

            public String getParentId() {
                return parentId;
            }

            public void setParentId(String parentId) {
                this.parentId = parentId;
            }

            public String getKeyName() {
                return keyName;
            }

            public void setKeyName(String keyName) {
                this.keyName = keyName;
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

            public int getStatu() {
                return statu;
            }

            public void setStatu(int statu) {
                this.statu = statu;
            }

            public long getCreatetime() {
                return createtime;
            }

            public void setCreatetime(long createtime) {
                this.createtime = createtime;
            }
        }
    }
}
