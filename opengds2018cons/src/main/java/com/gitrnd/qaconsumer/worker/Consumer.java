package com.gitrnd.qaconsumer.worker;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gitrnd.qaconsumer.qa.file.service.QAFileService;
import com.gitrnd.qaconsumer.qa.mobile.service.QAMobileService;

@Component
public class Consumer {

	@Autowired
	QAFileService webService;

	@Autowired
	QAMobileService mobileService;

	@RabbitListener(queues = "${gitrnd.rabbitmq.queue}")
	public void recievedWebMessage(String msg) throws Throwable {

		System.out.println(msg);

		// parse parameter
		JSONParser jsonP = new JSONParser();
		JSONObject param = (JSONObject) jsonP.parse(msg);

		boolean qa = webService.validate(param);
		System.out.println(qa);
	}

	// @RabbitListener(queues = "${gitrnd.rabbitmq.queue}")
	public JSONObject recievedMobileMessage(String msg) throws Throwable {

		System.out.println(msg);

		// parse parameter
		JSONParser jsonP = new JSONParser();
		JSONObject param = (JSONObject) jsonP.parse(msg);

		JSONObject qa = mobileService.validate(param);
		System.out.println(qa);
		return qa;
	}
}