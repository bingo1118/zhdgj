package com.smart.cloud.fire.view.dataSelector.expandableListView;

import com.smart.cloud.fire.view.dataSelector.BingoViewModel;

import java.util.List;

public class DataEntity {
    private String id;
    private String name;//一级列表内容
    private List<BingoViewModel> childrenDataList;


    public DataEntity(String title, List<BingoViewModel> childrenDataList) {
        this.name = title;
        this.childrenDataList = childrenDataList;
    }

    public List<BingoViewModel> getChildrenDataList() {
        return childrenDataList;
    }

    public String getTitle() {
        return name;
    }

    public void setTitle(String title) {
        this.name = title;
    }

    public void setChildrenDataList(List<BingoViewModel> childrenDataList) {
        this.childrenDataList = childrenDataList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
