package com.devmoroz.moneyme.eventBus;


public class SearchTriggered {
    public String term;

    public SearchTriggered(String term) {
        this.term = term;
    }
}
