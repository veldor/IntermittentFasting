package net.veldor.intermittentfasting.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import net.veldor.intermittentfasting.db.entity.Eat;
import net.veldor.intermittentfasting.db.entity.Period;

import java.util.List;


@Dao
public interface EatDao {
    @Query("SELECT * FROM Eat ORDER BY id DESC LIMIT 1")
    Eat getLastEat();

    @Query("SELECT * FROM Eat")
    List<Eat> getAllEats();

    @Insert
    void insert(Eat eat);

    @Delete
    void delete(Eat eat);
}
