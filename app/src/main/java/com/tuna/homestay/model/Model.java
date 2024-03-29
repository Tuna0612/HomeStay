package com.tuna.homestay.model;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class Model {
    @Exclude
    public String id;

    public <T extends Model> T withId(@NonNull final String id) {
        this.id = id;
        return (T) this;
    }
}
