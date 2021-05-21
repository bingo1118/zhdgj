package com.smart.cloud.fire.view.dataSelector.expandableListView;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.smart.cloud.fire.global.Area;

import java.util.List;

import fire.cloud.smart.com.smartcloudfire.R;

public class MyExpandableAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<Area> dataEntity;
    private DataExpandableSelectorView parentView;

    public MyExpandableAdapter(Context context, List<Area> dataEntity,DataExpandableSelectorView parentView) {
        this.context = context;
        this.dataEntity = dataEntity;
        this.parentView=parentView;
    }

    /**
     * 获取组的数目
     *
     * @return 返回一级列表组的数量
     */
    @Override
    public int getGroupCount() {
        return dataEntity == null ? 0 : dataEntity.size();
    }

    /**
     * 获取指定组中的子节点数量
     *
     * @param groupPosition 子元素组所在的位置
     * @return 返回指定组中的子数量
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        return dataEntity.get(groupPosition).getAreas().size();
    }

    /**
     * 获取与给定组相关联的对象
     *
     * @param groupPosition 子元素组所在的位置
     * @return 返回指定组的子数据
     */
    @Override
    public Object getGroup(int groupPosition) {
        return dataEntity.get(groupPosition).getAreaName();
    }


    /**
     * 获取与给定组中的给定子元素关联的数据
     *
     * @param groupPosition 子元素组所在的位置
     * @param childPosition 子元素的位置
     * @return 返回子元素的对象
     */
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return dataEntity.get(groupPosition).getAreas().get(childPosition);
    }

    /**
     * 获取组在给定位置的ID（唯一的）
     *
     * @param groupPosition 子元素组所在的位置
     * @return 返回关联组ID
     */
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }


    /**
     * 获取给定组中给定子元素的ID(唯一的)
     *
     * @param groupPosition 子元素组所在的位置
     * @param childPosition 子元素的位置
     * @return 返回子元素关联的ID
     */
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    /**
     * @return 确定id 是否总是指向同一个对象
     */
    @Override
    public boolean hasStableIds() {
        return true;
    }

    /**
     * @return 返回指定组的对应的视图 （一级列表样式）
     */
    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ParentHolder parentHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.expandable_parent_item, null);
            parentHolder = new ParentHolder();
            parentHolder.tvParent = convertView.findViewById(R.id.name_tv);
            parentHolder.img_right = convertView.findViewById(R.id.img_right);
            convertView.setTag(parentHolder);
        } else {
            parentHolder = (ParentHolder) convertView.getTag();
        }
        parentHolder.tvParent.setText(dataEntity.get(groupPosition).getAreaName());

        //共用一个右箭头，如果展开则顺时针旋转90°选择，否则不旋转
        if (isExpanded) parentHolder.img_right.setRotation(90F);
        else parentHolder.img_right.setRotation(0F);

        return convertView;
    }

    /**
     * @return 返回指定位置对应子视图的视图
     */
    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final ChildrenHolder childrenHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.expandable_childrens_item, null);
            childrenHolder = new ChildrenHolder();
            childrenHolder.tvChild = convertView.findViewById(R.id.name_tv);
            childrenHolder.checkBox = convertView.findViewById(R.id.checkbox);
            convertView.setTag(childrenHolder);
        } else {
            childrenHolder = (ChildrenHolder) convertView.getTag();
        }

        childrenHolder.tvChild.setText(dataEntity.get(groupPosition).getAreas().get(childPosition).getAreaName());
        childrenHolder.tvChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentView.changeStatus(DataExpandableSelectorView.Status.CHECKED,dataEntity.get(groupPosition).getAreas().get(childPosition));
            }
        });

        return convertView;
    }

    /**
     * 指定位置的子元素是否可选
     *
     * @param groupPosition 子元素组所在的位置
     * @param childPosition 子元素的位置
     * @return 返回是否可选
     */

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    class ParentHolder {
        TextView tvParent;
        ImageView img_right;
    }


    class ChildrenHolder {
        TextView tvChild;
        CheckBox checkBox;
    }

}

