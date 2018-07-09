package com.gitrnd.qaconsumer.worker;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gitrnd.qaconsumer.service.QAService;

@Component
public class Consumer {

	@Autowired
	QAService qauService;

	@RabbitListener(queues = "${gitrnd.rabbitmq.queue}")
	public void recievedMessage(String msg) throws Throwable {

		System.out.println(msg);

		// parse parameter
		JSONParser jsonP = new JSONParser();
		JSONObject param = (JSONObject) jsonP.parse(msg);

		boolean qa = qauService.validate(param);
		System.out.println(qa);
	}
}