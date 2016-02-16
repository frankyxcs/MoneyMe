package com.devmoroz.moneyme.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


import java.util.UUID;

@DatabaseTable(tableName = "categories")
public class Category {

    @DatabaseField(generatedId = true)
    private UUID id;

    @DatabaseField(canBeNull = false)
    private int color;

    @DatabaseField(canBeNull = false)
    private String title;

    @DatabaseField(canBeNull = false)
    private boolean custom;

    @DatabaseField
    private int order;

    public Category() {
        this.custom = false;
    }

    public Category(int color, String title, boolean custom, int order) {
        this.color = color;
        this.title = title;
        this.custom = custom;
        this.order = order;
    }

    public Category(int color, String title, int order) {
        this.color = color;
        this.title = title;
        this.custom = false;
        this.order = order;
    }

    public String getId() {
        return id.toString();
    }

    public void setId(String id) {
        this.id = UUID.fromString(id);
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isCustom() {
        return custom;
    }

    public void setCustom(boolean custom) {
        this.custom = custom;
    }

    public int getOrder() {
        return order;
    }
}
