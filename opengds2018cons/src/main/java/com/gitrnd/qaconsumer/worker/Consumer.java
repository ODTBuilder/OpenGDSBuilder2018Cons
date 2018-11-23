package com.gitrnd.qaconsumer.worker;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.gitrnd.qaconsumer.qa.file.service.QAFileService;
import com.gitrnd.qaconsumer.qa.mobile.service.QAMobileService;
import com.gitrnd.qaconsumer.qa.web.service.QAService;

@Component
public class Consumer {

	@Autowired
	@Qualifier("webService")
	QAService webService;

	@Autowired
	@Qualifier("fileService")
	QAFileService fileService;

	@Autowired
	@Qualifier("mobileService")
	QAMobileService mobileService;

	@RabbitListener(queues = "${gitrnd.rabbitmq.queue}")
	public void recievedWebMessage(String msg) throws Throwable {

		System.out.println(msg);
		// parse parameter
		JSONParser jsonP = new JSONParser();
		JSONObject param = (JSONObject) jsonP.parse(msg);

		String type = (String) param.get("type");

		if (type.equals("file")) {
			fileService.validate(param);
		}
		if (type.equals("web")) {
			webService.validate(param);
		}

	}

//	@RabbitListener(queues = "${gitrnd.rabbitmq.queue}")
//	public Object recievedWebMessage(String msg) throws Throwable {
//
//		System.out.println(msg);
//		// parse parameter
//		JSONParser jsonP = new JSONParser();
//		JSONObject param = (JSONObject) jsonP.parse(msg);
//
//		String type = (String) param.get("type");
//
//		if (type.equals("file")) {
//			return fileService.validate(param);
//		}
//		if (type.equals("web")) {
//			return webService.validate(param);
//		}
//		if (type.equals("mobile")) {
//			return mobileService.validate(param);
//		} else {
//			return null;
//		}
//	}
}