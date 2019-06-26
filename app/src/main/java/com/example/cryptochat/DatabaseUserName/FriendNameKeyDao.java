package com.example.cryptochat.DatabaseUserName;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface FriendNameKeyDao {
    @Query("SELECT * FROM friendnamekey")
    List<FriendNameKey> getAll();

    @Query("SELECT * FROM friendnamekey where user_name LIKE  :userName")
    FriendNameKey findByName(String userName);

    @Query("SELECT COUNT(*) from friendnamekey")
    int countUsers();

    @Insert
    void insertAll(FriendNameKey... friendNameKeys);

    @Query("DELETE FROM friendnamekey where user_name LIKE  :userName")
    void delete(String userName);
}
