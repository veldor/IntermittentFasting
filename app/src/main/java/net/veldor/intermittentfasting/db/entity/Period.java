package net.veldor.intermittentfasting.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity
public class Period {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @NotNull
    public String periodType = "";

    public long periodStart;

    public long periodFinish;
}
