package com.gitrnd.qaconsumer.service;

import org.json.simple.JSONObject;

public interface QAFileService {

	public boolean validate(JSONObject param) throws Throwable;

}
