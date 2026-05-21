package com.adventure.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * Observer pattern: broadcast game events to subscribed players.
 */
public class GameEventPublisher {
    private final List<Observer> observers = new ArrayList<>();

    public void subscribe(Observer observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void unsubscribe(Observer observer) {
        observers.remove(observer);
    }

    public void notify(String message) {
        notify(message, null);
    }

    public void notify(String message, Observer exclude) {
        for (Observer observer : observers) {
            if (observer != exclude) {
                observer.update(message);
            }
        }
    }

    public int getObserverCount() {
        return observers.size();
    }
}
