package com.mg.common.constant;

public class PurchaseConstant {
    public enum PurchaseStatus{
       CREATE(0,"新建"), ASSINGED(1,"已分配"),RECEIVE(2,"已领取"),FINISH(3,"已完成"),ERROR(4,"有异常");
       private int code;
       private String msg;
       PurchaseStatus(int code,String msg){
           this.code=code;
           this.msg=msg;
       }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }



    public enum PurchaseDetailStatus{
        CREATE(0,"新建"),
        ASSINGED(1,"已分配"),
        BUYING(2,"正在采购"),
        FINISH(3,"已完成"),
        ERROR(4,"采购失败");
        private int code;
        private String msg;
        PurchaseDetailStatus(int code,String msg){
            this.code=code;
            this.msg=msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }
}
