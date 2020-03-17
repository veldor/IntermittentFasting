package net.veldor.intermittentfasting.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import net.veldor.intermittentfasting.db.entity.Drink;
import net.veldor.intermittentfasting.db.entity.Eat;

import java.util.List;


@Dao
public interface DrinkDao {
    @Query("SELECT * FROM Drink ORDER BY id DESC LIMIT 1")
    Drink getLastDrink();

    @Query("SELECT * FROM Drink")
    List<Drink> getAllDrinks();

    @Insert
    void insert(Drink drink);

    @Delete
    void delete(Drink drink);
}
