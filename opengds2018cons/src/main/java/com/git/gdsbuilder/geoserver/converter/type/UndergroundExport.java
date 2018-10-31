package com.git.gdsbuilder.geoserver.converter.type;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.git.gdsbuilder.geoserver.converter.unzip.UndergroundUnzip;

public class UndergroundExport {
	private final static String tmp_dir_prefix ="temp_";
	
	private static final int BUFFER_SIZE = 4096;	
	private final static String SERVICE = "WFS";
	private final static String REQUEST = "GetFeature";
	private final static String VERSION = "1.0.0";
	private final static String OUTPUTFORMAT = "SHAPE-ZIP";
	
	private final String serverURL;
	private final String workspace;
	private final List<String> layerNames;
	private final Path outputFolderPath;
	private final String srs;
	
	public UndergroundExport(String serverURL, String workspace, List<String> layerNames, String outputFolderPath, String srs){
		if(serverURL.isEmpty()||workspace.isEmpty()||layerNames==null||outputFolderPath.isEmpty()||srs.isEmpty()){
			throw new IllegalArgumentException("필수파라미터 입력안됨");
		}
		this.serverURL = serverURL;
		this.workspace = workspace;
		this.layerNames = layerNames;
		this.outputFolderPath = FileSystems.getDefault().getPath(outputFolderPath);
		this.srs = srs;
		this.createFileDirectory(outputFolderPath);
		
		//기본 폴더에 현재시간 및 난수 추가
		/*long time = System.currentTimeMillis(); 
		SimpleDateFormat dayTime = new SimpleDateFormat("yyyymmddhhmmss");
		this.outputFolderPath = outputFolderPath + File.separator + dayTime.format(new Date(time)) + new Random().nextInt(10000);
		createFileDirectory(this.outputFolderPath);*/
	}
	
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
	 * */
	public int export(){
		int flag = 500;
		Path tmp;
		try {
			tmp = Files.createTempDirectory(outputFolderPath, tmp_dir_prefix);//temp 디렉토리 생성(임시폴더)
					if (layerNames != null) {
						for (String layerName : layerNames) {
							StringBuffer urlBuffer = new StringBuffer();
							urlBuffer.append(serverURL);
							urlBuffer.append("/" + workspace + "/ows?");
							urlBuffer.append("service=" + SERVICE);
							urlBuffer.append("&");
							urlBuffer.append("version=" + VERSION);
							urlBuffer.append("&");
							urlBuffer.append("request=" + REQUEST);
							urlBuffer.append("&");
							urlBuffer.append("typeName=" + workspace + ":" + layerName);
							urlBuffer.append("&");
							urlBuffer.append("outputFormat=" + OUTPUTFORMAT);
							urlBuffer.append("&");
							urlBuffer.append("srsname=" + srs);
							try {
								this.downloadFile(urlBuffer.toString(), tmp.toString());//다운로드 요청
							} catch (IOException e) {
								// TODO Auto-generated catch block
								flag = 703;
								System.err.println(layerName + " 레이어 다운로드 에러");
								return flag;
							}
						}
						File zipFolder = new File(tmp.toString());
						if(!zipFolder.exists()){
							System.err.println("폴더가 존재하지 않습니다");
						}else{
							File[] fileList = zipFolder.listFiles();
							for (int i = 0; i < fileList.length; i++) {
								File file = fileList[i];
								if (file.isFile()) {
									String fFullName = file.getName();

									if (fFullName.endsWith(".zip")) {
										UndergroundUnzip unZipFile = new UndergroundUnzip(file);
										try {
											flag = unZipFile.decompress();
											file.delete();
										} catch (Throwable e) {
											// TODO Auto-generated catch block
											flag = 702;
											System.err.println("압축풀기 실패");
											file.delete();
											return flag;
										}
									} 
								}
							}
						}
					} else {
						flag = 701;
						System.err.println("레이어 리스트 NULL");
					}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.err.println("정상적으로 export되지 않았습니다.");
		}
		return flag;
	}
	
	 /**
     * Downloads a file from a URL
     * @param fileURL HTTP URL of the file to be downloaded
     * @param saveDir path of the directory to save the file
     * @throws IOException
     */
    private void downloadFile(String fileURL, String saveDir)
            throws IOException {
        URL url = new URL(fileURL);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        int responseCode = httpConn.getResponseCode();
 
        // always check HTTP response code first
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String fileName = "";
            String disposition = httpConn.getHeaderField("Content-Disposition");
            String contentType = httpConn.getContentType();
            int contentLength = httpConn.getContentLength();
 
            if (disposition != null) {
                // extracts file name from header field
                int index = disposition.indexOf("filename=");
                if (index > 0) {
                    fileName = disposition.substring(index + 9,
                            disposition.length());
                }
            } else {
                // extracts file name from URL
                fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1,
                        fileURL.length());
            }
 
            System.out.println("Content-Type = " + contentType);
            System.out.println("Content-Disposition = " + disposition);
            System.out.println("Content-Length = " + contentLength);
            System.out.println("fileName = " + fileName);
 
            // opens input stream from the HTTP connection
            InputStream inputStream = httpConn.getInputStream();
            String saveFilePath = saveDir + File.separator + fileName;
             
            // opens an output stream to save into file
            FileOutputStream outputStream = new FileOutputStream(saveFilePath);
 
            int bytesRead = -1;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
 
            outputStream.close();
            inputStream.close();
 
            System.out.println("File downloaded");
        } else {
            System.out.println("No file to download. Server replied HTTP code: " + responseCode);
        }
        httpConn.disconnect();
    }
    
    
    
    private void createFileDirectory(String directory) {
		File file = new File(directory);
		if (!file.exists()) {
			file.mkdirs();
		}
	}
}
