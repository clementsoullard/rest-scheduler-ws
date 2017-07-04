package com.clement.magichome.scheduler;

public enum TvStatusEnum {
	ON(1), OFF(-1);

	private int serializeValue;

	private TvStatusEnum(int ser) {
		this.serializeValue = ser;
	}

	public int getSerializeValue() {
		return serializeValue;
	}
}
