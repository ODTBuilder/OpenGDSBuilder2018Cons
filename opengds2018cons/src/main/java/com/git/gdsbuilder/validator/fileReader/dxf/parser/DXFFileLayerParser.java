package com.git.gdsbuilder.validator.fileReader.dxf.parser;

import java.util.Iterator;
import java.util.List;

import org.geotools.feature.DefaultFeatureCollection;
import org.kabeja.dxf.DXFEntity;
import org.kabeja.dxf.DXFLayer;
import org.opengis.feature.simple.SimpleFeature;

import com.git.gdsbuilder.type.dt.layer.DTLayer;
import com.git.gdsbuilder.type.dt.layer.DTLayerList;

public class DXFFileLayerParser {

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
