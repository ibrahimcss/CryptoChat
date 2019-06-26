package com.example.cryptochat.DatabaseUserName;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface FriendNameChannelDao {

    @Query("SELECT * FROM friendnamechannel")
    List<FriendNameChannel> getAll();

    @Query("SELECT * FROM friendnamechannel where user_name LIKE  :userName")
    FriendNameChannel findByName(String userName);

    @Query("SELECT COUNT(*) from friendnamechannel")
    int countUsers();

    @Insert
    void insertAll(FriendNameChannel... friendNameChannels);

    @Query("DELETE FROM friendnamechannel where user_name LIKE  :userName")
    void delete(String userName);

}
