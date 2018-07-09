/**
 * 
 */
package com.git.gdsbuilder.type.validate.option.type;

/**
 * @className UFMQAOptions.java
 * @description 
 * @author DY.Oh
 * @date 2018. 3. 15. 오전 11:24:38
 * */
public class UFMQAOptions {

	public enum Type {

		UAVRGDPH10("UAvrgDPH10", "평균심도오류(정위치)", "그래픽오류"), 
		ULEADERLINE("ULeaderline", "지시선교차오류","그래픽오류"), 
		UNODEMISS("UNodeMiss", "시설물선형노드오류","그래픽오류"), 
		SYMBOLINLINE("SymbolInLine", "선형내심볼미존재오류","그래픽오류"),
		LINECROSS("LineCross", "관로상하월오류","그래픽오류"), 
		SYMBOLSDISTANCE("SymbolsDistance", "심볼간격오류","그래픽오류"), 
		USYMBOLOUT("USymbolOut", "심볼단독존재오류","그래픽오류"), 
		FENTITYINHOLE("FEntityInHole", "홀(Hole)존재오류","그래픽오류"), 
		
		UAVRGDPH20("UAvrgDPH20", "평균심도오류(구조화)", "속성오류"),
		SYMBOLDIRECTION("SymbolDirection", "시설물심볼방향오류","속성오류");
		
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
