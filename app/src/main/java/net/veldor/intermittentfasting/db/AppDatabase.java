package net.veldor.intermittentfasting.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import net.veldor.intermittentfasting.db.dao.DrinkDao;
import net.veldor.intermittentfasting.db.dao.EatDao;
import net.veldor.intermittentfasting.db.dao.PeriodDao;
import net.veldor.intermittentfasting.db.entity.Drink;
import net.veldor.intermittentfasting.db.entity.Eat;
import net.veldor.intermittentfasting.db.entity.Period;

@Database(entities = {Period.class, Eat.class, Drink.class}, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    abstract PeriodDao periodDao();
    abstract EatDao eatDao();
    abstract DrinkDao drinkDao();

    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(final SupportSQLiteDatabase database) {
            database.execSQL("create table Eat (id integer primary key autoincrement NOT NULL, eatTime long NOT NULL);");
        }
    };
    public static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(final SupportSQLiteDatabase database) {
            database.execSQL("create table Drink (id integer primary key autoincrement NOT NULL, drinkTime long NOT NULL);");
        }
    };
};
