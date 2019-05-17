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

package com.git.gdsbuilder.parser.file.ngi;

/**
 * NDA 파일 내에 존재하는 Layer의 Header 정보 중 속성 컬럼에 대한 정보를 저장하는 클래스.
 *
 * @author DY.Oh
 *
 */
public class NDAField {

	/**
	 * 속성 컬럼명
	 */
	String originFieldName;
	/**
	 * 속성 컬럼명
	 */
	String fieldName;
	/**
	 * 속성 타입
	 */
	String type;
	/**
	 * 속성 문자열 길이
	 */
	String size;
	/**
	 * 
	 */
	String decimal;
	/**
	 * Unique 허용
	 */
	boolean isUnique;
	/**
	 * Null 허용
	 */
	boolean isNotNull;

	public NDAField() {
		super();
	}

	public NDAField(String originFieldName, String fieldName, String type, String size, String decimal,
			boolean isUnique, boolean isNotNull) {
		super();
		this.originFieldName = originFieldName;
		this.fieldName = fieldName;
		this.type = type;
		this.size = size;
		this.decimal = decimal;
		this.isUnique = isUnique;
		this.isNotNull = isNotNull;
	}

	public NDAField(String fieldName, String type, String size, String decimal, boolean isUnique, boolean isNotNull) {
		super();
		this.fieldName = fieldName;
		this.type = type;
		this.size = size;
		this.decimal = decimal;
		this.isUnique = isUnique;
		this.isNotNull = isNotNull;
	}

	public NDAField(String fieldName, String type, String size, String decimal, boolean isUnique) {
		super();
		this.fieldName = fieldName;
		this.type = type;
		this.size = size;
		this.decimal = decimal;
		this.isUnique = isUnique;
	}

	public String getOriginFieldName() {
		return originFieldName;
	}

	public void setOriginFieldName(String originFieldName) {
		this.originFieldName = originFieldName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getDecimal() {
		return decimal;
	}

	public void setDecimal(String decimal) {
		this.decimal = decimal;
	}

	public boolean isUnique() {
		return isUnique;
	}

	public void setUnique(boolean isUnique) {
		this.isUnique = isUnique;
	}

}
