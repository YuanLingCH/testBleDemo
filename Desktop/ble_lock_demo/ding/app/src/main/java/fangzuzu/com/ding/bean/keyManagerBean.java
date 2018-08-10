package fangzuzu.com.ding.bean;

import java.util.List;

/**
 * Created by lingyuan on 2018/7/3.
 */

public class keyManagerBean  {

    /**
     * code : 1001
     * msg : null
     * data : {"recordsTotal":2,"pageSize":10,"totalPage":1,"data":[{"id":"8ddc67f9-7122-40ea-83a6-63a335e01f29","userId":"69715640858","parentId":null,"keyName":"aaaaaaaa","startTime":null,"endTime":null},{"id":"c8b4a43c-9d57-4a75-83cc-fc3737708b55","userId":"69712753416","parentId":"8ddc67f9-7122-40ea-83a6-63a335e01f29","keyName":"科技左","startTime":"2018-08-01 00:00:00.0","endTime":"2019-01-01 00:00:00.0"}]}
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
         * recordsTotal : 2
         * pageSize : 10
         * totalPage : 1
         * data : [{"id":"8ddc67f9-7122-40ea-83a6-63a335e01f29","userId":"69715640858","parentId":null,"keyName":"aaaaaaaa","startTime":null,"endTime":null},{"id":"c8b4a43c-9d57-4a75-83cc-fc3737708b55","userId":"69712753416","parentId":"8ddc67f9-7122-40ea-83a6-63a335e01f29","keyName":"科技左","startTime":"2018-08-01 00:00:00.0","endTime":"2019-01-01 00:00:00.0"}]
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
             * id : 8ddc67f9-7122-40ea-83a6-63a335e01f29
             * userId : 69715640858
             * parentId : null
             * keyName : aaaaaaaa
             * startTime : null
             * endTime : null
             */

            private String id;
            private String userId;
            private Object parentId;
            private String keyName;
            private Object startTime;
            private Object endTime;

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

            public Object getParentId() {
                return parentId;
            }

            public void setParentId(Object parentId) {
                this.parentId = parentId;
            }

            public String getKeyName() {
                return keyName;
            }

            public void setKeyName(String keyName) {
                this.keyName = keyName;
            }

            public Object getStartTime() {
                return startTime;
            }

            public void setStartTime(Object startTime) {
                this.startTime = startTime;
            }

            public Object getEndTime() {
                return endTime;
            }

            public void setEndTime(Object endTime) {
                this.endTime = endTime;
            }
        }
    }
}
