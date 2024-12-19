package com.traders.exchange.web.rest.model;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class WatchListOrderVM implements Serializable {
    private int oldIndex;
    private int newIndex;

    public int getOldIndex() {
        return oldIndex;
    }

    public void setOldIndex(int oldIndex) {
        this.oldIndex = oldIndex;
    }

    public int getNewIndex() {
        return newIndex;
    }

    public void setNewIndex(int newIndex) {
        this.newIndex = newIndex;
    }
}
