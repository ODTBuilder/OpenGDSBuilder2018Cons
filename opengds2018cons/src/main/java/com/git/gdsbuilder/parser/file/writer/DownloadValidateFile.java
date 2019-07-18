/**
 * 
 */
package com.git.gdsbuilder.parser.file.writer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

/**
 * 다른 경로(원격 또는 로컬)에 있는 파일을 특졍 경로에 다운로드 하는 클래스.
 * 
 * @author DY.Oh
 *
 */
public class DownloadValidateFile {

	/**
	 * 다른 경로(원격 또는 로컬)에 있는 파일을 특졍 경로에 다운로드 함.
	 * 
	 * @param url  원격 또는 로컬에 존재하는 URI 형태의 파일 경로
	 * @param file 파일을 다운로드 할 경로
	 * @return {@code true} : 다운로드 성공
	 *         <p>
	 *         {@code false} : 다운로드 실패
	 * @throws IOException {@link IOException}
	 * 
	 * @author DY.Oh
	 */
	public boolean download(String url, String file) throws IOException {

		boolean isTrue = true;

		BufferedInputStream bufferedIS = null;
		FileOutputStream fileOS = null;
		try {
			URL urlObj = new URL(url);
			bufferedIS = new BufferedInputStream(urlObj.openStream());

			String fileName = null;
			int index = url.indexOf("file=");
			if (index > 0) {
				fileName = url.substring(index + 5, url.length());
			}
			String saveFilePath = file + File.separator + URLDecoder.decode(fileName, "UTF-8");

			fileOS = new FileOutputStream(saveFilePath);

			int data = bufferedIS.read();
			while (data != -1) {
				fileOS.write(data);
				data = bufferedIS.read();
			}
		} catch (MalformedURLException e) {
			isTrue = false;
			e.printStackTrace();
		} catch (IOException e) {
			isTrue = false;
			e.printStackTrace();
		} finally {
			try {
				if (fileOS != null) {
					fileOS.close();
				}
				if (bufferedIS != null) {
					bufferedIS.close();
				}
			} catch (IOException e) {
				isTrue = false;
				e.printStackTrace();
			}
		}
		return isTrue;

//		URL url = new URL(path);
//		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
//
//		int responseCode = httpConn.getResponseCode();
//		if (responseCode == HttpURLConnection.HTTP_OK) {
//			String fileName = "";
//			String disposition = httpConn.getHeaderField("Content-Disposition");
//			String contentType = httpConn.getContentType();
//			int contentLength = httpConn.getContentLength();
//
//			if (disposition != null) {
//				int index = disposition.indexOf("filename=");
//				if (index > 0) {
//					fileName = disposition.substring(index + 9, disposition.length());
//				}
//			} else {
//				fileName = path.substring(path.lastIndexOf("/") + 1, path.length());
//			}
//
//			System.out.println("Content-Type = " + contentType);
//			System.out.println("Content-Disposition = " + disposition);
//			System.out.println("Content-Length = " + contentLength);
//			System.out.println("fileName = " + fileName);
//
//			InputStream inputStream = httpConn.getInputStream();
//			String saveFilePath = zipfilePath + File.separator + URLDecoder.decode(fileName, "UTF-8");
//			FileOutputStream outputStream = new FileOutputStream(saveFilePath);
//			int bytesRead = -1;
//			byte[] buffer = new byte[BUFFER_SIZE];
//			while ((bytesRead = inputStream.read(buffer)) != -1) {
//				outputStream.write(buffer, 0, bytesRead);
//			}
//			outputStream.close();
//			inputStream.close();
//			return true;
//		} else {
//			return false;
//		}
	}
}
