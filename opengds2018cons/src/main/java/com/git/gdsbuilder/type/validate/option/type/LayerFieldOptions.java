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
package com.git.gdsbuilder.type.validate.option.type;

/**
 * 레이어 구조 검수 항목 2가지에 대한 영문 검수 항목명, 한글 검수 항목명, 오류 타입 등의 정보를 저장하는 Enum 클래스
 * <p>
 * 수치지도, 임상도, 지하시설물 검수에 모두 적용
 * 
 * @author DY.Oh
 *
 */
public enum LayerFieldOptions {

	LAYERFIELDFIXMISS("LayerFixMiss", "Feature with wrong attribute value", "필드구조오류", "LayerError", "레이어오류"),
	LAYERTYPEFIXMISS("LayerFixMiss", "Feature with wrong geometry type", "Geometry타입오류", "LayerError", "레이어오류");

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

	private LayerFieldOptions(String errCode, String errNameE, String errName, String errTypeE, String errType) {
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
