package com.devmoroz.moneyme.models;


public enum SyncState {
    None(SyncState.VALUE_NONE),
    Synced(SyncState.VALUE_SYNCED),
    LocalChanges(SyncState.VALUE_LOCAL_CHANGES);

    private static final int VALUE_NONE = 1;
    private static final int VALUE_SYNCED = 3;
    private static final int VALUE_LOCAL_CHANGES = 4;

    private final int value;

    private SyncState(int value) {
        this.value = value;
    }

    public static SyncState fromInt(int value) {
        switch (value) {
            case VALUE_NONE:
                return None;

            case VALUE_SYNCED:
                return Synced;

            case VALUE_LOCAL_CHANGES:
                return LocalChanges;

            default:
                throw new IllegalArgumentException("Value " + value + " is not supported.");
        }
    }

    public int asInt() {
        return value;
    }

    public String asString() {
        return String.valueOf(value);
    }
}
