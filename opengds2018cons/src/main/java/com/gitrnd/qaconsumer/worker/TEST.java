/**
 * 
 */
package com.gitrnd.qaconsumer.worker;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
public class TEST {

	/**
	 * @param args
	 * @throws ParseException
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {

		JSONParser parser = new JSONParser();
		Object obj = parser.parse(new FileReader("d:\\option.json"));
		JSONObject param = (JSONObject) obj;

		JSONObject geoserver = (JSONObject) param.get("geoserver");
		JSONArray layers = (JSONArray) param.get("layers");
		JSONObject options = (JSONObject) param.get("options");

		// geoserver
		String baseUrl = (String) geoserver.get("url");
		String user = (String) geoserver.get("user");
		String pw = (String) geoserver.get("password");
		String workspace = (String) geoserver.get("workspace");
		JSONArray geoLayers = (JSONArray) geoserver.get("layers");

		DTLayerList dtLayers = new DTLayerList();
		for (int i = 0; i < geoLayers.size(); i++) {
			String geoLayer = (String) geoLayers.get(i);
			QALayerParser layerP = new QALayerParser(baseUrl, user, pw, workspace, geoLayer);
			layerP.init();
			DTLayer dtLayer = layerP.layerParse();
			dtLayers.add(dtLayer);
		}
		DTLayerCollection dtCollection = new DTLayerCollection();
		dtCollection.setLayers(dtLayers);

		// options
		JSONArray typeValidate = (JSONArray) options.get("definition");
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
				JSONObject json = new JSONObject();
				json.put("name", (String) lyrItem.get("name"));
				json.put("layers", (JSONArray) lyrItem.get("layers"));
				typeValidate.add(json);
			}
		}

		QATypeParser validateTypeParser = new QATypeParser(typeValidate);
		QALayerTypeList validateLayerTypeList = validateTypeParser.getValidateLayerTypeList();

		ErrorLayer errorLayer = executorValidate(dtCollection, validateLayerTypeList);
		if (errorLayer != null) {
			ErrorLayerParser errLayerP = new ErrorLayerParser();
			JSONObject errLayerJson = errLayerP.parseGeoJSON(errorLayer);
		}
	}

	/**
	 * @param dtCollection
	 * @param validateLayerTypeList
	 */
	private static ErrorLayer executorValidate(DTLayerCollection dtCollection, QALayerTypeList validateLayerTypeList) {

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
