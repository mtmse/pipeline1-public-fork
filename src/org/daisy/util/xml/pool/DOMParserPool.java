/*
 * org.daisy.util (C) 2005-2008 Daisy Consortium
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.daisy.util.xml.pool;

import java.util.Iterator;
import java.util.Map;

import org.apache.xerces.parsers.DOMParser;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

/**
 * A singleton DOMParser pool. Used for performance optimization.
 * <p>Do not use this class if you cannot guarantee the availability of 
 * <code>org.apache.xerces.parsers.DOMParser</code> on your classpath; 
 * this implementation does not use the JAXP abstractfactory API.</p>
 * @author Markus Gylling
 */
public class DOMParserPool extends AbstractPool {
	protected static DOMParserPool mInstance = new DOMParserPool();	
		
	static public DOMParserPool getInstance() {
		return mInstance;
	}
	
	private DOMParserPool(){
		super();
	}
	
	/**
	 * Retrieve a DOMParser from the pool, configurable via SAX properties. Do not use this class
	 * if you cannot guarantee the availability of <code>org.apache.xerces.parsers.DOMParser</code>
	 * on your classpath.
	 * <p>If the features and properties maps are null, a namespaceaware nonvalidating parser will be returned.</p>
	 * <p>Parser instances retrieved through the acquire() method are returned to pool using the release() method.</p>
	 * 
	 * <p>For the official SAX features and properties list, see
	 * http://www.saxproject.org/apidoc/org/xml/sax/package-summary.html#package_description</p>
	 * 
	 * <p>For Xerces specific properties, see
	 * http://xerces.apache.org/xerces2-j/properties.html</p>
	 * 
	 * <p>For Xerces specific features, see 
	 * http://xerces.apache.org/xerces2-j/features.html</p>
	 */
	public DOMParser acquire(Map<String,Object> saxFeatures, Map<String,Object> saxProperties) throws PoolException {
		try {
			Object o = getProcessorFromCache(saxFeatures, saxProperties);
			if(o!=null) {
				return (DOMParser)o;
			}
			return createDomParser(saxFeatures, saxProperties);		
			
		} catch (Exception e) {
			throw new PoolException(e.getMessage(),e);
		}		
	}

	/**
	 * Return the parser back to the pool
	 * @throws PoolException 
	 */
	public void release(DOMParser parser, Map<String,Object> features, Map<String,Object> properties) throws PoolException {	
		try {
			//reset all handlers
			parser.setDocumentSource(null);			
			parser.setDTDSource(null);
			parser.setEntityResolver(null);
			parser.setErrorHandler(null);			
			//call reset, repop and release
			parser.reset();		
			super.release(setFeaturesAndProperties(parser,features, properties), features, properties);
		} catch (Exception e) {
			throw new PoolException(e.getMessage(),e);			
		} 
	}
	
	private DOMParser createDomParser(Map<String,Object> features, Map<String,Object> properties) throws ClassCastException, SAXNotRecognizedException, SAXNotSupportedException {		
		DOMParser parser = new DOMParser();				    
	    return setFeaturesAndProperties(parser,features, properties);		
	}
	
	private DOMParser setFeaturesAndProperties(DOMParser parser, Map<String,Object> features, Map<String,Object> properties) throws SAXNotRecognizedException, SAXNotSupportedException {
	    Iterator<String> i;
	    if (features != null){
	      for (i = features.keySet().iterator(); i.hasNext();){
	        String feature = i.next();
	        parser.setFeature(feature, ((Boolean)features.get(feature)).booleanValue());
	      }
	    }
	    if (properties != null){
	      for (i = properties.keySet().iterator(); i.hasNext();){
	        String property = i.next();
	        parser.setProperty(property, properties.get(property));
	      }
	    }
	    return parser;
	}
}
