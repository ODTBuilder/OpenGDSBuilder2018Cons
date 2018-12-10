/**
 * 
 */
package com.gitrnd.qaconsumer.qa.mobile.service;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import com.git.gdsbuilder.parser.geoserver.QALayerParser;
import com.git.gdsbuilder.parser.json.ErrorLayerParser;
import com.git.gdsbuilder.parser.qa.QATypeParser;
import com.git.gdsbuilder.type.dt.collection.DTLayerCollection;
import com.git.gdsbuilder.type.dt.layer.DTLayer;
import com.git.gdsbuilder.type.dt.layer.DTLayerList;
import com.git.gdsbuilder.type.validate.error.ErrorLayer;
import com.git.gdsbuilder.type.validate.layer.QALayerTypeList;
import com.git.gdsbuilder.validator.collection.CollectionValidator;

/**
 * @author GIT
 *
 */
@ComponentScan
@Service("mobileService")
public class QAMobileServiceImpl implements QAMobileService {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gitrnd.qaconsumer.service.QAMobileService#validate(org.json.simple.
	 * JSONObject)
	 */
	@Override
	public JSONObject validate(JSONObject param) {

		param = (JSONObject) param.get("mobile");
		ErrorLayer errorLayer = null;
		try {
			JSONObject geoserver = (JSONObject) param.get("geoserver");
			JSONArray layers = (JSONArray) param.get("layers");
			JSONObject options = (JSONObject) param.get("options");

			// geoserver
			String baseUrl = (String) geoserver.get("url");
			String user = (String) geoserver.get("user");
			String pw = (String) geoserver.get("password");
			JSONArray geoLayers = (JSONArray) geoserver.get("layers");
			// options
			JSONArray typeValidate = (JSONArray) options.get("definition");

			DTLayerList dtLayers = new DTLayerList();
			for (int i = 0; i < geoLayers.size(); i++) {
				String geoLayer = (String) geoLayers.get(i);
				QALayerParser layerP = new QALayerParser(baseUrl, user, pw, geoLayer);
				layerP.init();
				DTLayer dtLayer = layerP.layerParse();
				dtLayers.add(dtLayer);
			}
			DTLayerCollection dtCollection = new DTLayerCollection();
			dtCollection.setLayers(dtLayers);

			for (int j = 0; j < layers.size(); j++) {
				// layers
				JSONObject lyrItem = (JSONObject) layers.get(j);
				Boolean isExist = false;
				for (int i = 0; i < typeValidate.size(); i++) {
					JSONObject optItem = (JSONObject) typeValidate.get(i);
					String typeName = (String) optItem.get("name");
					if (typeName.equals((String) lyrItem.get("name"))) {
						optItem.put("layers", (JSONArray) lyrItem.get("layers"));
						isExist = true;
					}
				}
				if (!isExist) {
					JSONObject obj = new JSONObject();
					obj.put("name", (String) lyrItem.get("name"));
					obj.put("layers", (JSONArray) lyrItem.get("layers"));
					typeValidate.add(obj);
				}
			}
			QATypeParser validateTypeParser = new QATypeParser(typeValidate);
			QALayerTypeList validateLayerTypeList = validateTypeParser.getValidateLayerTypeList();
			errorLayer = executorValidate(dtCollection, validateLayerTypeList);
		//	if (errorLayer != null) {
				ErrorLayerParser errLayerP = new ErrorLayerParser();
				JSONObject errLayerJson = errLayerP.parseGeoJSON(errorLayer);
				System.out.println(errLayerJson.toString());
				return errLayerJson;
	//		} else {
	//			return null;
	//		}
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * @param dtCollection
	 * @param validateLayerTypeList
	 */
	private ErrorLayer executorValidate(DTLayerCollection dtCollection, QALayerTypeList validateLayerTypeList) {

		CollectionValidator validator = new CollectionValidator(dtCollection, null, validateLayerTypeList);
		ErrorLayer errorLayer = validator.getErrLayer();
		int errSize = errorLayer.getErrFeatureList().size();
		if (errorLayer != null && errSize > 0) {
			return errorLayer;
		} else {
			return null;
		}
	}
}
