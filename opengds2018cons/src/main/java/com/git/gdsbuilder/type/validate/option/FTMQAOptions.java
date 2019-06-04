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
 * 임상도 검수 항목 6가지에 대한 영문 검수 항목명, 한글 검수 항목명, 오류 타입 등의 정보를 저장하는 Enum 클래스
 * 
 * @author DY.Oh
 *
 */
public enum FTMQAOptions {

	FCODELOGICALATTRIBUTE("FCodeLogicalAttribute", "Wrong F Code (Forest)", "FCode불일치", "AttributeError", "속성오류"),
	FLABELLOGICALATTRIBUTE("FLabelLogicalAttribute", "Wrong F Label (Forest)", "AttributeError", "Label불일치", "속성오류"),
	DISSOLVE("Dissolve", "Discord of adjacent attribute", "인접속성병합오류", "AttributeError", "속성오류"),

	MULTIPART("MultiPart", "Selection of wrong multiple parts", "다중객체존재오류", "GraphicError", "그래픽오류"),
	SMALLAREA("SmallArea", "Areas under tolerance limit", "미세폴리곤존재오류", "GraphicError", "그래픽오류"),
	FENTITYINHOLE("FEntityInHole", "Holes in polygons", "홀(Hole)폴리곤존재오류", "GraphicError", "그래픽오류"),
	SELFENTITY("SelfEntity", "Overlapping features", "단독존재오류", "GraphicError", "그래픽오류");

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

	private FTMQAOptions(String errCode, String errNameE, String errName, String errTypeE, String errType) {
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

	public String getErrTypeE() {
		return errTypeE;
	}

	public void setErrTypeE(String errTypeE) {
		this.errTypeE = errTypeE;
	}

	public String getErrType() {
		return errType;
	}

	public void setErrType(String errType) {
		this.errType = errType;
	}
}
