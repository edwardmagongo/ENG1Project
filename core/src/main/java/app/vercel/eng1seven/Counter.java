package app.vercel.eng1seven;

public class Counter {
    private int itemsUsed;
    private int hiddenEventsTriggered;

    public void usedItem() {
        itemsUsed++;
    }

    public void eventsTriggered() {
        hiddenEventsTriggered++;
    }

    public int getItemsUsed() {
        return itemsUsed;
    }

    public int getEventsTriggered() {
        return hiddenEventsTriggered;
    }

    public String toString() {
        return "Items used: " + itemsUsed + ". Hidden Events Triggered: " + hiddenEventsTriggered;
    }
}
