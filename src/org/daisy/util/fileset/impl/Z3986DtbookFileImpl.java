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
package org.daisy.util.fileset.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.daisy.util.fileset.ManifestFile;
import org.daisy.util.fileset.UIDCarrier;
import org.daisy.util.fileset.Z3986DtbookFile;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Markus Gylling
 */
final class Z3986DtbookFileImpl extends XmlFileImpl implements Z3986DtbookFile, ManifestFile, UIDCarrier {
	private String dcTitle = null;
	private String dcIdentifier = null;
	private String dtbUid = null;
	private String doctitle = null;
	private Set<String> docauthors = new HashSet<String>();
	private Set<String> dcCreators = new HashSet<String>();
	private String dcPublisher= null;
	private Set<String> dcLanguages= new HashSet<String>(); //repeatable
	private String mRootVersion = null;
	
	private boolean inDoctitle = false;
	private boolean inDocauthor = false;
	
	private String charCollector = "";
	
	Z3986DtbookFileImpl(URI uri) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException {
		super(uri,Z3986DtbookFile.mimeStringConstant);		
	}
	
	//private int count = 0;
	
	public void startElement (String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException {	
		super.startElement(namespaceURI, sName, qName, attrs);
		
		if (sName=="meta") {
			String name = attrs.getValue("name");
			if (name != null){
				if (name.toLowerCase().equals("dc:title")) {				
					this.dcTitle = attrs.getValue("content");
				}else if(name.toLowerCase().equals("dc:identifier")){
					this.dcIdentifier = attrs.getValue("content");
				}else if(name.toLowerCase().equals("dc:creator")){
						this.dcCreators.add(attrs.getValue("content"));	
				}else if(name.toLowerCase().equals("dc:publisher")){
					this.dcPublisher = attrs.getValue("content");		
				}else if(name.toLowerCase().equals("dtb:uid")){
					this.dtbUid = attrs.getValue("content");
				}else if(name.toLowerCase().equals("dc:language")){
					this.dcLanguages.add(attrs.getValue("content"));
				}		
			}
		}else if (sName == "doctitle") {
			inDoctitle = true;
		}else if (sName == "docauthor") {
			inDocauthor = true;
		}else if (sName == "dtbook") {
			String version = attrs.getValue("version");
			if(version != null) {
			  mRootVersion = version;	
			}
		}
		
		for (int i = 0; i < attrs.getLength(); i++) {
			attrName = attrs.getQName(i);
			attrValue = attrs.getValue(i).intern();
			
			if (attrName=="id") {				
				this.putIdAndQName(attrValue,new QName(namespaceURI,sName));				
			} else if (regex.matches(regex.DTBOOK_ATTRIBUTES_WITH_URIS,attrName)
					||regex.matches(regex.DTBOOK_COMPOUND_ATTRIBUTES_WITH_URIS,attrName)) {
			   putUriValue(attrValue);
			}else if (attrName=="xml:lang") {
				this.mXmlLangValues.add(attrValue);
			}
		}//for (int i
	}//startElement

	@SuppressWarnings("unused")
	public void endElement(String uri, String sName, String qName) throws SAXException {
		if (sName == "doctitle") {			
			this.doctitle = charCollector;
			charCollector="";
			inDoctitle = false;
		}else if (sName == "docauthor") {
			this.docauthors.add(charCollector);
			charCollector="";
			inDocauthor = false;
		}
	}

	@SuppressWarnings("unused")
	public void characters(char[] chars, int start, int end) throws SAXException {
		if(inDoctitle||inDocauthor) {
			charCollector += String.copyValueOf(chars,start,end);
		}
	}

	public String getRootVersion() {
		return mRootVersion;
	}
	
	public String getDcIdentifier() {
		return dcIdentifier;
	}

	public String getUID() {		
		return getDcIdentifier();
	}
	
	public String getDcTitle() {
		return dcTitle;
	}

	public String getDcCreator() {
		if (!dcCreators.isEmpty()) {
			return dcCreators.iterator().next();
		}
		return null;
	}
	
	public Collection<String> getDcCreators() {
		return dcCreators;
	}
	
	public String getDcPublisher() {
		return dcPublisher;
	}
	
	public String getDocauthor() {
		if (!docauthors.isEmpty()) {
			return docauthors.iterator().next();
		}
		return null;
	}
	
	public Collection<String> getDocauthors() {
		return docauthors;
	}

	public String getDoctitle() {
		return doctitle;
	}

	public String getDtbUid() {
		return dtbUid;
	}
	
	public Collection<String> getDcLanguages() {		
		return this.dcLanguages;
	}
	
	private static final long serialVersionUID = -4975394410229229129L;

}