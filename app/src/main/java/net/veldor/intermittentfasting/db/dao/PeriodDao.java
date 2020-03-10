package net.veldor.intermittentfasting.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import net.veldor.intermittentfasting.db.entity.Period;

import java.util.List;


@Dao
public interface PeriodDao {


    @Query("SELECT * FROM Period")
    List<Period> getAllPeriods();

    @Insert
    void insert(Period period);

    @Delete
    void delete(Period period);

/*    @Update
    void update(ReadedBooks book);
*/
}
