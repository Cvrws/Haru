package cc.unknown.event.impl.other;

import cc.unknown.event.impl.Event;

public class KeyEvent extends Event {
    private int key;

    public KeyEvent(int key) {
        this.key = key;
    }

    public void setKey(int key) {
		this.key = key;
	}

	public int getKey() {
		return key;
	}
}