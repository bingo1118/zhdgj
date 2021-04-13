package com.smart.cloud.fire.view.dataSelector;


public class DeviceState implements BingoViewModel {

    private int id;
    private String name;

    public DeviceState(int id, String name) {
        this.id = id;
        this.name = name;
    }


    @Override
    public String getModelId() {
        return id+"";
    }

    @Override
    public String getModelName() {
        return name;
    }
}
