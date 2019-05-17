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

package com.git.gdsbuilder.validator.fileReader.ngi.header;

import java.util.ArrayList;
import java.util.List;

/**
 * NGI 파일 내에 존재하는 Layer의 Header 정보를 저장하는 클래스.
 * <p>
 * NGI 파일 Import 할 때 파일 내의 각 레이어 마다 Header 정보를 저장한 후 다시 NGI 포맷으로 Export 할 때 해당
 * 정보를 포함하여 Export 함.
 * 
 * @author DY.Oh
 *
 */
public class NGIHeader {

	/**
	 * NGI 레이어 버전
	 */
	private String version;
	/**
	 * NGI 레이어 메타데이터
	 */
	private String geometric_metadata;
	/**
	 * NGI 레이어 Geometry 차원 (DIM(2) or DIM(3))
	 */
	private String dim;
	/**
	 * NGI 레이어 Envelop
	 */
	private String bound;
	/**
	 * NGI Point 레이어 스타일
	 */
	private List<String> point_represent;
	/**
	 * NGI LineString 레이어 스타일
	 */
	private List<String> line_represent;
	/**
	 * NGI Polygon 레이어 스타일
	 */
	private List<String> region_represent;
	/**
	 * NGI Text 레이어 스타일
	 */
	private List<String> text_represent;

	public NGIHeader() {
		this.version = "";
		this.geometric_metadata = "";
		this.dim = "";
		this.bound = "";
		this.point_represent = new ArrayList<String>();
		this.line_represent = new ArrayList<String>();
		this.region_represent = new ArrayList<String>();
		this.text_represent = new ArrayList<String>();
	}

	public NGIHeader(String version, String geometric_metadata, String dim, String bound, List<String> point_represent,
			List<String> line_represent, List<String> region_represent, List<String> text_represent) {
		super();
		this.version = version;
		this.geometric_metadata = geometric_metadata;
		this.dim = dim;
		this.bound = bound;
		this.point_represent = point_represent;
		this.line_represent = line_represent;
		this.region_represent = region_represent;
		this.text_represent = text_represent;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getGeometric_metadata() {
		return geometric_metadata;
	}

	public void setGeometric_metadata(String geometric_metadata) {
		this.geometric_metadata = geometric_metadata;
	}

	public String getDim() {
		return dim;
	}

	public void setDim(String dim) {
		this.dim = dim;
	}

	public String getBound() {
		return bound;
	}

	public void setBound(String bound) {
		this.bound = bound;
	}

	public List<String> getPoint_represent() {
		return point_represent;
	}

	public void setPoint_represent(List<String> point_represent) {
		this.point_represent = point_represent;
	}

	public List<String> getLine_represent() {
		return line_represent;
	}

	public void setLine_represent(List<String> line_represent) {
		this.line_represent = line_represent;
	}

	public List<String> getRegion_represent() {
		return region_represent;
	}

	public void setRegion_represent(List<String> region_represent) {
		this.region_represent = region_represent;
	}

	public List<String> getText_represent() {
		return text_represent;
	}

	public void setText_represent(List<String> text_represent) {
		this.text_represent = text_represent;
	}

	public void addRegion_represent(String region_represent) {
		this.region_represent.add(region_represent);
	}

}
