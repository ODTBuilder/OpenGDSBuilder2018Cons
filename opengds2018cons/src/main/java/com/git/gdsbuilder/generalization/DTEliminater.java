package com.git.gdsbuilder.generalization;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.DefaultFeatureCollection;
import org.opengis.feature.simple.SimpleFeature;

import com.vividsolutions.jts.geom.Geometry;

public class DTEliminater {

	public SimpleFeatureCollection eliminate(SimpleFeatureCollection inputSfc, double tolerance) {

		DefaultFeatureCollection dfc = new DefaultFeatureCollection();
		SimpleFeatureIterator sfIter = inputSfc.features();

		while (sfIter.hasNext()) {
			SimpleFeature feature = sfIter.next();
			Geometry geom = (Geometry) feature.getDefaultGeometry();
			if (geom.isValid() && geom.isSimple()) {
				// linestring
				if (geom.getGeometryType().equals("LineString") || geom.getGeometryType().equals("MultiLineString")) {
					if (geom.getLength() > tolerance) {
						dfc.add(feature);
					}
				}
				// polygon
				else if (geom.getGeometryType().equals("Polygon") || geom.getGeometryType().equals("MultiPolygon")) {
					if (geom.getArea() > tolerance) {
						dfc.add(feature);
					}
				}
			}
		}
		return dfc;
	}

}
