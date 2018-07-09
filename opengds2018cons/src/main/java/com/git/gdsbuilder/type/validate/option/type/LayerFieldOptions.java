/**
 * 
 */
package com.git.gdsbuilder.type.validate.option.type;

/**
 * @className LayerFieldOptions.java
 * @description
 * @author DY.Oh
 * @date 2018. 4. 2. 오전 10:14:08
 */
public class LayerFieldOptions {
	
	public enum Type {

	LAYERFieldFIXMISS("LayerFixMiss", "필드구조오류", "레이어오류"),
	LAYERTypeFIXMISS("LayerFixMiss", "Geometry타입오류", "레이어오류");
		
		String errNameE;
		String errName;
		String errType;

		private Type(String errNameE, String errName, String errType) {
			this.errNameE = errNameE;
			this.errName = errName;
			this.errType = errType;
		}

		public String getErrNameE() {
			return errNameE;
		}

		public void setErrNameE(String errNameE) {
			this.errNameE = errNameE;
		}

		public String getErrName() {
			return errName;
		}

		public void setErrName(String errName) {
			this.errName = errName;
		}

		public String getErrType() {
			return errType;
		}

		public void setErrType(String errType) {
			this.errType = errType;
		}		
	}
}
