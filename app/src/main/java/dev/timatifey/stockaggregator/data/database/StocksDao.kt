package dev.timatifey.stockaggregator.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import dev.timatifey.stockaggregator.data.model.Stock

@Dao
interface StocksDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun addStock(stock: Stock)

    @Query("SELECT * FROM stocks_table ORDER BY id ASC")
    fun readAllData(): LiveData<List<Stock>>

    @Query("SELECT * FROM stocks_table WHERE isFavourite = 1 ORDER BY id ASC")
    fun readFavouriteStocks(): LiveData<List<Stock>>

    @Query("SELECT * FROM stocks_table WHERE ticker LIKE :searchQuery OR name LIKE :searchQuery")
    fun searchRequest(searchQuery: String): LiveData<List<Stock>>

    @Update
    suspend fun updateStock(stock: Stock)

}