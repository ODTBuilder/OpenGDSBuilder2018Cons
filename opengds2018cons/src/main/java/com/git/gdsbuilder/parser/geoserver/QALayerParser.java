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
package com.git.gdsbuilder.parser.geoserver;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;

import com.git.gdsbuilder.type.dt.layer.DTLayer;

import lombok.Data;

/**
 * Geoserver SHP Layer를 DTLayer로 변환하는 클래스
 * 
 * @author DY.OH
 *
 */
@Data
public class QALayerParser {

	/**
	 * Geoserver URL
	 */
	private String baseUrl;
	/**
	 * Geoserver 사용자 계정
	 */
	private String user;
	/**
	 * Geoserver 계정 비밀번호
	 */
	private String pw;
	/**
	 * GET_CAPABILITIES_URL
	 */
	private String getCapabilities;
	/**
	 * Geoserver SHP Layer 명
	 */
	private String layerName;
	/**
	 * SHP 파일 소스, 파일 구조 및 속성 컬럼 구조에 대한 정보
	 */
	private DataStore dataStore;

	/**
	 * Geoserver SHP Layer를 DTLayer로 변환하기 위한 생성자
	 * 
	 * @param baseUrl   Geoserver URL
	 * @param user      Geoserver 사용자 계정
	 * @param pw        Geoserver 계정 비밀번호
	 * @param layerName Geoserver SHP Layer 명
	 */
	public QALayerParser(String baseUrl, String user, String pw, String layerName) {
		super();
		this.baseUrl = baseUrl;
		this.user = user;
		this.pw = pw;
		this.layerName = layerName;
	}

	/**
	 * Geoserver로 부터 SHP Layer DataStore 정보를 받아옴
	 * 
	 * @author DY.OH
	 */
	public void init() {
		String getCapabilities = baseUrl + "/wfs?REQUEST=GetCapabilities&version=1.0.0";
		Map connectionParameters = new HashMap();
		connectionParameters.put("WFSDataStoreFactory:GET_CAPABILITIES_URL", getCapabilities);
		try {
			this.dataStore = DataStoreFinder.getDataStore(connectionParameters);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Geoserver SHP Layer를 DTLayer로 변환
	 * 
	 * @return DTLayer SHP Layer 검수 관련 정보(SimpleFeatureCollection, layerID 등)
	 * 
	 * @author DY.OH
	 */
	public DTLayer layerParse() {
		SimpleFeatureCollection sfc = null;
		DTLayer dtLayer = null;
		try {
			SimpleFeatureSource source = this.dataStore.getFeatureSource(this.layerName);
			sfc = source.getFeatures();
			dtLayer = new DTLayer();
			String layrName = this.layerName.substring(this.layerName.indexOf(":") + 1, this.layerName.length());
			dtLayer.setLayerID(layrName);
			dtLayer.setSimpleFeatureCollection(sfc);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dtLayer;
	}
}
