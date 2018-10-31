package com.git.gdsbuilder.geoserver.converter;

/**
 * @Description Geoserver Data -> 검수 File 
 * @author SG.Lee
 * @Date 2018. 10. 30. 오전 9:52:33
 * */
public interface GeoserverDataConverter {
	/**
	 * @Description Geoserver Data -> 수치지도 구조의 File
	 * @author SG.Lee
	 * @Date 2018. 10. 30. 오전 9:57:15
	 * @return int 200 성공
	 * 			   500 내부에러
	 * 			   700 파일구조에러
	 * 			   701 레이어 리스트 NULL
	 *             702 파일손상
	 *             703 Geoserver Layer 다운에러
	 * */
	public int digitalExport();
	
	/**
	 * @Description 
	 * @author SG.Lee
	 * @Date 2018. 10. 29. 오후 3:48:38
	 * @return int 200 성공
	 * 			   500 내부에러
	 * 			   700 파일구조에러
	 * 			   701 레이어 리스트 NULL
	 *             702 파일손상
	 *             703 Geoserver Layer 다운에러
	 *             704 workspace 2개이상
	 * */
	public int undergroundExport();
	
	/**
	 * @Description 
	 * @author SG.Lee
	 * @Date 2018. 10. 29. 오후 3:48:38
	 * @return int 200 성공
	 * 			   500 내부에러
	 * 			   700 파일구조에러
	 * 			   701 레이어 리스트 NULL
	 *             702 파일손상
	 *             703 Geoserver Layer 다운에러
	 *             704 workspace 2개이상
	 * */
	public int forestExport(String nearLine);
}
