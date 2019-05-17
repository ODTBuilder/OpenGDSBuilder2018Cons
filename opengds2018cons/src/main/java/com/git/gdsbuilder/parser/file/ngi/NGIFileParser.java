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

package com.git.gdsbuilder.parser.file.ngi;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.geotools.data.DataUtilities;
import org.geotools.data.ngi.NGIDataStoreFactory;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * ngi/nda 파일을 {@link NGIDataStore}로 변환는 클래스
 * 
 * @author DY.Oh
 */
public class NGIFileParser extends NGIDataStoreFactory {

	/**
	 * ngi/nda 파일을 {@link NGIDataStore}로 변환하여 반환
	 * 
	 * @param ngiFilePath 파일 경로
	 * @param srs         좌표계 (ex. EPSG:4326)
	 * @param charset     인고딩 타입 (ex. EUC-KR)
	 * @return NGIDataStore
	 * @throws IOException
	 * 
	 * @author DY.Oh
	 */
	public NGIDataStore parse(String ngiFilePath, String srs, String charset) throws IOException {

		Map<String, Serializable> params = new HashMap<String, Serializable>();

		params.put(NGIDataStoreFactory.PARAM_FILE.key, DataUtilities.fileToURL(new File(ngiFilePath)));
		params.put(NGIDataStoreFactory.PARAM_SRS.key, srs);
		params.put(NGIDataStoreFactory.PARAM_CHARSET.key, charset);

		return createDTDataStore(params);

	}

	private NGIDataStore createDTDataStore(Map<String, Serializable> params) throws IOException {

		URL url = (URL) PARAM_FILE.lookUp(params);
		String code = (String) PARAM_SRS.lookUp(params);
		String charset = (String) PARAM_CHARSET.lookUp(params);

		if (charset == null || charset.isEmpty()) {
			charset = (String) PARAM_CHARSET.sample;
		}

		CoordinateReferenceSystem crs = null;
		if (code == null || code.isEmpty()) {
			crs = null; // default??
		} else {
			try {
				crs = CRS.decode(code);
			} catch (NoSuchAuthorityCodeException e) {
				LOGGER.log(Level.WARNING, e.getMessage(), e);
			} catch (FactoryException e) {
				LOGGER.log(Level.WARNING, e.getMessage(), e);
			}
		}
		return new NGIDataStore(DataUtilities.urlToFile(url), Charset.forName(charset), crs);
	}

}
