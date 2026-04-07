package com.example.demo.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "itemCount")
public class ItemCountResponse {
    private int count;

    public ItemCountResponse() {
    }

    public ItemCountResponse(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
