package cn.itcast.emim.base;

public interface Const {

    public static final String ITEM_INVITE_AND_NOTIFICATION = "申请与通知";
    public static final String ITEM_IM_HELPER = "环信小助手";
    public static final String ITEM_ARROW_UP = "↑";

    /**
     * 添加联系人
     */
    public static final int ACTION_ADD_CONTACT = 1;
    /**
     * 删除联系人
     */
    public static final int ACTION_DEL_CONTACT = 2;


    /** 网络错误 */
    public static final int NETWORK_ERROR = 3;
    /** 网络重新连接上 */
    public static final int NETWORK_CONNECT_SUCCESS= 4;

    /** 重新发送消息 */
    public static final int RESEND_SMS = 5;

}
