package com.mito.exobj.lib.input;

public class BB_Key {

	public int ikey = 0;

	public BB_Key(boolean ctrl, boolean shift, boolean alt) {
		int b = 0;

		if (ctrl) {
			b = b | 1;
		}
		if (shift) {
			b = b | 2;
		}
		if (alt) {
			b = b | 4;
		}
		this.ikey = b;
	}

	public BB_Key(int key) {
		this.ikey = key;
	}

	public boolean isControlPressed() {
		return (ikey & 1) == 1;
	}

	public boolean isShiftPressed() {
		return (ikey & 2) == 2;
	}

	public boolean isAltPressed() {
		return (ikey & 4) == 4;
	}

}
