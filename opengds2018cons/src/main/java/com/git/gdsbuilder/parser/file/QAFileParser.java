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
package com.git.gdsbuilder.parser.file;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.DefaultFeatureCollection;
import org.opengis.feature.simple.SimpleFeature;

import com.git.gdsbuilder.parser.file.meta.FileMeta;
import com.git.gdsbuilder.parser.file.meta.FileMetaList;
import com.git.gdsbuilder.parser.file.shp.SHPFileLayerParser;
import com.git.gdsbuilder.type.dt.collection.DTLayerCollection;
import com.git.gdsbuilder.type.dt.collection.DTLayerCollectionList;
import com.git.gdsbuilder.type.dt.collection.MapSystemRule;
import com.git.gdsbuilder.type.dt.layer.DTLayer;
import com.git.gdsbuilder.type.dt.layer.DTLayerList;

import lombok.Data;

/**
 * 
 * zip 파일 또는 Geoserver SHP Layer 파일을 {@link DTLayerCollectionList} 타입으로 변환하는 클래스
 * 
 * @author DY.OH
 *
 */
@Data
public class QAFileParser {

	String brTag = "<br>";
	/**
	 * 파일 변환 시 발생하는 오류에 대한 설명
	 */
	String status = "";
	/**
	 * 검수 종류 ID
	 */
	Integer cIdx;
	/**
	 * 검수 종류 별 지원 파일 포맷
	 */
	String support;
	/**
	 * zip 파일 압축 해제 경로 및 압축 해제된 파일 정보
	 */
	UnZipFile unZipFile;
	/**
	 * Geoserver SHP Layer 파일
	 */
	File geoLayersPath;
	/**
	 * 검수영역 레이어 파일명
	 */
	String neatLine;
	/**
	 * 파일 좌표계 ex) EPSG:4326
	 */
	String epsg;
	/**
	 * 변환된 DTLayerCollectionList, 검수 대상 파일 및 검수에 필요한 정보를 List 형태로 저장
	 */
	DTLayerCollectionList collectionList;

	/**
	 * 변환 성공 여부
	 */
	boolean isTrue;

	/**
	 * zip 파일을 DTLayerCollectionList 타입으로 변환하기 위한 클래스 생성자
	 * 
	 * @param epsg      파일 좌표계 ex) EPSG:4326
	 * @param cIdx      검수 종류 ID 수치지도 1.0 : 1, 수치지도 2.0 : 2, 지하시설물 1.0 : 3, 지하시설물
	 *                  2.0 : 4, 임상도 : 5
	 * @param support   검수 종류 별 지원 파일 포맷
	 * @param unZipFile zip 파일 압축 해제 경로 및 압축 해제된 파일 정보
	 * @param neatLine  검수영역 레이어 파일명
	 * @author DY.OH
	 */
	public QAFileParser(String epsg, int cIdx, String support, UnZipFile unZipFile, String neatLine) {

		this.cIdx = cIdx;
		this.support = support;
		this.unZipFile = unZipFile;
		this.neatLine = neatLine;
		this.epsg = epsg;

		if (cIdx == 1 && support.equals("dxf")) {
			parseNumericalQA10File();
			isTrue = true;
		} else if (cIdx == 2 && support.equals("shp")) {
			parseNumericalQA20ShpFile();
			isTrue = true;
		} else if (cIdx == 2 && support.equals("ngi")) {
			parseNumericalQA20NgiFile();
			isTrue = true;
		} else if (cIdx == 3 && support.equals("dxf")) {
			parseUndergroundQA10File();
			isTrue = true;
		} else if (cIdx == 4 && support.equals("shp")) {
			parseUndergroundQA20File();
			isTrue = true;
		} else if (cIdx == 5 && support.equals("shp")) {
			parseForestQA20File();
			isTrue = true;
		} else {
			status += "지원하지않는 파일포맷입니다." + brTag;
			isTrue = false;
		}
	}

	/**
	 * Geoserver SHP Layer 파일을 DTLayerCollectionList 타입으로 변환하기 위한 클래스 생성자
	 * 
	 * @param epsg          파일 좌표계 ex) EPSG:4326
	 * @param cIdx          검수 종류 ID (SHP 포맷의 검수 종류만 지원 가능) 수치지도 2.0 : 2, 지하시설물 2.0
	 *                      : 4, 임상도 : 5
	 * @param geoLayersPath Geoserver SHP Layer 파일
	 * @param fname         Geoserver 작업공간 이름
	 * @param neatLine      검수영역 레이어 파일명
	 * @author DY.OH
	 */
	public QAFileParser(String epsg, int cIdx, File geoLayersPath, String fname, String neatLine) {

		this.cIdx = cIdx;
		this.geoLayersPath = geoLayersPath;
		this.neatLine = neatLine;
		this.epsg = epsg;

		if (cIdx == 1 || cIdx == 2) {
			parseDigitalGeoserverLayers();
			isTrue = true;
		} else if (cIdx == 3 || cIdx == 4) {
			parseUnderGeoserverLayers(fname);
			isTrue = true;
		} else if (cIdx == 5) {
			parseForestGeoserverLayers();
			isTrue = true;
		} else {
			status += "지원하지않는 검수 타입입니다." + brTag;
			isTrue = false;
		}

	}

	private void parseForestGeoserverLayers() {

		// geolayersPath
		if (geoLayersPath.exists() == false) {
			status += "경로가 존재하지 않습니다" + brTag;
		} else {
			File[] collectionDir = geoLayersPath.listFiles();
			if (collectionDir.length == 0) {
				this.collectionList = null;
				status += "요청 형식(1개 GeoServer WorkSpace)이 다릅니다." + brTag;
			} else {
				DTLayerCollectionList collectionList = new DTLayerCollectionList();
				for (File collectionFile : collectionDir) {
					if (!collectionFile.isDirectory()) {
						continue;
					}
					String collectionName = FilenameUtils.getName(collectionFile.getName());
					DTLayerCollection collection = new DTLayerCollection();
					collection.setCollectionName(collectionName);
					File[] layerFiles = collectionFile.listFiles();
					DTLayerList dtLayerList = new DTLayerList();
					DTLayer neatlineLayer = null;
					for (File layerFile : layerFiles) { // 레이어
						DTLayer dtLayer = null;
						String fileName = layerFile.getName();
						int Idx = fileName.lastIndexOf(".");
						String layerName = fileName.substring(0, Idx);

						String ext = FilenameUtils.getExtension(layerFile.getPath());
						if (!ext.endsWith("shp")) {
							continue;
						}
						try {
							dtLayer = new SHPFileLayerParser().parseDTLayer(epsg, layerFile);
						} catch (Exception e) {
							e.printStackTrace();
							status += layerName + " : Geoserver Layer가 손상되어 있습니다." + brTag;
						}

						if (this.neatLine != null) {
							if (this.neatLine.equalsIgnoreCase(layerName)) {
								collection.setNeatLine(dtLayer);
								neatlineLayer = dtLayer;
							} else {
								dtLayerList.add(dtLayer);
							}
						} else {
							dtLayerList.add(dtLayer);
						}
					}
					if (dtLayerList.size() > 0) {
						if (this.neatLine != null && neatlineLayer.getSimpleFeatureCollection() == null) {
							this.status += collectionName + " : 일치하는 도곽이 없습니다." + this.brTag;
						}
						if (this.neatLine != null && neatlineLayer.getSimpleFeatureCollection() != null) {
							DTLayerCollection setDTCollection = setForestNeatLine(collection, neatlineLayer);
							setDTCollection.setCollectionName(collection.getCollectionName());
							setDTCollection.setLayers(dtLayerList);
							setDTCollection.setMapRule(
									new MapSystemRule().setMapSystemRule(setDTCollection.getCollectionName()));
							if (setDTCollection != null) {
								collectionList.add(setDTCollection);
							} else {
								this.status += collectionName + " : 일치하는 도곽이 없습니다." + this.brTag;
							}
						}
					} else {
						if (!collection.getCollectionName().equals(this.neatLine)) {
							this.status += collectionName + " : 잘못된  Geoserver Layer 이거나 지원하지 않는 Layer Type 입니다."
									+ brTag;
							continue;
						}
					}
				}
				this.collectionList = collectionList;
			}
		}
	}

	private void parseUnderGeoserverLayers(String fname) {

		// geolayersPath
		if (geoLayersPath.exists() == false) {
			status += "경로가 존재하지 않습니다" + brTag;
		}
		File[] layerFiles = geoLayersPath.listFiles();
		if (layerFiles.length == 0) {
			this.collectionList = null;
			status += "요청 형식(1개 GeoServer WorkSpace)이 다릅니다." + brTag;
		} else {
			DTLayerCollectionList collectionList = new DTLayerCollectionList();
			DTLayerCollection collection = new DTLayerCollection();
			collection.setCollectionName(fname);
			DTLayerList dtLayerList = new DTLayerList();
			for (File layerFile : layerFiles) { // 레이어
				DTLayer dtLayer = null;
				String ext = FilenameUtils.getExtension(layerFile.getPath());
				if (!ext.endsWith("shp")) {
					continue;
				}
				try {
					dtLayer = new SHPFileLayerParser().parseDTLayer(epsg, layerFile);
				} catch (Exception e) {
					e.printStackTrace();
					String fileName = layerFile.getName();
					int Idx = fileName.lastIndexOf(".");
					String layerName = fileName.substring(0, Idx);
					status += layerName + " : Geoserver Layer가 손상되어 있습니다." + brTag;
				}
				if (dtLayer != null) {
					dtLayerList.add(dtLayer);
				}
			}
			if (dtLayerList.size() > 0) {
				collection.setLayers(dtLayerList);
				collectionList.add(collection);
				this.collectionList = collectionList;
			} else {
				status += "요청 형식(1개 GeoServer WorkSpace)이 다릅니다." + brTag;
			}
		}
	}

	private void parseDigitalGeoserverLayers() {

		// geolayersPath
		if (geoLayersPath.exists() == false) {
			System.out.println("경로가 존재하지 않습니다");
		}
		File[] layerFiles = geoLayersPath.listFiles();
		if (layerFiles.length == 0) {
			this.collectionList = null;
			status += "요청 형식(1개 도엽 당 1개 Geoserver Workspace)이 다릅니다." + brTag;
		} else {
			DTLayerCollectionList collectionList = new DTLayerCollectionList();
			for (File layerFile : layerFiles) { // 작업공간 목록
				String collectionName = FilenameUtils.getName(layerFile.getName());
				DTLayerCollection collection = new DTLayerCollection();
				collection.setCollectionName(collectionName);
				boolean isDir = layerFile.isDirectory();
				if (isDir) {
					File[] subFiles = layerFile.listFiles();
					if (subFiles.length == 0) {
						this.collectionList = null;
						status += "요청 형식(1개 도엽 당 1개 Geoserver Workspace)이 다릅니다." + brTag;
					}
					DTLayerList dtLayerList = new DTLayerList();
					for (File layer : subFiles) { // 레이어 목록
						DTLayer dtLayer = null;
						String ext = FilenameUtils.getExtension(layer.getPath());
						if (!ext.endsWith("shp")) {
							continue;
						}
						try {
							dtLayer = new SHPFileLayerParser().parseDTLayer(epsg, layer);
						} catch (Exception e) {
							e.printStackTrace();
							String fileName = layer.getName();
							int Idx = fileName.lastIndexOf(".");
							String layerName = fileName.substring(0, Idx);
							status += layerName + " : Geoserver Layer가 손상되어 있습니다." + brTag;
						}
						if (this.neatLine != null) {
							if (this.neatLine.equalsIgnoreCase(dtLayer.getLayerID())) {
								collection.setNeatLine(dtLayer);
							} else {
								dtLayerList.add(dtLayer);
							}
						} else {
							dtLayerList.add(dtLayer);
						}
					}
					collection.setLayers(dtLayerList);
					if (this.neatLine != null && collection.getNeatLine() == null) {
						this.status += collectionName + " : 일치하는 " + this.neatLine + " 도곽이 없습니다." + this.brTag;
						collection = null;
					} else if (this.neatLine != null && collection.getNeatLine().getSimpleFeatureCollection() != null) {
						MapSystemRule mapRule = new MapSystemRule().setMapSystemRule(collection.getCollectionName());
						if (mapRule != null) {
							collection.setMapRule(mapRule);
						}
						collectionList.add(collection);
					} else if (this.neatLine == null) {
						MapSystemRule mapRule = new MapSystemRule().setMapSystemRule(collection.getCollectionName());
						if (mapRule != null) {
							collection.setMapRule(mapRule);
						}
						collectionList.add(collection);
					}
					this.collectionList = collectionList;
				}
			}
		}
	}

	private void parseUndergroundQA20File() {

		if (this.unZipFile.isFiles()) {
			FileMetaList metaList = this.unZipFile.getFileMetaList();
			String collectionName = this.unZipFile.getEntryName();
			DTLayerCollectionList collectionList = new DTLayerCollectionList();
			DTLayerList layerList = new DTLayerList();
			for (int i = 0; i < metaList.size(); i++) {
				FileMeta fileMeta = metaList.get(i);
				String filePath = fileMeta.getFilePath();
				String fileName = fileMeta.getFileName();
				int pos = fileName.lastIndexOf(".");
				String name = fileName.substring(0, pos);
				if (fileName.endsWith("shp")) {
					try {
						DTLayer layer = new SHPFileLayerParser().parseDTLayer(epsg, filePath, name);
						if (layer != null) {
							layerList.add(layer);
						}
					} catch (Exception e) {
						status += fileName + " : 파일이 손상되어 있습니다." + brTag;
					}
				}
			}
			DTLayerCollection collection = new DTLayerCollection();
			collection.setCollectionName(collectionName);
			if (layerList.size() > 0) {
				collection.setLayers(layerList);
				collectionList.add(collection);
			} else {
				status += collectionName + " : 잘못된 shp파일이거나 지원하지 않는 레이어타입입니다." + brTag;
			}
			if (collectionList.size() > 0) {
				this.collectionList = collectionList;
			} else {
				this.collectionList = null;
				status += "레이어 정의 옵션과 검수 옵션이 요청 파일에 연관되지 않습니다." + brTag;
			}
		} else {
			this.collectionList = null;
			status += "요청 형식(지하도2.0-shp)과 대상 파일 형식이 다릅니다." + brTag;
			status += "검수 대상이 폴더 형태가 아닌 파일 형태가 필요합니다." + brTag;
		}
	}

	/**
	 * @author DY.Oh
	 * @since 2018. 3. 29. 오후 6:10:03 void
	 * @decription
	 */
	private void parseUndergroundQA10File() {

		if (this.unZipFile.isFiles()) {
			FileMetaList metaList = this.unZipFile.getFileMetaList();
			DTLayerCollectionList collectionList = new DTLayerCollectionList();
			FileDTLayerCollectionReader collectionReader = new FileDTLayerCollectionReader();
			for (int i = 0; i < metaList.size(); i++) {
				FileMeta fileMeta = metaList.get(i);
				String filePath = fileMeta.getFilePath();
				String fileName = fileMeta.getFileName();
				int pos = fileName.lastIndexOf(".");
				String name = fileName.substring(0, pos);
				if (fileName.endsWith("dxf")) {
					DTLayerCollection collection = null;
					try {
						collection = collectionReader.dxfLayerParse(this.epsg, filePath, name, null);
						if (collection == null) {
							status += fileName + " : 잘못된 dxf파일이거나 지원하지 않는 레이어타입입니다." + brTag;
							continue;
						} else {
							collectionList.add(collection);
						}
					} catch (Exception e) {
						status += fileName + " : 파일이 손상되어 있습니다." + brTag;
					}
				}
			}
			if (collectionList.size() > 0) {
				this.collectionList = collectionList;
			} else {
				this.collectionList = null;
				status += "전체 파일에서 " + this.neatLine + " 도곽이 없습니다." + brTag;
			}
		} else {
			this.collectionList = null;
			status += "요청 형식과 대상(지하시설물1.0 - dxf) 파일 형식이 다릅니다." + brTag;
			status += "검수 대상이 폴더 형태가 아닌 파일 형태가 필요합니다." + brTag;
		}
	}

	/**
	 * @author DY.Oh
	 * @since 2018. 3. 29. 오후 6:04:16 void
	 * @decription
	 */
	private void parseNumericalQA20NgiFile() {

		if (unZipFile.isFiles()) {
			FileDTLayerCollectionReader collectionReader = new FileDTLayerCollectionReader();
			FileMetaList metaList = unZipFile.getFileMetaList();
			DTLayerCollectionList collectionList = new DTLayerCollectionList();
			for (int i = 0; i < metaList.size(); i++) {
				FileMeta fileMeta = metaList.get(i);
				String filePath = fileMeta.getFilePath();
				String fileName = fileMeta.getFileName();
				int pos = fileName.lastIndexOf(".");
				String name = fileName.substring(0, pos);
				DTLayerCollection collection = null;
				try {
					if (fileName.endsWith("ngi")) {
						collection = collectionReader.ngiLayerParse(epsg, filePath, name, neatLine);

						if (collection == null) {
							status += fileName + " : 잘못된 ngi파일이거나 지원하지 않는 레이어타입입니다." + brTag;
							continue;
						}
					}
					if (this.neatLine != null && collection.getNeatLine() == null) {
						this.status += fileName + " : 일치하는 " + this.neatLine + " 도곽이 없습니다." + this.brTag;
						collection = null;
					}
					if (this.neatLine != null && collection.getNeatLine() != null) {
						MapSystemRule mapRule = new MapSystemRule().setMapSystemRule(collection.getCollectionName());
						if (mapRule != null) {
							collection.setMapRule(mapRule);
						}
						collectionList.add(collection);
					}
					if (this.neatLine == null) {
						MapSystemRule mapRule = new MapSystemRule().setMapSystemRule(collection.getCollectionName());
						if (mapRule != null) {
							collection.setMapRule(mapRule);
						}
						collectionList.add(collection);
					}
				} catch (Exception e) {
					status += fileName + " : 파일이 손상되어 있습니다." + brTag;
				}
			}
			if (collectionList.size() > 0) {
				this.collectionList = collectionList;
			} else {
				this.collectionList = null;
				status += "전체 파일에서 " + this.neatLine + " 도곽이 없습니다." + brTag;
			}
		} else {
			this.collectionList = null;
			status += "요청 형식(수치지도2.0 - ngi)과 대상 파일 구조가 다릅니다." + brTag;
			status += "검수 대상이 폴더 형태가 아닌 파일 형태가 필요합니다." + brTag;
		}
	}

	/**
	 * @author DY.Oh
	 * @since 2018. 3. 29. 오후 5:49:52 void
	 * @decription
	 */
	private void parseNumericalQA20ShpFile() {

		if (this.unZipFile.isDir()) {
			DTLayerCollectionList collectionList = new DTLayerCollectionList();
			Map<String, FileMetaList> dirMetaList = this.unZipFile.getDirMetaList();
			Iterator<?> dirIterator = dirMetaList.keySet().iterator();
			while (dirIterator.hasNext()) {
				String dirPath = (String) dirIterator.next();
				String dirName = getDirName(dirPath);
				DTLayerCollection collection = new DTLayerCollection();
				collection.setCollectionName(dirName);
				DTLayerList layerList = new DTLayerList();
				FileMetaList metaList = dirMetaList.get(dirPath);
				for (int i = 0; i < metaList.size(); i++) {
					DTLayer layer = new DTLayer();
					FileMeta fileMeta = metaList.get(i);
					String fileName = fileMeta.getFileName();
					int pos = fileName.lastIndexOf(".");
					String name = fileName.substring(0, pos);
					if (fileName.endsWith("shp")) {
						try {
							layer = new SHPFileLayerParser().parseDTLayer(epsg, dirPath, name);
							if (this.neatLine != null) {
								if (this.neatLine.equals(layer.getLayerID())) {
									collection.setNeatLine(layer);
								} else {
									layerList.add(layer);
								}
							} else {
								layerList.add(layer);
							}
						} catch (Exception e) {
							status += fileName + " : 파일이 손상되어 있습니다." + brTag;
						}
					}
				}
				if (layerList.size() > 0) {
					collection.setLayers(layerList);
				} else {
					status += dirName + " : 잘못된 shp파일이거나 지원하지 않는 레이어타입입니다." + brTag;
					continue;
				}

				if (this.neatLine != null && collection.getNeatLine().getSimpleFeatureCollection() == null) {
					this.status += dirName + " : 일치하는 " + this.neatLine + " 도곽이 없습니다." + this.brTag;
					collection = null;
				}
				if (this.neatLine != null && collection.getNeatLine().getSimpleFeatureCollection() != null) {
					MapSystemRule mapRule = new MapSystemRule().setMapSystemRule(collection.getCollectionName());
					if (mapRule != null) {
						collection.setMapRule(mapRule);
					}
					collectionList.add(collection);
				}
				if (this.neatLine == null) {
					MapSystemRule mapRule = new MapSystemRule().setMapSystemRule(collection.getCollectionName());
					if (mapRule != null) {
						collection.setMapRule(mapRule);
					}
					collectionList.add(collection);
				}
			}
			if (collectionList.size() > 0) {
				this.collectionList = collectionList;
			} else {
				this.collectionList = null;
				status += "전체 파일에서 " + this.neatLine + " 에 해당하는 도곽이 없습니다." + brTag;
			}
		} else {
			this.collectionList = null;
			status += "요청 형식(수치지도2.0 - shp)과 대상 파일 형식이 다릅니다." + brTag;
			status += "검수 대상이 파일 형태가 아닌 폴더 형태가 필요합니다." + brTag;
		}
	}

	/**
	 * @author DY.Oh
	 * @since 2018. 3. 29. 오후 3:10:06
	 * @decription
	 */
	private void parseNumericalQA10File() {

		if (this.unZipFile.isFiles()) {
			FileMetaList metaList = this.unZipFile.getFileMetaList();
			DTLayerCollectionList collectionList = new DTLayerCollectionList();
			FileDTLayerCollectionReader collectionReader = new FileDTLayerCollectionReader();

			for (int i = 0; i < metaList.size(); i++) {
				FileMeta fileMeta = metaList.get(i);
				String filePath = fileMeta.getFilePath();
				String fileName = fileMeta.getFileName();
				int pos = fileName.lastIndexOf(".");
				String name = fileName.substring(0, pos);

				if (fileName.endsWith("dxf")) {
					DTLayerCollection collection = null;
					try {
						collection = collectionReader.dxfLayerParse(this.epsg, filePath, name, this.neatLine);

						if (collection == null) {
							status += fileName + " : 잘못된 dxf파일이거나 지원하지 않는 레이어타입입니다." + brTag;
							continue;
						}
						DTLayer neatLineLayer = collection.getNeatLine();
						if (this.neatLine != null && neatLineLayer == null) {
							this.status += fileName + " : 일치하는 " + this.neatLine + " 도곽이 없습니다." + this.brTag;
							collection = null;
							continue;
						}
						if (this.neatLine != null && neatLineLayer != null) {
							MapSystemRule mapRule = new MapSystemRule()
									.setMapSystemRule(collection.getCollectionName());
							if (mapRule != null) {
								collection.setMapRule(mapRule);
							}
							collectionList.add(collection);
						}
						if (this.neatLine == null) {
							collectionList.add(collection);
						}
					} catch (Exception e) {
						status += fileName + " : 파일이 손상되어 있습니다." + brTag;
					}
				}
			}
			if (collectionList.size() > 0) {
				this.collectionList = collectionList;
			} else {
				this.collectionList = null;
				status += "전체 파일에서 " + this.neatLine + " 도곽이 없습니다." + brTag;
			}
		} else {
			this.collectionList = null;
			status += "요청 형식(수치지도 1.0-dxf)과 대상 파일 형식이 다릅니다." + brTag;
			status += "검수 대상이 폴더 형태가 아닌 파일 형태가 필요합니다." + brTag;
		}
	}

	private void parseForestQA20File() {

		if (this.unZipFile.isDir()) {
			Map<String, FileMetaList> dirMetaList = this.unZipFile.getDirMetaList();
			DTLayerCollectionList collectionList = new DTLayerCollectionList();
			Iterator<?> dirIterator = dirMetaList.keySet().iterator();
			while (dirIterator.hasNext()) {
				String dirPath = (String) dirIterator.next();
				String dirName = getDirName(dirPath);
				DTLayerCollection collection = new DTLayerCollection();
				collection.setCollectionName(dirName);
				DTLayerList layerList = new DTLayerList();
				FileMetaList metaList = dirMetaList.get(dirPath);
				DTLayer neatlineLayer = new DTLayer();
				for (int i = 0; i < metaList.size(); i++) {
					DTLayer layer = new DTLayer();
					FileMeta fileMeta = metaList.get(i);
					String fileName = fileMeta.getFileName();
					int pos = fileName.lastIndexOf(".");
					String name = fileName.substring(0, pos);
					if (fileName.endsWith("shp")) {
						try {
							layer = new SHPFileLayerParser().parseDTLayer(epsg, dirPath, name);
							if (this.neatLine != null) {
								if (this.neatLine.equals(layer.getLayerID())) {
									neatlineLayer = layer;
								} else {
									layerList.add(layer);
								}
							} else {
								layerList.add(layer);
							}
						} catch (Exception e) {
							status += fileName + " : 파일이 손상되어 있습니다." + brTag;
						}
					}
				}
				if (layerList.size() > 0) {
					if (this.neatLine != null && neatlineLayer.getSimpleFeatureCollection() == null) {
						this.status += dirName + " : 일치하는 도곽이 없습니다." + this.brTag;
					}
					if (this.neatLine != null && neatlineLayer.getSimpleFeatureCollection() != null) {
						DTLayerCollection setDTCollection = setForestNeatLine(collection, neatlineLayer);
						setDTCollection.setCollectionName(collection.getCollectionName());
						setDTCollection.setLayers(layerList);
						MapSystemRule mapRule = new MapSystemRule()
								.setMapSystemRule(setDTCollection.getCollectionName());
						if (mapRule != null) {
							setDTCollection.setMapRule(mapRule);
						}
						if (setDTCollection.getNeatLine() != null) {
							collectionList.add(setDTCollection);
						} else {
							this.status += dirName + " : 일치하는 도곽이 없습니다." + brTag;
						}
					}
				} else {
					if (!collection.getCollectionName().equals(this.neatLine)) {
						this.status += dirName + " : 잘못된 shp파일이거나 지원하지 않는 레이어타입입니다." + brTag;
						continue;
					}
				}
			}
			if (collectionList.size() > 0) {
				this.collectionList = collectionList;
			} else {
				status += "전체 파일에서 " + this.neatLine + " 도곽이 없습니다." + brTag;
				this.collectionList = null;
			}
		} else {
			this.collectionList = null;
			status += "요청 형식(임상도-shp)과 대상 파일 형식이 다릅니다." + brTag;
			status += "검수 대상이 파일 형태가 아닌 폴더 형태가 필요합니다." + brTag;
		}
	}

	/**
	 * @author DY.Oh
	 * @since 2018. 4. 17. 오전 11:20:17
	 * @param collectionList2
	 * @param neatlineLayer
	 * @decription
	 */
	private DTLayerCollection setForestNeatLine(DTLayerCollection collection, DTLayer neatlineLayer) {

		SimpleFeatureCollection neatSfc = neatlineLayer.getSimpleFeatureCollection();
		SimpleFeatureIterator iter = neatSfc.features();

		DTLayerCollection dtLayerCollection = new DTLayerCollection();
		DefaultFeatureCollection dfc = new DefaultFeatureCollection();
		String collectionName = collection.getCollectionName();
		while (iter.hasNext()) {
			SimpleFeature sf = iter.next();
			Object num = sf.getAttribute("ALLNUM");
			if (num != null) {
				String numStr = num.toString();
				if (collectionName.equals(numStr)) {
					dfc.add(sf);
					DTLayer neatLine = new DTLayer();
					neatLine.setLayerID("index");
					neatLine.setSimpleFeatureCollection(dfc);
					dtLayerCollection.setNeatLine(neatLine);
				}
			}
		}
		iter.close();
		return dtLayerCollection;
	}

	private static String getDirName(String dirName) {

		String[] dirs = dirName.split("\\\\");
		return dirs[dirs.length - 1];
	}

}
