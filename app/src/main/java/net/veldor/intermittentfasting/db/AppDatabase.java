package net.veldor.intermittentfasting.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import net.veldor.intermittentfasting.db.dao.PeriodDao;
import net.veldor.intermittentfasting.db.entity.Period;

@Database(entities = {Period.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract PeriodDao periodDao();
    };
