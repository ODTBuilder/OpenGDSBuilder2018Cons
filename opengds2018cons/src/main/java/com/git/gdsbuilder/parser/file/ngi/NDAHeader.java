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

import java.util.ArrayList;
import java.util.List;

/**
 * NDA 파일 내에 존재하는 Layer의 Header 정보를 저장하는 클래스.
 * <p>
 * NDA 파일 Import 할 때 파일 내의 각 레이어 마다 Header 정보를 저장한 후 다시 NDA 포맷으로 Export 할 때 해당
 * 정보를 포함하여 Export 함.
 * 
 * 
 * @author DY.Oh
 *
 */
public class NDAHeader {

	private String version;
	private List<NDAField> aspatial_field_def;

	public NDAHeader() {

	}

	public NDAHeader(String version, List<NDAField> aspatial_field_def) {
		super();
		this.version = version;
		this.aspatial_field_def = aspatial_field_def;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public List<NDAField> getAspatial_field_def() {
		return aspatial_field_def;
	}

	public void setAspatial_field_def(List<NDAField> aspatial_field_def) {
		this.aspatial_field_def = aspatial_field_def;
	}

	public void addField(NDAField field) {
		aspatial_field_def.add(field);
	}

	/**
	 * NDA 파일 내의 특정 레이어의 모든 속성 컬럼명 반환.
	 * 
	 * @return List<String>
	 * 
	 * @author DY.Oh
	 */
	public List<String> getFieldNames() {
		List<String> fieldNames = new ArrayList<String>();
		for (NDAField field : this.aspatial_field_def) {
			fieldNames.add(field.getFieldName());
		}
		return fieldNames;
	}
}
