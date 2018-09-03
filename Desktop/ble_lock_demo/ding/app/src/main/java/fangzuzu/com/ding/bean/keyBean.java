package fangzuzu.com.ding.bean;

import java.util.List;

/**
 * Created by lingyuan on 2018/9/3.
 */

public class keyBean {

    /**
     * code : 1001
     * msg : null
     * data : {"recordsTotal":1,"pageSize":10,"totalPage":1,"data":[{"id":"0604be4f-142d-443c-8f75-c656242f6213","userId":"69880820011","parentId":"a2d68fc9-adbc-4055-b283-604c99f2717f","keyName":"测试","startTime":"2018-09-03 11:04:00.0","endTime":"2019-01-01 00:00:00.0"}]}
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
         * data : [{"id":"0604be4f-142d-443c-8f75-c656242f6213","userId":"69880820011","parentId":"a2d68fc9-adbc-4055-b283-604c99f2717f","keyName":"测试","startTime":"2018-09-03 11:04:00.0","endTime":"2019-01-01 00:00:00.0"}]
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
             * id : 0604be4f-142d-443c-8f75-c656242f6213
             * userId : 69880820011
             * parentId : a2d68fc9-adbc-4055-b283-604c99f2717f
             * keyName : 测试
             * startTime : 2018-09-03 11:04:00.0
             * endTime : 2019-01-01 00:00:00.0
             */

            private String id;
            private String userId;
            private String parentId;
            private String keyName;
            private String startTime;
            private String endTime;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getUserId() {
                return userId;
            }

            public void setUserId(String userId) {
                this.userId = userId;
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
        }
    }
}
