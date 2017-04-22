package com.rokid.rkaudioplayer.constants;

/**
 * Created by Bassam on 2016/11/1.
 */

public interface RKMediaPlayerConstants {
    String ACTION_PLAY = "MediaPlayer.PLAY";
    String ACTION_STOP = "MediaPlayer.STOP";
    String ACTION_CLEARQUEUE = "MediaPlay.ClearQueue";


    String RP_TYPE_APPEND = "APPEND";
    String RP_TYPE_REPLACE_ALL = "REPLACE_ALL";
    String RP_TYPE_REPLACE_APPEND = "REPLACE_APPEND";


    class PlayActivity {
        public static String PLAYER_ACTIVITY_IDLE = "IDLE";
        public static String PLAYER_ACTIVITY_LOADING = "LOADING";
        public static String PLAYER_ACTIVITY_PLAYING = "PLAYING";
        public static String PLAYER_ACTIVITY_PAUSED = "PAUSED";
        public static String PLAYER_ACTIVITY_FINISHED = "FINISHED";
        public static String PLAYER_ACTIVITY_STOPPED = "STOPPED";
        public static String PLAYER_ACTIVITY_BUFFER_UNDERRUN = "BUFFER_UNDERRUN";
        public static String PLAYER_ACTIVITY_PENDING = "PENDING";

    }
}
