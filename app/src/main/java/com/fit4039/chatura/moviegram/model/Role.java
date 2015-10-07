package com.fit4039.chatura.moviegram.model;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * Created by hp on 5/13/2015.
 */

//each actor/actress of the movie is stored in this class
public class Role implements Serializable {
    private String characterName;
    private String realName;

    public String getCharacterName() {
        return characterName;
    }

    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }
}
