package com.devmoroz.moneyme.eventBus;

import com.squareup.otto.Bus;

public class BusProvider {

    public static final Bus BUS = new Bus();

    public static Bus getInstance() {
        return BUS;
    }

    private BusProvider() {

    }
}
