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

/**
 * Consumer.
 * <p>
 * 웹(Geoserver), 파일, 모바일 검수 수행
 * 
 * @author DY.Oh
 *
 */
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

//	@Autowired
//	@Qualifier("generalizationService")
//	GeneralizationService generalizationService;

	/**
	 * Producer로부터 검수 Queue Message를 받는 메스드.
	 * 
	 * @param msg Queue Message갑작던
	 * @throws Throwable Throwablelej
	 * 
	 * @author DY.r
	 */
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

//		JSONParser p = new JSONParser();
//		Object obj = null;
//		try {
//			obj = p.parse(new FileReader("D:\\일반화\\option.json"));
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		JSONObject fileObj = (JSONObject) obj;
//		JSONObject preset = (JSONObject) fileObj.get("preset");
//		JSONObject param = (JSONObject) p.parse(msg);
//		
//		
//		

	}

	/**
	 * Producer로부터 모바일 검수 Queue Message를 받는 메스드.
	 * <p>
	 * Message로 검수 요청 및 검수 결과 반환
	 * 
	 * @param msg 모바일 Queue Message
	 * @return 검수 결과 JSONObject
	 * @throws Throwable Throwable
	 * 
	 * @author DY.Oh
	 */
	@RabbitListener(queues = "${gitrnd.rabbitmq.mobilequeue}")
	public Object recievedWebMessageMobile(String msg) throws Throwable {

		System.out.println(msg);
		// parse parameter
		JSONParser jsonP = new JSONParser();
		JSONObject param = (JSONObject) jsonP.parse(msg);

		String type = (String) param.get("type");
		if (type.equals("mobile")) {
			return mobileService.validate(param);
		} else {
			return null;
		}
	}

//	@RabbitListener(queues = "${gitrnd.rabbitmq.generalizationqueue}")
//	public void recievedWebMessageGeneralization(String msg) throws Throwable {
//
//		System.out.println(msg);
//		// parse parameter
//		JSONParser jsonP = new JSONParser();
//		JSONObject param = (JSONObject) jsonP.parse(msg);
//
//		generalizationService.excute(param);
//	}
}