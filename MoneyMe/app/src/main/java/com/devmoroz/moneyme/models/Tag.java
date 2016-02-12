package com.devmoroz.moneyme.models;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.UUID;

@DatabaseTable(tableName = "tags")
public class Tag {

    @DatabaseField(generatedId = true)
    UUID id;

    @DatabaseField
    String title;

    @DatabaseField
    String categoryId;

    public Tag() {
    }

    public Tag(String title, String categoryId) {
        this.title = title;
        this.categoryId = categoryId;
    }

    public Tag(UUID id, String title, String categoryId) {
        this.id = id;
        this.title = title;
        this.categoryId = categoryId;
    }
}
