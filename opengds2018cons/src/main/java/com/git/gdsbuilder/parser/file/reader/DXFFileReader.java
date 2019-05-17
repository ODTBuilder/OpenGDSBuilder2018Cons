package com.git.gdsbuilder.parser.file.reader;

import java.util.Iterator;

import org.kabeja.dxf.DXFDocument;
import org.kabeja.dxf.DXFLayer;
import org.kabeja.parser.DXFParser;
import org.kabeja.parser.Parser;
import org.kabeja.parser.ParserBuilder;

import com.git.gdsbuilder.parser.file.dxf.DXFFileLayerParser;
import com.git.gdsbuilder.type.dt.collection.DTLayerCollection;
import com.git.gdsbuilder.type.dt.layer.DTLayer;
import com.git.gdsbuilder.type.dt.layer.DTLayerList;

/**
 * 특정 경로에 존재하는 DXF 파일을 읽어 검수를 하기 위해 {@link DTLayerCollection} 객체로 변환하는 클래스.
 * 
 * @author DY.Oh
 *
 */
public class DXFFileReader {

	/**
	 * 특정 경로에 존재하는 DXF 파일을 읽어 {@link DTLayerCollection} 객체로 변환.
	 * <p>
	 * DXF는 1개의 파일 내에 다수의 레이어로 구성되어 있어 {@link DTLayerCollection}로 변환됨.
	 * <p>
	 * DXF는 AutoDAD 도면 파일로 {@link Geometry} 형태가 아닌 Line, Polyline, Blocks/Insert,
	 * Text 등의 Entity로 구성되어 검수 시 {@link Geometry} 공간 연산(Equals, Intersects, Contains
	 * 등)을 사용할 수 없음.
	 * <p>
	 * 따라서 각 레이어의 Entity를 호환 가능한 {@link Point}/{@link LineString}/{@link Polygon}
	 * 타입으로 변환 후 {@link DTLayer}로 생성함.
	 * <p>
	 * 검수 영역 레이어는 {@link DTLayerCollection}의 {@link DTLayerList}에 포함시키지 않고 neatLine
	 * 변수에 저장함.
	 * 
	 * @param epsg         좌표계 (ex EPSG:4326)
	 * @param filePath     파일 경로
	 * @param fileName     파일 이름
	 * @param neatLineName 검수 영역 레이어 이름
	 * @return DXF 파일을 변환한 {@link DTLayerCollection}, DXF 파일 내에 레이어가 존재하지 않거나 올바르지
	 *         않은 형태이면 {@code null} 반환
	 * @throws Exception
	 * 
	 * @author DY.Oh
	 */
	public DTLayerCollection read(String epsg, String filePath, String fileName, String neatLineName) throws Exception {

		DTLayerCollection dtCollection = new DTLayerCollection();
		dtCollection.setCollectionName(fileName);
		Parser parser = ParserBuilder.createDefaultParser();
		parser.parse(filePath, DXFParser.DEFAULT_ENCODING);
		DXFDocument doc = parser.getDocument();
		DTLayerList layerList = new DTLayerList();
		Iterator layerIterator = doc.getDXFLayerIterator();
		while (layerIterator.hasNext()) {
			DXFLayer dxfLayer = (DXFLayer) layerIterator.next();
			// String layerId = dxfLayer.getName();
			// if (layerId.matches("[0-9|a-z|A-Z|ㄱ-ㅎ|ㅏ-ㅣ|가-힝]*")) {
			DTLayerList dtLayers = DXFFileLayerParser.parseDTLayer(epsg, dxfLayer);
			for (int i = 0; i < dtLayers.size(); i++) {
				DTLayer layer = dtLayers.get(i);
				String layerName = layer.getLayerID();
				if (neatLineName != null) {
					if (layerName.toUpperCase().equals(neatLineName.toUpperCase())) {
						dtCollection.setNeatLine(layer);
					} else {
						layerList.add(layer);
					}
				} else {
					layerList.add(layer);
				}
			}
		}
		if (layerList.size() > 0) {
			dtCollection.setLayers(layerList);
			return dtCollection;
		} else {
			return null;
		}
	}
}
