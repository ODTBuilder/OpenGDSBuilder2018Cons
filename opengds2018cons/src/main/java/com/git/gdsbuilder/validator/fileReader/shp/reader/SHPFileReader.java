package com.git.gdsbuilder.validator.fileReader.shp.reader;

import java.util.List;
import java.util.Map;

import com.git.gdsbuilder.type.dt.collection.DTLayerCollection;
import com.git.gdsbuilder.type.dt.layer.DTLayer;
import com.git.gdsbuilder.type.dt.layer.DTLayerList;
import com.git.gdsbuilder.validator.fileReader.shp.parser.SHPFileLayerParser;

public class SHPFileReader {

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

	@SuppressWarnings("unchecked")
	public DTLayerCollection read(String epsg, String upZipFilePath, String entryName, Map<String, Object> fileNameMap)
			throws Exception {

		List<String> shpFileNames = (List<String>) fileNameMap.get("shp");
		List<String> shxFileNames = (List<String>) fileNameMap.get("shx");
		List<String> dbfFileNames = (List<String>) fileNameMap.get("dbf");

		DTLayerCollection dtCollectoin = new DTLayerCollection();
		dtCollectoin.setCollectionName(entryName);
		SHPFileLayerParser parser = new SHPFileLayerParser();
		DTLayerList dtLayerList = new DTLayerList();
		for (int i = 0; i < shpFileNames.size(); i++) {
			String shpFile = shpFileNames.get(i);
			int comma = shpFile.lastIndexOf(".");
			String shpName = shpFile.substring(0, comma);
			boolean validShx = isValidFile(shpName, shxFileNames);
			boolean validDbf = isValidFile(shpName, dbfFileNames);
			if (validShx && validDbf) {
				DTLayer layer = parser.parseDTLayer(epsg, upZipFilePath, shpName);
				dtLayerList.add(layer);
			}
		}
		dtCollectoin.setLayers(dtLayerList);
		return dtCollectoin;
	}

	private boolean isValidFile(String fileName, List<String> otherFileNames) {

		boolean isValid = false;

		for (int i = 0; i < otherFileNames.size(); i++) {
			String otherFileName = otherFileNames.get(i);
			int otherComma = otherFileName.lastIndexOf(".");
			String otherName = otherFileName.substring(0, otherComma);
			if (fileName.toUpperCase().equals(otherName.toUpperCase())) {
				isValid = true;
			}
		}
		return isValid;
	}

	private String getFileName(String filePath) {

		String[] splitArr = filePath.split("\\\\");
		String fileName = splitArr[splitArr.length - 1];
		int pos = fileName.lastIndexOf(".");
		return fileName.substring(0, pos);
	}
}
