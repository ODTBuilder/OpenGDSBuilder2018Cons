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

/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2002-2012, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package com.git.gdsbuilder.parser.file.reader;

import com.git.gdsbuilder.type.dt.collection.DTLayerCollection;

/**
 * 검수 대상 파일을 읽어 {@link DTLayerCollection}로 파싱하는 클래스.
 * 
 * @author DY.Oh
 *
 */
public class FileDTLayerCollectionReader {

	/**
	 * shp 파일을 읽어 {@link DTLayerCollection} 객체로 파싱.
	 * 
	 * @param epsg         좌표계 (ex. EPSG:4326)
	 * @param filePath     shp 파일 경로
	 * @param fileName     shp 파일명
	 * @param neatLineName 검수 영역 shp 파일 명
	 * @return {@link DTLayerCollection}
	 * @throws Throwable {@link Throwable}
	 * 
	 * @author DY.Oh
	 */
	public DTLayerCollection shpLayerParse(String epsg, String filePath, String fileName, String neatLineName)
			throws Throwable {

		SHPFileReader fileReader = new SHPFileReader();
		DTLayerCollection dtCollection = fileReader.read(epsg, filePath, fileName, neatLineName);
		return dtCollection;
	}

	/**
	 * dxf 파일을 읽어 {@link DTLayerCollection} 객체로 파싱.
	 * 
	 * @param epsg         좌표계 (ex. EPSG:4326)
	 * @param filePath     dxf 파일 경로
	 * @param fileName     dxf 파일명
	 * @param neatLineName 검수 영역 shp 파일 명
	 * @return {@link DTLayerCollection}
	 * @throws Exception {@link Exception}
	 * 
	 * @author DY.Oh
	 */
	public DTLayerCollection dxfLayerParse(String epsg, String filePath, String fileName, String neatLineName)
			throws Exception {

		DXFFileReader fileReader = new DXFFileReader();
		DTLayerCollection dtCollection = fileReader.read(epsg, filePath, fileName, neatLineName);
		return dtCollection;
	}

	/**
	 * ngi/nda 파일을 읽어 DTLayerCollection 객체로 파싱.
	 * 
	 * @param epsg     좌표계 (ex. EPSG:4326)
	 * @param filePath shp 파일 경로
	 * @param fileName shp 파일명
	 * @param neatLine 검수 영역 shp 파일 명
	 * @return {@link DTLayerCollection}
	 * @throws Exception {@link Exception}
	 * 
	 * @author DY.Oh
	 */
	public DTLayerCollection ngiLayerParse(String epsg, String filePath, String fileName, String neatLine)
			throws Exception {

		NGIFileReader fileReader = new NGIFileReader();
		DTLayerCollection dtCollection = fileReader.read(epsg, filePath, fileName, neatLine);
		return dtCollection;
	}

}
