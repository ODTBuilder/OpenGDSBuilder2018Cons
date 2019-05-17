package com.git.gdsbuilder.validator.fileReader.dxf.parser;

import java.util.Iterator;
import java.util.List;

import org.geotools.feature.DefaultFeatureCollection;
import org.kabeja.dxf.DXFEntity;
import org.kabeja.dxf.DXFLayer;
import org.opengis.feature.simple.SimpleFeature;

import com.git.gdsbuilder.type.dt.layer.DTLayer;
import com.git.gdsbuilder.type.dt.layer.DTLayerList;

/**
 * {@link DXFLayer}를 {@link DTLayerList}로 변환하는 클래스.
 * 
 * @author DY.Oh
 *
 */
public class DXFFileLayerParser {

	/**
	 * DXF Entity 레이어를 호환 가능한 {@link Point}/{@link LineString}/{@link Polygon} 타입의
	 * {@link DTLayer}로 변환.
	 * <p>
	 * 변환은 DXF Entity 타입 중 LINE, POLYLINE, LWPOLYLINE, INSERT, TEXT, SOLID, CIRCLE,
	 * ARC 총 8가지 타입만 지원함. 이외 타입의 DXF Entity 타입은 변환되지 않음.
	 * <p>
	 * DXF 레이어는 1개의 레이어가 여러가지 타입의 Entity를 가질 수 있으나 {@link DTLayer}는 타입이 1가지이므로 변환 시
	 * Entity 타입 만큼의 {@link DTLayer}가 생성되고 {@link DTLayerList}가 반환됨.
	 * <p>
	 * DXF Entity는 각각의 타입별로 분류되어
	 * 
	 * @param epsg     좌표계 (ex EPSG:4326)
	 * @param dxfLayer DXF Entity 레이어
	 * @return DTLayerList n개(DXF Entity 타입 갯수)의 {@link DTLayer}
	 * @throws Exception
	 * 
	 * @author DY.Oh
	 */
	public static DTLayerList parseDTLayer(String epsg, DXFLayer dxfLayer) throws Exception {

		DTLayerList dtlayers = new DTLayerList();
		Iterator typeIterator = dxfLayer.getDXFEntityTypeIterator();
		while (typeIterator.hasNext()) {
			String type = (String) typeIterator.next();
			String layerId = dxfLayer.getName();
			DTLayer dtlayer = new DTLayer();
			dtlayer.setLayerType(type);
			dtlayer.setLayerID(layerId);
			List<DXFEntity> dxfEntities = (List<DXFEntity>) dxfLayer.getDXFEntities(type);
			boolean typeValid = true;
			DefaultFeatureCollection dfc = new DefaultFeatureCollection(layerId);
			for (int i = 0; i < dxfEntities.size(); i++) {
				DXFEntity dxfEntity = dxfEntities.get(i);
				String entityId = dxfEntity.getID();
				if (entityId.equals("") || entityId == null) {
					dxfEntity.setID(layerId + "_" + i + 1);
				}
				SimpleFeature sf = null;
				if (type.equals("LINE")) {
					sf = DXFFileFeatureParser.parseDTLineFeaeture(epsg, dxfEntity);
				} else if (type.equals("POLYLINE")) {
					sf = DXFFileFeatureParser.parseDTPolylineFeature(epsg, dxfEntity);
				} else if (type.equals("LWPOLYLINE")) {
					sf = DXFFileFeatureParser.parseDTLWPolylineFeature(epsg, dxfEntity);
				} else if (type.equals("INSERT")) {
					sf = DXFFileFeatureParser.parseDTInsertFeature(epsg, dxfEntity);
				} else if (type.equals("TEXT")) {
					sf = DXFFileFeatureParser.parseDTTextFeature(epsg, dxfEntity);
				} else if (type.equals("SOLID")) {
					sf = DXFFileFeatureParser.parseDTSolidFeature(epsg, dxfEntity);
				} else if (type.equals("CIRCLE")) {
					sf = DXFFileFeatureParser.parseDTCircleFeature(epsg, dxfEntity);
				} else if (type.equals("ARC")) {
					sf = DXFFileFeatureParser.parseDTArcFeature(epsg, dxfEntity);
				} else {
					typeValid = false;
					continue;
				}
				dfc.add(sf);
			}
			dtlayer.setSimpleFeatureCollection(dfc);
			if (typeValid) {
				dtlayers.add(dtlayer);
			} else {
				continue;
			}
		}
		return dtlayers;
	}
}
