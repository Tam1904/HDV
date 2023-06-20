package com.sfin.message.messagegateway.response;

import lombok.Data;

import java.util.List;

@Data
public class ShopTemplateResponse {

    Integer error;
    String message;
    List<DataTemplate> data;
    MetaData metadata;
}


class MetaData{
    Integer total;

    public MetaData() {
    }

    public MetaData(Integer total) {
        this.total = total;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
