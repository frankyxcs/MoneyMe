package com.devmoroz.moneyme.eventBus;

/**
 * Created by Vitalii_Moroz on 11/17/2015.
 */
public class AppInitCompletedEvent {
    public boolean success;

    public AppInitCompletedEvent(boolean success) {
        this.success = success;
    }
}
