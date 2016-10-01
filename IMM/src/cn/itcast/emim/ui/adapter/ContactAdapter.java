package cn.itcast.emim.ui.adapter;

import android.content.Context;
import android.view.ViewGroup;

import java.util.List;

import cn.em.sdk.bean.EaseUser;
import cn.itcast.emim.ui.holder.BaseHolder;
import cn.itcast.emim.ui.holder.ContactHolder;

/**
 * Created by Shur on 2016/9/27.
 */

public class ContactAdapter extends MyBaseAdapter<EaseUser> {

    public ContactAdapter(Context context, List listdatas) {
        super(context, listdatas);
    }

    /**
     * 创建holder
     *
     * @param parent
     * @param data
     * @return
     */
    @Override
    public BaseHolder onCreateViewHolder(ViewGroup parent, EaseUser data) {
        return new ContactHolder(mContext, parent, this);
    }

    /**
     * 根据列表项位置获取首字母
     *
     * @param position
     * @return
     */
    public int getCharacter(int position) {
        EaseUser item = getItem(position);
        return item.getInitialLetter().charAt(0);
    }

    /**
     * 获取首字母在列表中第一次出现的列表项位置
     *
     * @param character
     * @return
     */
    public int getPosition(int character) {
        for (int i = 0; i < getCount(); i++) {
            EaseUser item = getItem(i);
            if (item.getInitialLetter().charAt(0) == character) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 判断列表项实体的首字母在列表中是否是第一次出现
     *
     * @param position
     * @return
     */
    public boolean isFirstCharacter(int position) {
        int character = getCharacter(position);
        int characterPos = getPosition(character);
        // 两个位置相同表示该首字母在列表中是第一次出现，则应该显示
        return position == characterPos;
    }

    /**
     * 删除一个联系人，刷新列表显示
     *
     * @param user
     */
    public void removeContact(EaseUser user) {
        mListDatas.remove(user);
        notifyDataSetChanged(); // 刷新列表显示
    }
}
