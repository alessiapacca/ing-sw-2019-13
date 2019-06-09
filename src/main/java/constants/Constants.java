package constants;

import java.io.File;

public class Constants {
    public static final int MAX_HP = 12;
    public static final int MAX_MARKS = 3;
    public static final int NUM_FIGURES = 5;
    public static final int MAX_AMMO_CUBES_PER_COLOR = 3;
    public static final int MAX_NUMBER_OF_DEATHS = 6;
    public static final int[] POINT_VALUE = {8,6,4,2,1,1};
    public static final int[] INITIAL_AMMO = {1,1,1};

    public static final int NUMBER_OF_WEAPONS = 21;
    public static final int NUMBER_OF_POWERUP_CARDS = 24;
    public static final int NUMBER_OF_WEAPON_PER_SPAWN_POINT = 3;

    public static final int DEATH_THRESHOLD = 10;

    public static final int NUMBER_OF_AMMOTILE = 36;

    public static final int BETTERCOLLECTDAMAGE = 2;
    public static final int BETTERSHOOTDAMAGE = 5;

    public static final int FIRST_POWERUP_EXTRA = 0;
    public static final int SECOND_POWERUP_EXTRA = 1;
    public static final int THIRD_POWERUP_EXTRA = 2;
    public static final int MAX_MOVES_BETTER_COLLECT = 2;

    public static final int MAX_MOVES_NORMAL_COLLECT = 1;

    public static final int MAX_MOVES_BETTER_SHOOT = 1;

    public static final int MAX_WEAPON_HAND = 3;
    public static final int MAX_POWERUP_HAND = 3;

    public static final int TARGET_SCOPE_DMG = 1;


    public static final String TARGETING_SCOPE = "Targeting Scope";

    public static final int NO_CHOICE = -99999;

    public static final String ANSI_RESET = "\u001B[0m";


    public static final String CUBE = "∎";





    /////////////////////////////////////////////////////////////////////////////////

    public static final String PATH_TO_RESOURCES_FOLDER = "." + File.separatorChar + "src" +
            File.separatorChar + "main" + File.separatorChar + "resources";

    public static final String PATH_TO_AMMOTILE_JSON =  PATH_TO_RESOURCES_FOLDER +
            File.separatorChar + "ammo_tiles.json";

    public static final String PATH_TO_POWERUP_JSON =  PATH_TO_RESOURCES_FOLDER +
            File.separatorChar + "powerups.json";

    public static final String PATH_TO_WEAPONS_JSON =  PATH_TO_RESOURCES_FOLDER +
            File.separatorChar + "weapons.json";

    public static final String PATH_TO_MAP_11 = PATH_TO_RESOURCES_FOLDER +
            File.separatorChar + "maps" + File.separatorChar + "map11.txt";

    public static final String PATH_TO_MAP_12 = PATH_TO_RESOURCES_FOLDER +
            File.separatorChar + "maps" + File.separatorChar + "map12.txt";

    public static final String PATH_TO_MAP_21 = PATH_TO_RESOURCES_FOLDER +
            File.separatorChar + "maps" + File.separatorChar + "map21.txt";

    public static final String PATH_TO_MAP_22 = PATH_TO_RESOURCES_FOLDER +
            File.separatorChar + "maps" + File.separatorChar + "map22.txt";

}
