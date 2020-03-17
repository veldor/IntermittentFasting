package net.veldor.intermittentfasting.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Drink {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public long drinkTime;
}
