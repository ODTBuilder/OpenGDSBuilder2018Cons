package com.git.gdsbuilder.geoserver.converter.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.git.gdsbuilder.geoserver.converter.GeoserverDataConverter;
import com.git.gdsbuilder.geoserver.converter.type.DigitalMapExport;
import com.git.gdsbuilder.geoserver.converter.type.ForestExport;
import com.git.gdsbuilder.geoserver.converter.type.UndergroundExport;

/**
 * { "layers":{ "geoserver30":{ "admin":[
 * "geo_shp_37712012_A0010000_MULTIPOLYGON",
 * "geo_shp_37712012_A0020000_MULTILINESTRING",
 * "geo_shp_37712012_A0070000_MULTIPOLYGON",
 * "geo_shp_37712012_B0010000_MULTIPOLYGON",
 * "geo_shp_37712012_B0020000_MULTILINESTRING",
 * "geo_shp_37712012_F0010000_MULTILINESTRING",
 * "geo_shp_37712012_H0010000_MULTILINESTRING" ], "shp":[ "a0010000",
 * "a0020000", "a0070000", "b0010000", "b0020000", "f0010000", "h0010000" ] } }
 * "cidx" : "0" }
 * 
 * @Description
 * @author SG.Lee
 * @Date 2018. 9. 28. 오후 5:25:38
 */
public class GeoserverDataConverterImpl implements GeoserverDataConverter {
	private final String serverURL;
	private final Map<String, List<String>> layerMaps;
	private final String outputFolderPath;
	private final String srs;

	public GeoserverDataConverterImpl(String serverURL, Map<String, List<String>> layerMaps, String outputFolderPath,
			String srs) {
		if (serverURL.isEmpty() || layerMaps == null || outputFolderPath.isEmpty() || srs.isEmpty()) {
			throw new IllegalArgumentException("필수파라미터 입력안됨");
		}
		this.serverURL = serverURL;
		this.layerMaps = layerMaps;
		this.outputFolderPath = outputFolderPath;
		this.srs = srs;
	}

	public String getSrs() {
		return srs;
	}

	public String getServerURL() {
		return serverURL;
	}

	public Map<String, List<String>> getLayerMaps() {
		return layerMaps;
	}

	public String getOutputFolderPath() {
		return outputFolderPath;
	}

	/**
	 * 
	 * @since 2018. 10. 30.
	 * @author SG.Lee
	 * @return
	 * @see com.git.gdsbuilder.geoserver.converter.GeoserverDataConverter#digitalExport()
	 */
	public int digitalExport() {
		return new DigitalMapExport(serverURL, layerMaps, outputFolderPath, srs).export();
	}

	/**
	 * 
	 * @since 2018. 10. 30.
	 * @author SG.Lee
	 * @return
	 * @see com.git.gdsbuilder.geoserver.converter.GeoserverDataConverter#undergroundExport()
	 */
	public int undergroundExport() {
		int flag = 500;
		if (layerMaps != null) {
			int mapSize = layerMaps.size();

			if (mapSize > 1) {
				System.err.println("지하시설물은 workspace 하나만 가능합니다");
				flag = 700;
			} else {
				Iterator<String> keys = layerMaps.keySet().iterator();
				while (keys.hasNext()) {
					String workspace = keys.next();
					List<String> layerNames = layerMaps.get(workspace);
					if (layerNames != null) {
						flag = new UndergroundExport(serverURL, workspace, layerNames, outputFolderPath, srs).export();
					} else {
						flag = 612;
						System.err.println("레이어 리스트 NULL");
					}
				}
			}
		}
		return flag;
	}

	/**
	 * 
	 * @since 2018. 10. 30.
	 * @author SG.Lee
	 * @param nearLine
	 * @return
	 * @see com.git.gdsbuilder.geoserver.converter.GeoserverDataConverter#forestExport(java.lang.String)
	 */
	public int forestExport(String nearLine) {
		int flag = 500;
		if (layerMaps != null) {
			int mapSize = layerMaps.size();

			if (mapSize > 1) {
				System.err.println("임상도는 workspace 하나만 가능합니다");
				flag = 700;
			} else {
				Iterator<String> keys = layerMaps.keySet().iterator();
				while (keys.hasNext()) {
					String workspace = keys.next();
					List<String> layerNames = layerMaps.get(workspace);
					if (layerNames != null) {
						flag = new ForestExport(serverURL, workspace, layerNames, outputFolderPath, srs, nearLine)
								.export();
					} else {
						flag = 612;
						System.err.println("레이어 리스트 NULL");
					}
				}
			}
		}
		return flag;
	}
}
