package com.bely.kongplayer.shared;

import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.util.Log;

import androidx.media.MediaBrowserServiceCompat;

import java.util.ArrayList;
import java.util.List;

public class BrowseListProvider {
    final static String MEDIA_ID_ROOT = "root";
    final static String TAG =  "BrowseListProvider";
    public static void loadChildrenImpl_Testing(final String parentMediaId,
                                          final MediaBrowserServiceCompat.Result<List<MediaBrowserCompat.MediaItem>> result) {
        List<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();
        //root
        if (MEDIA_ID_ROOT.equals(parentMediaId)) {
            Log.d(TAG, "OnLoadChildren.ROOT");
            mediaItems.add(new MediaBrowserCompat.MediaItem(
                    new MediaDescriptionCompat.Builder()
                            .setMediaId("__LEVEL_1_1")
                            .setTitle("Artist")
                            .build(), MediaBrowserCompat.MediaItem.FLAG_BROWSABLE));
            mediaItems.add(new MediaBrowserCompat.MediaItem(
                    new MediaDescriptionCompat.Builder()
                            .setMediaId("__LEVEL_1_2")
                            .setTitle("Album")
                            .build(), MediaBrowserCompat.MediaItem.FLAG_BROWSABLE));
            //second menu
        }else if ("__LEVEL_1_1".equals(parentMediaId)) {
            //artist
            mediaItems.add(new MediaBrowserCompat.MediaItem(
                    new MediaDescriptionCompat.Builder()
                            .setMediaId("__LEVEL_2_1_1")
                            .setTitle("Taylor Swift")
                            .build(), MediaBrowserCompat.MediaItem.FLAG_BROWSABLE));
            mediaItems.add(new MediaBrowserCompat.MediaItem(
                    new MediaDescriptionCompat.Builder()
                            .setMediaId("__LEVEL_2_1_2")
                            .setTitle("Lady Gaga")
                            .build(), MediaBrowserCompat.MediaItem.FLAG_BROWSABLE));
        }else if ("__LEVEL_1_2".equals(parentMediaId)) {
            //album
            mediaItems.add(new MediaBrowserCompat.MediaItem(
                    new MediaDescriptionCompat.Builder()
                            .setMediaId("__LEVEL_2_2_1")
                            .setTitle("Culture")
                            .build(), MediaBrowserCompat.MediaItem.FLAG_BROWSABLE));
            mediaItems.add(new MediaBrowserCompat.MediaItem(
                    new MediaDescriptionCompat.Builder()
                            .setMediaId("__LEVEL_2_2_2")
                            .setTitle("Divide")
                            .build(), MediaBrowserCompat.MediaItem.FLAG_BROWSABLE));
            //third menu
        }else if ("__LEVEL_2_1_1".equals(parentMediaId)) {
            //Taylor swift
            mediaItems.add(new MediaBrowserCompat.MediaItem(
                    new MediaDescriptionCompat.Builder()
                            .setMediaId("__LEVEL_3_1_1_1")
                            .setTitle("Taylor swift Song1")
                            .build(), MediaBrowserCompat.MediaItem.FLAG_BROWSABLE));
            mediaItems.add(new MediaBrowserCompat.MediaItem(
                    new MediaDescriptionCompat.Builder()
                            .setMediaId("__LEVEL_3_1_1_2")
                            .setTitle("Taylor swift Song2")
                            .build(), MediaBrowserCompat.MediaItem.FLAG_BROWSABLE));
            mediaItems.add(new MediaBrowserCompat.MediaItem(
                    new MediaDescriptionCompat.Builder()
                            .setMediaId("__LEVEL_3_1_1_3")
                            .setTitle("Taylor swift Song3")
                            .build(), MediaBrowserCompat.MediaItem.FLAG_BROWSABLE));
        }else if ("__LEVEL_2_1_2".equals(parentMediaId)) {
            //Lady Gaga
            mediaItems.add(new MediaBrowserCompat.MediaItem(
                    new MediaDescriptionCompat.Builder()
                            .setMediaId("__LEVEL_3_2_1_1")
                            .setTitle("Lady Gaga Song1")
                            .build(), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE));
            mediaItems.add(new MediaBrowserCompat.MediaItem(
                    new MediaDescriptionCompat.Builder()
                            .setMediaId("__LEVEL_3_2_1_2")
                            .setTitle("Lady Gaga Song2")
                            .build(), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE));
            mediaItems.add(new MediaBrowserCompat.MediaItem(
                    new MediaDescriptionCompat.Builder()
                            .setMediaId("__LEVEL_3_2_1_3")
                            .setTitle("Lady Gaga Song3")
                            .build(), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE));
        }else if ("__LEVEL_2_2_1".equals(parentMediaId)) {
            //Album Culture
            mediaItems.add(new MediaBrowserCompat.MediaItem(
                    new MediaDescriptionCompat.Builder()
                            .setMediaId("__LEVEL_3_3_1_1")
                            .setTitle("Album Culture Song1")
                            .build(), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE));
            mediaItems.add(new MediaBrowserCompat.MediaItem(
                    new MediaDescriptionCompat.Builder()
                            .setMediaId("__LEVEL_3_3_1_2")
                            .setTitle("Album Culture Song2")
                            .build(), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE));
            mediaItems.add(new MediaBrowserCompat.MediaItem(
                    new MediaDescriptionCompat.Builder()
                            .setMediaId("__LEVEL_3_3_1_3")
                            .setTitle("Album Culture Song3")
                            .build(), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE));
        }else if ("__LEVEL_2_2_2".equals(parentMediaId)) {
            //Album Divide
            mediaItems.add(new MediaBrowserCompat.MediaItem(
                    new MediaDescriptionCompat.Builder()
                            .setMediaId("__LEVEL_3_3_1_1")
                            .setTitle("Album Divide Song1")
                            .build(), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE));
            mediaItems.add(new MediaBrowserCompat.MediaItem(
                    new MediaDescriptionCompat.Builder()
                            .setMediaId("__LEVEL_3_3_1_2")
                            .setTitle("Album Divide Song2")
                            .build(), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE));
            mediaItems.add(new MediaBrowserCompat.MediaItem(
                    new MediaDescriptionCompat.Builder()
                            .setMediaId("__LEVEL_3_3_1_3")
                            .setTitle("Album Divide Song3")
                            .build(), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE));
        }
        result.sendResult(mediaItems);
    }

}
