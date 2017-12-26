package com.drivingassisstantHouse.library.base;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;


/**
 * @param <T>
 * @author sunJi
 *         <p>
 *         简单通用型Adapter(继承型),用以显示多个item
 *
 *     子类一定要实现getViewTypeCount和getItemType方法，否则将无法实现多种item
 */
public abstract class BSimpleMultiEAdapter<T> extends BaseAdapter {

    /**
     * 数据源
     */
    protected List<T> mDatas;
    /**
     * 上下文
     */
    protected Context mContext;


    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }


    public List<T> getmDatas() {
        return mDatas;
    }

    public void setmDatas(List<T> mDatas) {
        this.mDatas = mDatas;
    }


    /**
     * @param context 上下文
     * @param datas   数据源
     */
    public BSimpleMultiEAdapter(Context context, List<T> datas) {
        this.mContext = context;
        this.mDatas = datas;

    }

    /**
     * 数据源改变，刷新界面
     *
     * @param datas
     */
    public void refersh(List<T> datas) {
        this.mDatas = datas;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mDatas != null)
            return mDatas.size();
        else
            return 0;
    }

    @Override
    public T getItem(int position) {
        if (mDatas != null) {
            return mDatas.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);

        SimpleAdapterHolder holder = getHolder(convertView, parent, type, position);
        covertView(holder, type, position, mDatas, getItem(position));
        return holder.getmConvertView();
    }

    /***
     * 根据不同的类型关联不同layout，并返回相应的holder
     * @param convertView
     * @param parent
     * @param type item的类型
     * @param position
     * @return
     */
    public abstract SimpleAdapterHolder getHolder(View convertView, ViewGroup parent, int type, int position);

    //可以把此类变成抽象类，让子类继承时在里面处理方法，不过使用接口更灵活，因为Activiy也可以实现interface
    public abstract void covertView(SimpleAdapterHolder holder, int type, int position, List<T> datas,
                                    T obj);

//	public interface AdapterInter<T> {
//		public void covertView(SimpleAdapterHolder holder, int position, List<T> datas, Object obj);
//	}


}
