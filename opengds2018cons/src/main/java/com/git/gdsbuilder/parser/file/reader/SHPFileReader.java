package com.git.gdsbuilder.parser.file.reader;

import com.git.gdsbuilder.parser.file.shp.SHPFileLayerParser;
import com.git.gdsbuilder.type.dt.collection.DTLayerCollection;
import com.git.gdsbuilder.type.dt.layer.DTLayer;
import com.git.gdsbuilder.type.dt.layer.DTLayerList;

public class SHPFileReader {

	/**
	 * 특정 경로에 존재하는 다수의 SHP 파일을 읽어 검수를 하기 위해 {@link DTLayerCollection} 객체로 변환하는 클래스.
	 * 
	 * @param epsg         좌표계 (ex EPSG:4326)
	 * @param filePath     파일 경로
	 * @param fileName     파일 이름
	 * @param neatLineName 검수 영역 레이어 이름
	 * @return {@link DTLayerCollection}
	 * @throws Exception {@link Exception}
	 * 
	 * @author DY.Oh
	 */
	@SuppressWarnings("unchecked")
	public DTLayerCollection read(String epsg, String filePath, String fileName, String neatLineName) throws Exception {

		DTLayerCollection dtCollectoin = new DTLayerCollection();
		dtCollectoin.setCollectionName(fileName);
		SHPFileLayerParser parser = new SHPFileLayerParser();
		DTLayerList dtLayerList = new DTLayerList();
		DTLayer layer = parser.parseDTLayer(epsg, filePath, fileName);
		String layerName = layer.getLayerID();
		if (layerName != null) {
			if (layerName.equals(neatLineName)) {
				dtCollectoin.setNeatLine(layer);
			}
		} else {
			dtLayerList.add(layer);
		}
		dtCollectoin.setLayers(dtLayerList);
		return dtCollectoin;
	}
}
