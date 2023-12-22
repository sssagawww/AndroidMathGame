package com.mygdx.game.battle;

public enum ENTITY_LIST {
    PLAYER,
    ENEMY;

    public static ENTITY_LIST getEntities(ENTITY_LIST entityList){
        switch (entityList){
            case PLAYER:
                return ENEMY;
            case ENEMY:
                return PLAYER;
            default:
                return null;
        }
    }
}
