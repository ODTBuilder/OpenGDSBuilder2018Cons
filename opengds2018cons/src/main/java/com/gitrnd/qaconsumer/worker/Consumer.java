package com.gitrnd.qaconsumer.worker;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.gitrnd.qaconsumer.qa.file.service.QAFileService;
import com.gitrnd.qaconsumer.qa.mobile.service.QAMobileService;

@Component
public class Consumer {

	// @Autowired
	// QAService webService;

	@Autowired
	@Qualifier("fileService")
	QAFileService fileService;

	@Autowired
	@Qualifier("mobileService")
	QAMobileService mobileService;

	@RabbitListener(queues = "${gitrnd.rabbitmq.queue}")
	public Object recievedWebMessage(String msg) throws Throwable {

		System.out.println(msg);
		// parse parameter
		JSONParser jsonP = new JSONParser();
		JSONObject param = (JSONObject) jsonP.parse(msg);

		if (param.get("file") != null) {
			return fileService.validate(param);
		}
		if (param.get("web") != null) {
			return null;
		}
		if (param.get("mobile") != null) {
			return mobileService.validate((JSONObject) param.get("mobile"));
		} else {
			return null;
		}
	}
}