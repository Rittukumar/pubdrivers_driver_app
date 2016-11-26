package com.oceanstyxx.pddriver.model;

import java.math.BigDecimal;
/**
 * Created by mohsin on 13/11/16.
 */

public class Billing {

    private Integer id;

    private Integer drive_request_id;

    private Integer quantity;

    private BigDecimal unit_price;

    private BigDecimal total_price;

    private String price_breakup;

    public String getPrice_breakup() {
        return price_breakup;
    }

    public void setPrice_breakup(String price_breakup) {
        this.price_breakup = price_breakup;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDrive_request_id() {
        return drive_request_id;
    }

    public void setDrive_request_id(Integer drive_request_id) {
        this.drive_request_id = drive_request_id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnit_price() {
        return unit_price;
    }

    public void setUnit_price(BigDecimal unit_price) {
        this.unit_price = unit_price;
    }

    public BigDecimal getTotal_price() {
        return total_price;
    }

    public void setTotal_price(BigDecimal total_price) {
        this.total_price = total_price;
    }
}
