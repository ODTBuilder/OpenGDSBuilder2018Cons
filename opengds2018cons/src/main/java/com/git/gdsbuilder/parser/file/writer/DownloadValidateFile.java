/**
 * 
 */
package com.git.gdsbuilder.parser.file.writer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;

/**
 * 다른 경로(원격 또는 로컬)에 있는 파일을 특졍 경로에 다운로드 하는 클래스.
 * 
 * @author DY.Oh
 *
 */
public class DownloadValidateFile {

	private static final int BUFFER_SIZE = 4096;

	/**
	 * 다른 경로(원격 또는 로컬)에 있는 파일을 특졍 경로에 다운로드 함.
	 * 
	 * @param path        원격 또는 로컬에 존재하는 파일 경로
	 * @param zipfilePath 파일을 다운로드 할 경로
	 * @return {@code true} : 다운로드 성공
	 *         <p>
	 *         {@code false} : 다운로드 실패
	 * @throws IOException {@link IOException}
	 * 
	 * @author DY.Oh
	 */
	public boolean download(String path, String zipfilePath) throws IOException {

		URL url = new URL(path);
		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();

		int responseCode = httpConn.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) {
			String fileName = "";
			String disposition = httpConn.getHeaderField("Content-Disposition");
			if (disposition != null) {
				int index = disposition.indexOf("filename=");
				if (index > 0) {
					fileName = disposition.substring(index + 9, disposition.length());
				}
			} else {
				fileName = path.substring(path.lastIndexOf("/") + 1, path.length());
			}
			InputStream inputStream = httpConn.getInputStream();
			String saveFilePath = zipfilePath + File.separator + URLDecoder.decode(fileName, "UTF-8");
			FileOutputStream outputStream = new FileOutputStream(saveFilePath);
			int bytesRead = -1;
			byte[] buffer = new byte[BUFFER_SIZE];
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}
			outputStream.close();
			inputStream.close();
			return true;
		} else {
			return false;
		}
	}
}
