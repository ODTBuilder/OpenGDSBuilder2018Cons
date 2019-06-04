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

package com.git.gdsbuilder.parser.file.ngi;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.geotools.feature.DefaultFeatureCollection;
import org.opengis.feature.simple.SimpleFeature;

import com.git.gdsbuilder.type.dt.layer.DTLayer;
import com.git.gdsbuilder.type.dt.layer.DTLayerList;

/**
 * ngi/nda 파일의 Layer를 {@link DTLayerList} 객체로 파싱하는 클래스
 * 
 * @author DY.Oh
 *
 */
public class NGIFileLayerParser {

	/**
	 * ngiReader
	 */
	BufferedReader ngiReader;
	/**
	 * ndaReader
	 */
	BufferedReader ndaReader;
	/**
	 * 좌표계 (ex. EPSG:4326)
	 */
	String epsg;

	public NGIFileLayerParser(String epsg, BufferedReader ngiReader) {
		this.ngiReader = ngiReader;
		this.epsg = epsg;
	}

	public NGIFileLayerParser(String epsg, BufferedReader ngiReader, BufferedReader ndaReader) {
		this.ngiReader = ngiReader;
		this.ndaReader = ndaReader;
		this.epsg = epsg;
	}

	/**
	 * 속성이 존재하는 ngi 레이어를 {@link DTLayerList} 객체로 변환.
	 * <p>
	 * ngi 파일과 동일 경로에 nda 파일이 존재하는 경우, 속성이 값이 존재하는 {@link DTLayerList}로 변환.
	 * <p>
	 * ngi 파일의 경우 1개의 레이어 내에 다수의 Geometry 타입 레이어가 존재할 수 있으나 {@link DTLayer}의 경우
	 * 가~~~ㄸ
	 * 
	 * @return {@link DTLayerList}
	 * @throws Exception {@link Exception}
	 * 
	 * @author DY.Oh
	 */
	public DTLayerList parseDTLayersWithAtt() throws Exception {

		DTLayerList layers = new DTLayerList();
		String line = ngiReader.readLine();
		while (line != null) {
			if (line.equalsIgnoreCase("<LAYER_START>")) {
				DTLayerList layerList = parseDTLayerWithAtt();
				for (DTLayer layer : layerList) {
					if (layer.getSimpleFeatureCollection().size() > 0) {
						layers.add(layer);
					}
				}
			}
			line = ngiReader.readLine();
		}
		ngiReader.close();
		ndaReader.close();
		return layers;
	}

	/**
	 * 속성이 존재하지 않는 ngi 레이어를 {@link DTLayerList} 객체로 변환.
	 * 
	 * @return {@link DTLayerList}
	 * @throws Exception {@link Exception}
	 * 
	 * @author DY.Oh
	 */
	public DTLayerList parseDTLayers() throws Exception {

		DTLayerList layers = new DTLayerList();
		String line = ngiReader.readLine();
		while (line != null) {
			if (line.equalsIgnoreCase("<LAYER_START>")) {
				DTLayerList layerList = parseDTLayer();
				for (DTLayer layer : layerList) {
					if (layer.getSimpleFeatureCollection().size() > 0) {
						layers.add(layer);
					}
				}
			}
			line = ngiReader.readLine();
		}
		ngiReader.close();
		return layers;
	}

	private DTLayerList parseDTLayerWithAtt() throws Exception {

		DTLayerList layers = new DTLayerList();
		String layerID = getLayerID();
		String layerName = getLayerName().replaceAll("\"", "");
		NGIHeader ngiHeader = getNgiHeader();
		NDAHeader ndaHeader = getNdaHeader();
		String[] layerTypes = getLayerTypes(ngiHeader);

		for (String type : layerTypes) {
			boolean isEquals = layers.isEqualsLayer(layerName, type);
			if (isEquals == false) {
				DTLayer layer = new DTLayer();
				layer.setSimpleFeatureCollection(new DefaultFeatureCollection(layerID));
				layer.setLayerID(layerName);
				layer.setLayerType(type);
				layers.add(layer);
			}
		}
		String ngiLine = ngiReader.readLine();
		String ndaLine = ndaReader.readLine();
		while (ngiLine != null) {
			// getDATA
			if (ngiLine.equalsIgnoreCase("<DATA>") && ndaLine.equalsIgnoreCase("<DATA>")) {
				ngiLine = ngiReader.readLine();
				ndaLine = ndaReader.readLine();

				while (!ngiLine.equalsIgnoreCase("<END>")) {
					String featureID = ngiLine;
					NGIFileFeatureParser featureParser = new NGIFileFeatureParser(epsg, ngiReader, ndaReader);
					SimpleFeature feature = featureParser.parserDTFeature(featureID, ndaHeader);
					layers.getDTLayer(layerName).addFeature(feature);
					ngiReader = featureParser.getNgiReader();
					ndaReader = featureParser.getNdaReader();

					if (!ndaLine.equalsIgnoreCase("<END>")) {
						ngiLine = ngiReader.readLine();
						ndaLine = ndaReader.readLine();
					} else {
						ngiLine = ngiReader.readLine();
					}
					if (ngiLine.equalsIgnoreCase("<END>") && ndaLine.equalsIgnoreCase("<END>")) {
						ngiLine = ngiReader.readLine();
						ndaLine = ndaReader.readLine();
						break;
					}
				}
			}
			if (ngiLine.equalsIgnoreCase("<LAYER_END>") && ndaLine.equalsIgnoreCase("<LAYER_END>")) {
				break;
			}
			ngiLine = ngiReader.readLine();
			ndaLine = ndaReader.readLine();
		}
		return layers;
	}

	private DTLayerList parseDTLayer() throws Exception {

		DTLayerList layers = new DTLayerList();
		String layerID = getLayerID();
		String layerName = getLayerName().replaceAll("\"", "");
		NGIHeader ngiHeader = getNgiHeader();
		String[] layerTypes = getLayerTypes(ngiHeader);
		for (String type : layerTypes) {
			boolean isEquals = layers.isEqualsLayer(layerName, type);
			if (!isEquals) {
				DTLayer layer = new DTLayer();
				layer.setSimpleFeatureCollection(new DefaultFeatureCollection(layerID));
				layer.setLayerID(layerName);
				layer.setLayerType(type);
				layers.add(layer);
			}
		}
		String ngiLine = ngiReader.readLine();
		while (ngiLine != null) {
			if (ngiLine.equalsIgnoreCase("<DATA>")) {
				ngiLine = ngiReader.readLine();
				while (!ngiLine.equalsIgnoreCase("<END>")) {
					String featureID = ngiLine;
					NGIFileFeatureParser featureParser = new NGIFileFeatureParser(epsg, ngiReader);
					SimpleFeature feature = featureParser.parserDTFeature(featureID);
					layers.getDTLayer(layerName).addFeature(feature);
					ngiReader = featureParser.getNgiReader();
				}
			}
			if (ngiLine.equalsIgnoreCase("<LAYER_END>")) {
				break;
			}
			ngiLine = ngiReader.readLine();
		}
		return layers;
	}

	private String[] getLayerTypes(NGIHeader header) {

		String geometric = header.getGeometric_metadata();
		int startIndex = geometric.indexOf("(");
		int lastIndex = geometric.indexOf(")");

		String metadata = geometric.substring(startIndex + 1, lastIndex);
		String[] types = metadata.split(",");

		return types;
	}

	private String getLayerID() throws IOException {

		String line = ngiReader.readLine();
		String layerID = "";
		while (line != null) {
			// getLayerID
			if (line.equalsIgnoreCase("$LAYER_ID")) {
				String temp = ngiReader.readLine();
				layerID = temp;
			}
			if (line.equalsIgnoreCase("$END")) {
				break;
			}
			line = ngiReader.readLine();
		}
		return layerID;
	}

	private String getLayerName() throws IOException {

		String line = ngiReader.readLine();
		String layerName = "";
		while (line != null) {
			// getLayerID
			if (line.equalsIgnoreCase("$LAYER_NAME")) {
				layerName = ngiReader.readLine();
			}
			if (line.equalsIgnoreCase("$END")) {
				break;
			}
			line = ngiReader.readLine();
		}
		return layerName;
	}

	private NDAHeader getNdaHeader() throws IOException {

		NDAHeader ndaHeader = new NDAHeader();
		String line = ndaReader.readLine();
		while (!line.equalsIgnoreCase("<END>")) {
			if (line.equalsIgnoreCase("$VERSION")) {
				ndaHeader.setVersion(ndaReader.readLine());
			}
			if (line.equalsIgnoreCase("$ASPATIAL_FIELD_DEF")) {
				ndaHeader.setAspatial_field_def(getAttrib());
			}
			if (line.equalsIgnoreCase("<END>")) {
				break;
			}
			line = ndaReader.readLine();
		}
		return ndaHeader;

	}

	private List<NDAField> getAttrib() throws IOException {

		List<NDAField> fields = new ArrayList<NDAField>();
		boolean isEnd = false;
		while (!isEnd) {
			String line = ndaReader.readLine();
			if (!line.equalsIgnoreCase("$END")) {
				String fieldStr = getAttribText(line);
				String[] fieldsStr = fieldStr.replaceAll(" ", "").split(",");
				// Attrfield
				String fieldName = getQuotesText(fieldsStr[0]);
				String type = fieldsStr[1];
				String size = fieldsStr[2];
				String decimal = fieldsStr[3];
				boolean isUnique;
				if (fieldsStr[4].equals("TRUE")) {
					isUnique = true;
				} else {
					isUnique = false;
				}
				NDAField field = new NDAField(fieldName, type, size, decimal, isUnique);
				fields.add(field);
			} else {
				isEnd = true;
			}
		}
		return fields;
	}

	private String getQuotesText(String line) {
		Pattern p = Pattern.compile("\\\"(.*?)\\\"");
		Matcher m = p.matcher(line);
		if (m.find()) {
			String attr = m.group(1);
			return attr;
		} else {
			return null;
		}
	}

	private String getAttribText(String line) {

		int startIndex = line.indexOf("(");
		int endIndex = line.lastIndexOf(")");

		String returnStr = line.substring(startIndex + 1, endIndex);
		return returnStr;
	}

	private NGIHeader getNgiHeader() throws IOException {

		NGIHeader header = new NGIHeader();
		String line = ngiReader.readLine();
		while (line != "<END>") {
			if (line.equalsIgnoreCase("$VERSION")) {
				header.setVersion(ngiReader.readLine());
			}
			if (line.equalsIgnoreCase("$GEOMETRIC_METADATA")) {
				header.setGeometric_metadata(ngiReader.readLine());
				header.setDim(ngiReader.readLine());
				header.setBound(ngiReader.readLine());
			}
			if (line.equalsIgnoreCase("$POINT_REPRESENT")) {
				List<String> point_represent = new ArrayList<String>();
				while (line != null) {
					line = ngiReader.readLine();
					if (!line.equals("$END")) {
						point_represent.add(line);
					} else {
						break;
					}
				}
				header.setPoint_represent(point_represent);
			}
			if (line.equalsIgnoreCase("$LINE_REPRESENT")) {
				List<String> lineString_represent = new ArrayList<String>();
				while (line != null) {
					line = ngiReader.readLine();
					if (!line.equals("$END")) {
						lineString_represent.add(line);
					} else {
						break;
					}
				}
				header.setLine_represent(lineString_represent);
			}
			if (line.equalsIgnoreCase("$REGION_REPRESENT")) {
				List<String> gegion_represent = new ArrayList<String>();
				while (line != null) {
					line = ngiReader.readLine();
					if (!line.equals("$END")) {
						gegion_represent.add(line);
					} else {
						break;
					}
				}
				header.setRegion_represent(gegion_represent);
			}
			if (line.equalsIgnoreCase("$TEXT_REPRESENT")) {
				List<String> text_represent = new ArrayList<String>();
				while (line != null) {
					line = ngiReader.readLine();
					if (!line.equals("$END")) {
						text_represent.add(line);
					} else {
						break;
					}
				}
				header.setText_represent(text_represent);
			}
			if (line.equalsIgnoreCase("<END>")) {
				break;
			}
			line = ngiReader.readLine();
		}
		return header;
	}
}
