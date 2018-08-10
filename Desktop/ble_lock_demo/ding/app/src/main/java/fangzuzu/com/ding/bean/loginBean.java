package fangzuzu.com.ding.bean;

/**
 * Created by lingyuan on 2018/6/1.
 */

public class loginBean {

    /**
     * code : 1001
     * msg : null
     * data : {"id":"40770883-7112-11e8-b04f-00163e0c1269","uid":"73efb549-6127-4156-8aae-c4f4220e66dd","idcard":null,"nickname":null,"idcardName":null,"gender":null,"age":null,"prefession":null,"headImgUrl":null,"partid":null,"username":"18617145277"}
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
         * id : 40770883-7112-11e8-b04f-00163e0c1269
         * uid : 73efb549-6127-4156-8aae-c4f4220e66dd
         * idcard : null
         * nickname : null
         * idcardName : null
         * gender : null
         * age : null
         * prefession : null
         * headImgUrl : null
         * partid : null
         * username : 18617145277
         */

        private String id;
        private String uid;
        private Object idcard;
        private Object nickname;
        private Object idcardName;
        private Object gender;
        private Object age;
        private Object prefession;
        private Object headImgUrl;
        private Object partid;
        private String username;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public Object getIdcard() {
            return idcard;
        }

        public void setIdcard(Object idcard) {
            this.idcard = idcard;
        }

        public Object getNickname() {
            return nickname;
        }

        public void setNickname(Object nickname) {
            this.nickname = nickname;
        }

        public Object getIdcardName() {
            return idcardName;
        }

        public void setIdcardName(Object idcardName) {
            this.idcardName = idcardName;
        }

        public Object getGender() {
            return gender;
        }

        public void setGender(Object gender) {
            this.gender = gender;
        }

        public Object getAge() {
            return age;
        }

        public void setAge(Object age) {
            this.age = age;
        }

        public Object getPrefession() {
            return prefession;
        }

        public void setPrefession(Object prefession) {
            this.prefession = prefession;
        }

        public Object getHeadImgUrl() {
            return headImgUrl;
        }

        public void setHeadImgUrl(Object headImgUrl) {
            this.headImgUrl = headImgUrl;
        }

        public Object getPartid() {
            return partid;
        }

        public void setPartid(Object partid) {
            this.partid = partid;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }
}
