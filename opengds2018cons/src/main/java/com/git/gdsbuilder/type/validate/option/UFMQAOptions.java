/*
 *    OpenGDS/Builder
 *    http://git.co.kr
 *
 *    (C) 2014-2017, GeoSpatial Information Technology(GIT)
 *    
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 3 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package com.git.gdsbuilder.type.validate.option;

/**
 * 지하시설물 검수 항목 9가지에 대한 영문 검수 항목명, 한글 검수 항목명, 오류 타입 등의 정보를 저장하는 Enum 클래스
 * 
 * @author DY.Oh
 *
 */
public enum UFMQAOptions {

	UAVRGDPH10("UAvrgDPH10", "Wrong mean depth(Graphic) (Underground)", "평균심도오류(정위치)", "GraphicError", "그래픽오류"),
	ULEADERLINE("ULeaderline", "Leader line overlapping (Underground)", "지시선교차오류", "GraphicError", "그래픽오류"),
	UNODEMISS("UNodeMiss", "Missing node on line (Underground)", "시설물선형노드오류", "GraphicError", "그래픽오류"),
	SYMBOLINLINE("SymbolInLine", "Missing symbol on line (Underground)", "선형내심볼미존재오류", "GraphicError", "그래픽오류"),
	LINECROSS("LineCross", "Crossing pipes (Underground)", "관로상하월오류", "GraphicError", "그래픽오류"),
	SYMBOLSDISTANCE("SymbolsDistance", "Distance between symbols (Underground)", "심볼간격오류", "GraphicError", "그래픽오류"),
	USYMBOLOUT("USymbolOut", "Symbol misplacement (Underground)", "심볼단독존재오류", "GraphicError", "그래픽오류"),

	UAVRGDPH20("UAvrgDPH20", "Wrong mean depth(Attribute) (Underground)", "평균심도오류(구조화)", "AttributeError", "속성오류"),
	SYMBOLDIRECTION("SymbolDirection", "Mismatching direction of symbol (Underground)", "시설물심볼방향오류", "AttributeError",
			"속성오류");

	/**
	 * 검수옵션 생성 및 오류 레이어 생성 시 사용되는 에러코드
	 */
	String errCode;
	/**
	 * 검수 항목 명(영문)
	 */
	String errNameE;
	/**
	 * 검수 항목 명(한글)
	 */
	String errName;
	/**
	 * 오류 타입(영문)
	 */
	String errTypeE;
	/**
	 * 오류 타입(한글)
	 */
	String errType;

	private UFMQAOptions(String errCode, String errNameE, String errName, String errTypeE, String errType) {
		this.errCode = errCode;
		this.errNameE = errNameE;
		this.errName = errName;
		this.errTypeE = errTypeE;
		this.errType = errType;
	}

	public String getErrCode() {
		return errCode;
	}

	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}

	public String getErrNameE() {
		return errNameE;
	}

	public void setErrNameE(String errNameE) {
		this.errNameE = errNameE;
	}

	public String getErrName() {
		return errName;
	}

	public void setErrName(String errName) {
		this.errName = errName;
	}

	public String getErrType() {
		return errType;
	}

	public void setErrType(String errType) {
		this.errType = errType;
	}

	public String getErrTypeE() {
		return errTypeE;
	}

	public void setErrTypeE(String errTypeE) {
		this.errTypeE = errTypeE;
	}

}
