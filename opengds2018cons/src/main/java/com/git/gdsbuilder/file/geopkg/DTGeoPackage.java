/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2002-2016, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package com.git.gdsbuilder.file.geopkg;

import static java.lang.String.format;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;

import org.apache.commons.dbcp.BasicDataSource;
import org.geotools.data.DataStore;
import org.geotools.data.DataUtilities;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureWriter;
import org.geotools.data.Transaction;
import org.geotools.data.jdbc.datasource.ManageableDataSource;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureWriter;
import org.geotools.feature.SchemaException;
import org.geotools.geometry.jts.Geometries;
import org.geotools.geopkg.Features;
import org.geotools.geopkg.GeoPkgDataStoreFactory;
import org.geotools.geopkg.geom.GeoPkgGeomWriter;
import org.geotools.jdbc.JDBCDataStore;
import org.geotools.jdbc.JDBCDataStoreFactory;
import org.geotools.jdbc.PreparedStatementSQLDialect;
import org.geotools.jdbc.SQLDialect;
import org.geotools.referencing.CRS;
import org.geotools.util.logging.Logging;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.feature.type.PropertyType;
import org.opengis.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Provides access to a GeoPackage database.
 * 
 * @author Justin Deoliveira, OpenGeo
 * @author Niels Charlier
 */
public class DTGeoPackage {

	static final Logger LOGGER = Logging.getLogger("com.git.gdsbuilder.validator.basic.geopkg");

	File file;

	final DataSource connPool;

	volatile JDBCDataStore dataStore;

	private boolean initialised = false;

	protected GeoPkgGeomWriter.Configuration writerConfig = new GeoPkgGeomWriter.Configuration();

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public JDBCDataStore getDataStore() {
		return dataStore;
	}

	public void setDataStore(JDBCDataStore dataStore) {
		this.dataStore = dataStore;
	}

	public boolean isInitialised() {
		return initialised;
	}

	public void setInitialised(boolean initialised) {
		this.initialised = initialised;
	}

	public GeoPkgGeomWriter.Configuration getWriterConfig() {
		return writerConfig;
	}

	public void setWriterConfig(GeoPkgGeomWriter.Configuration writerConfig) {
		this.writerConfig = writerConfig;
	}

	public DataSource getConnPool() {
		return connPool;
	}

	public GeoPkgGeomWriter.Configuration getWriterConfiguration() {
		return writerConfig;
	}

	public DTGeoPackage() throws IOException {
		this(File.createTempFile("geopkg", ".db"));
	}

	/**
	 * Creates a GeoPackage from an existing file.
	 * <p>
	 * This constructor assumes no credentials are required to connect to the
	 * database.
	 * </p>
	 */
	public DTGeoPackage(File file) throws IOException {
		this(file, null, null);
	}

	/**
	 * Creates a GeoPackage from an existing file specifying database credentials.
	 */
	public DTGeoPackage(File file, String user, String passwd) throws IOException {
		this.file = file;

		Map params = new HashMap();
		if (user != null) {
			params.put(GeoPkgDataStoreFactory.USER.key, user);
		}
		if (passwd != null) {
			params.put(GeoPkgDataStoreFactory.PASSWD.key, passwd);
		}

		params.put(GeoPkgDataStoreFactory.DATABASE.key, file.getPath());
		params.put(GeoPkgDataStoreFactory.DBTYPE.key, GeoPkgDataStoreFactory.DBTYPE.sample);

		this.connPool = new GeoPkgDataStoreFactory(writerConfig).createDataSource(params);

	}

	DTGeoPackage(DataSource dataSource) {
		this.connPool = dataSource;
	}

	DTGeoPackage(JDBCDataStore dataStore) {
		this.dataStore = dataStore;
		this.connPool = dataStore.getDataSource();
	}

	public void add(DTFeatureEntry entry, SimpleFeatureCollection collection) throws IOException, SchemaException {

		DTFeatureEntry e = new DTFeatureEntry();
		e.init(entry);

		if (e.getBounds() == null) {
			e.setBounds(collection.getBounds());
		}

		SimpleFeatureType sft = collection.getSchema();
		String tmp = "";
		Collection<PropertyDescriptor> proDescs = sft.getDescriptors();
		for (PropertyDescriptor proDesc : proDescs) {
			String name = proDesc.getName().toString();
			PropertyType proType = proDesc.getType();
			String typeStr = proType.getBinding().getSimpleName();
			if (typeStr.equals("Date")) {
				typeStr = "java.sql.Timestamp";
			}
			if (tmp.equals("")) {
				tmp += name + ":" + typeStr;
			} else {
				tmp += "," + name + ":" + typeStr;
			}
		}

		SimpleFeatureType newType = DataUtilities.createType(collection.toString(), tmp);
		create(e, newType);

		Transaction tx = new DefaultTransaction();
		try {
			SimpleFeatureWriter w = writer(e, true, null, tx);
			SimpleFeatureIterator it = collection.features();
			try {
				while (it.hasNext()) {
					SimpleFeature f = it.next();
					SimpleFeature g = w.next();
					for (PropertyDescriptor pd : collection.getSchema().getDescriptors()) {
						String name = pd.getName().getLocalPart();
						Object value = f.getAttribute(name);
						if (value != null) {
							if (value.getClass().getSimpleName().equals("Date")) {
								Date date = (Date) value;
								Timestamp timeStamp = new Timestamp(date.getTime());
								g.setAttribute(name, timeStamp);
							} else {
								g.setAttribute(name, value);
							}
						} else {
							g.setAttribute(name, null);
						}
//						g.setAttribute(name, value);
					}
					w.write();
				}
			} finally {
				w.close();
				it.close();
			}
			tx.commit();
		} catch (Exception ex) {
			tx.rollback();
			throw new IOException(ex);
		} finally {
			tx.close();
		}
		entry.init(e);
	}

	public SimpleFeatureWriter writer(DTFeatureEntry entry, boolean append, Filter filter, Transaction tx)
			throws IOException {

		DataStore dataStore = dataStore();
		FeatureWriter w = append ? dataStore.getFeatureWriterAppend(entry.getTableName(), tx)
				: dataStore.getFeatureWriter(entry.getTableName(), filter, tx);

		return Features.simple(w);
	}

	private void create(DTFeatureEntry entry, SimpleFeatureType schema) throws IOException {

		// clone entry so we can work on it
		DTFeatureEntry e = new DTFeatureEntry();
		e.init(entry);
		e.setTableName(schema.getTypeName());

		if (e.getGeometryColumn() != null) {
			// check it
			if (schema.getDescriptor(e.getGeometryColumn()) == null) {
				throw new IllegalArgumentException(
						format("Geometry column %s does not exist in schema", e.getGeometryColumn()));
			}
		} else {
			e.setGeometryColumn(findGeometryColumn(schema));
		}

		if (e.getIdentifier() == null) {
			e.setIdentifier(schema.getTypeName());
		}
		if (e.getDescription() == null) {
			e.setDescription(e.getIdentifier());
		}

		// check for bounds
		if (e.getBounds() == null) {
			throw new IllegalArgumentException("Entry must have bounds");
		}

		// check for srid
		if (e.getSrid() == null) {
			try {
				e.setSrid(findSRID(schema));
			} catch (Exception ex) {
				throw new IllegalArgumentException(ex);
			}
		}
		if (e.getSrid() == null) {
			throw new IllegalArgumentException("Entry must have srid");
		}

		if (e.getGeometryType() == null) {
			e.setGeometryType(findGeometryType(schema));
		}
		e.setLastChange(new Date());
		schema.getUserData().put(DTFeatureEntry.class, e);
		JDBCDataStore dataStore = dataStore();
		dataStore.createSchema(schema);
		// dataStore.setSQLDialect();
		entry.init(e);

	}

	static String findGeometryColumn(SimpleFeatureType schema) {
		GeometryDescriptor gd = findGeometryDescriptor(schema);
		return gd != null ? gd.getLocalName() : null;
	}

	static Integer findSRID(SimpleFeatureType schema) throws Exception {
		CoordinateReferenceSystem crs = schema.getCoordinateReferenceSystem();
		if (crs == null) {
			GeometryDescriptor gd = findGeometryDescriptor(schema);
			crs = gd.getCoordinateReferenceSystem();
		}

		return crs != null ? CRS.lookupEpsgCode(crs, true) : null;
	}

	static GeometryDescriptor findGeometryDescriptor(SimpleFeatureType schema) {
		GeometryDescriptor gd = schema.getGeometryDescriptor();
		if (gd == null) {
			for (PropertyDescriptor pd : schema.getDescriptors()) {
				if (pd instanceof GeometryDescriptor) {
					return (GeometryDescriptor) pd;
				}
			}
		}
		return gd;
	}

	static Geometries findGeometryType(SimpleFeatureType schema) {
		GeometryDescriptor gd = findGeometryDescriptor(schema);
		return gd != null ? Geometries.getForBinding((Class<? extends Geometry>) gd.getType().getBinding()) : null;
	}

	JDBCDataStore dataStore() throws IOException {
		if (dataStore == null) {
			synchronized (this) {
				if (dataStore == null) {
					dataStore = createDataStore();
				}
			}
		}
		return dataStore;
	}

	JDBCDataStore createDataStore() throws IOException {
		Map<String, Object> params = new HashMap<>();
		params.put(GeoPkgDataStoreFactory.DATASOURCE.key, connPool);
		return new GeoPkgDataStoreFactory(writerConfig).createDataStore(params);
	}

	public void close() {
		if (dataStore != null) {
			dataStore.dispose();
		}

		try {
			if (connPool instanceof BasicDataSource) {
				((BasicDataSource) connPool).close();
			} else if (connPool instanceof ManageableDataSource) {
				((ManageableDataSource) connPool).close();
			}

		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Error closing database connection", e);
		}
	}
}
