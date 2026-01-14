package com.gportal.a2s;

public interface Query extends Message {
	public Integer challenge();
	public Query withChallenge(int challenge);
}
