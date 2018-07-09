package com.git.gdsbuilder.validator.fileReader.dxf.reader;

import java.util.Iterator;

import org.kabeja.dxf.DXFDocument;
import org.kabeja.dxf.DXFLayer;
import org.kabeja.parser.DXFParser;
import org.kabeja.parser.Parser;
import org.kabeja.parser.ParserBuilder;

import com.git.gdsbuilder.type.dt.collection.DTLayerCollection;
import com.git.gdsbuilder.type.dt.layer.DTLayer;
import com.git.gdsbuilder.type.dt.layer.DTLayerList;
import com.git.gdsbuilder.validator.fileReader.dxf.parser.DXFFileLayerParser;

public class DXFFileReader {

	public DTLayerCollection read(String epsg, String filePath, String fileName, String neatLineName) throws Exception {

		DTLayerCollection dtCollection = new DTLayerCollection();
		dtCollection.setCollectionName(fileName);
		Parser parser = ParserBuilder.createDefaultParser();
		// try {
		parser.parse(filePath, DXFParser.DEFAULT_ENCODING);
		DXFDocument doc = parser.getDocument();
		// readDXF
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
					if (layerName.equals(neatLineName)) {
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

	public DTLayerCollection read(String epsg, String filePath, String fileName) throws Exception {

		DTLayerCollection dtCollection = new DTLayerCollection();
		dtCollection.setCollectionName(fileName);
		Parser parser = ParserBuilder.createDefaultParser();
		// try {
		parser.parse(filePath, DXFParser.DEFAULT_ENCODING);
		DXFDocument doc = parser.getDocument();
		// readDXF
		Iterator layerIterator = doc.getDXFLayerIterator();

		DTLayerList layerList = new DTLayerList();

		DXFLayer dxfLayer = (DXFLayer) layerIterator.next();
		String layerId = dxfLayer.getName();
		if (layerId.matches("[0-9|a-z|A-Z|ㄱ-ㅎ|ㅏ-ㅣ|가-힝]*")) {
			DTLayerList dtLayers = DXFFileLayerParser.parseDTLayer(epsg, dxfLayer);
			for (int i = 0; i < dtLayers.size(); i++) {
				DTLayer layer = dtLayers.get(i);
				layerList.add(layer);
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
