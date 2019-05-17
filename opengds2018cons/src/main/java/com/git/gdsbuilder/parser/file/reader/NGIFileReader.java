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

package com.git.gdsbuilder.parser.file.reader;

import com.git.gdsbuilder.parser.file.ngi.NGIDataStore;
import com.git.gdsbuilder.parser.file.ngi.NGIFileLayerParser;
import com.git.gdsbuilder.parser.file.ngi.NGIFileParser;
import com.git.gdsbuilder.type.dt.collection.DTLayerCollection;
import com.git.gdsbuilder.type.dt.layer.DTLayer;
import com.git.gdsbuilder.type.dt.layer.DTLayerList;

/**
 * ngi/nda 파일을 {@link DTLayerCollection} 객체로 변환하는 클래스.
 * 
 * @author DY.Oh
 */
public class NGIFileReader {

	/**
	 * @param epsg     좌표계 (ex EPSG:4326)
	 * @param filePath 파일 경로
	 * @param fileName 파일 이름
	 * @param neatLine 검수 영역 레이어 이름
	 * @return ngi/nda 파일을 변환한 {@link DTLayerCollection}, ngi/nda 파일 내에 레이어가 존재하지
	 *         않거나 올바르지 않은 형태이면 {@code null} 반환
	 * @throws Exception
	 * 
	 * @author DY.Oh
	 */
	public DTLayerCollection read(String epsg, String filePath, String fileName, String neatLine) throws Exception {

		NGIFileParser parser = new NGIFileParser();
		NGIDataStore dataStore = parser.parse(filePath, epsg, "EUC-KR");
		DTLayerCollection collection = new DTLayerCollection();

		DTLayerList list;
		if (dataStore.isNDA()) {
			NGIFileLayerParser dtlayers = new NGIFileLayerParser(epsg, dataStore.getNgiReader(),
					dataStore.getNdaReader());
			list = dtlayers.parseDTLayersWithAtt();
		} else {
			NGIFileLayerParser dtlayers = new NGIFileLayerParser(epsg, dataStore.getNgiReader());
			list = dtlayers.parseDTLayers();
		}
		for (int i = 0; i < list.size(); i++) {
			DTLayer layer = list.get(i);
			String layerName = layer.getLayerID();
			int sfcSize = layer.getSimpleFeatureCollection().size();
			if (sfcSize != 0) {
				if (neatLine != null) {
					if (layerName.toUpperCase().equals(neatLine.toUpperCase())) {
						collection.setNeatLine(layer);
					}
				}
			}
		}
		collection.setLayers(list);
		collection.setCollectionName(fileName);
		return collection;
	}
}
