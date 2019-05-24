/**
 * 
 */
package com.gitrnd.qaconsumer.qa.mobile.service;

import org.json.simple.JSONObject;

/**
 * 모바일 기반 검수 요청 클래스.
 * 
 * @author DY.Oh
 *
 */
public interface QAMobileService {

	/**
	 * 모바일 기반 검수 요청.
	 * 
	 * @param param Producer로부터 전달받은 Message를 {@link JSONObject} 형태로 변환함.
	 * @return 검수 결과 {@link JSONObject}. 오류 Point 및 오류 정보에 대한 정보가 저장되어 있음.
	 * 
	 * @author DY.Oh
	 */
	public JSONObject validate(JSONObject param);

}
