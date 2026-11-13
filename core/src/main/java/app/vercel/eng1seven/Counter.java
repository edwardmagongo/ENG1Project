package app.vercel.eng1seven;

public class Counter {
    private int itemsUsed;
    private int checkpointEventsTriggered;
    private int hiddenEventsTriggered;

    public void usedItem() {
        itemsUsed++;
    }

    public void chkpntsTriggered() {
        checkpointEventsTriggered++;
    }

    public void hiddenEventTriggered() {
        hiddenEventsTriggered++;
    }

    public int getItemsUsed() {
        return itemsUsed;
    }

    public int getchkpntsTriggered() {
        return checkpointEventsTriggered;
    }

    public int getHiddenEventsTriggered() {
        return hiddenEventsTriggered;
    }

    public String toString() {
        return "Items used: " + itemsUsed + ". Checkpoint Events Triggered: " + checkpointEventsTriggered
         + ". Hidden Events Triggered: " + hiddenEventsTriggered;
    }
}
