package net.veldor.intermittentfasting.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity
public class Eat {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public long eatTime;
}
