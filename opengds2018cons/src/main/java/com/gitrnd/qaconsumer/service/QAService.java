package com.gitrnd.qaconsumer.service;

import org.json.simple.JSONObject;

public interface QAService {

	public boolean validate(JSONObject param) throws Throwable;

}
