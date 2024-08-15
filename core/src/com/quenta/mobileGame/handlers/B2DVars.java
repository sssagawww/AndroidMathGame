package com.quenta.mobileGame.handlers;

public class B2DVars {
    //направление анимации игрока
    public static class PlayerAnim {
        public static final int DOWN = 0;
        public static final int LEFT = 1;
        public static final int UP = 2;
        public static final int RIGHT = 3;
        public static final int IDLE = -1;
    }

    //pixels per meter - нужно для маштаба мира, 10 пикселей = 1 метр
    public static final float PPM = 10;

    //для фильтра коллизии, что с чем может контактировать
    public static final short BIT_NOTHING = 2;
    public static final short BIT_PLAYER = 4;
    public static final short BIT_TROPA = 8;
    public static final short BIT_RABBIT = 16;
}
