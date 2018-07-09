/**
 * 
 */
package com.git.gdsbuilder.type.validate.option.type;

/**
 * @className FTMQAOptions.java
 * @description
 * @author DY.Oh
 * @date 2018. 3. 15. 오전 11:25:57
 */
public class FTMQAOptions {

	public enum Type {

		FCODELOGICALATTRIBUTE("FCodeLogicalAttribute", "FCode불일치","속성오류"), 
		FLABELLOGICALATTRIBUTE("FLabelLogicalAttribute", "Label불일치","속성오류"), 
		DISSOLVE("Dissolve", "인접속성병합오류","속성오류"), 
		
		MULTIPART("MultiPart", "다중객체존재오류","그래픽오류"),
		SMALLAREA("SmallArea", "미세폴리곤존재오류", "그래픽오류"),
		FENTITYINHOLE("FEntityInHole", "홀(Hole)폴리곤존재오류","그래픽오류"),
		SELFENTITY("SelfEntity", "단독존재오류","그래픽오류");
		
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
