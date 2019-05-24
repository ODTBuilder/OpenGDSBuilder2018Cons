package com.gitrnd.qaconsumer.qa.web.service;

import org.json.simple.JSONObject;

/**
 * Geoserver 기반 검수 요청 클래스.
 * 
 * @author DY.Oh
 *
 */
public interface QAService {

	/**
	 * Geoserver 기반 검수 요청.
	 * 
	 * @param param Producer로부터 전달받은 Message를 {@link JSONObject} 형태로 변환함.
	 * @return {@code true} : 검수 성공
	 *         <p>
	 *         {@code false} : 검수 실패
	 * @throws Throwable Throwable
	 * 
	 * @author DY.Oh
	 */
	public boolean validate(JSONObject param) throws Throwable;

}
