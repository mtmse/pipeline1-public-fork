package se_tpb_zed2daisy202;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.daisy.util.file.TempFile;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.stax.StaxEntityResolver;

/**
 * Extract the information needed by the smil2smil stylesheet.
 * @author Linus Ericson
 */
class PreCalc {

    private XMLInputFactory factory = null;
    private XMLOutputFactory of = null;
    private XMLEventFactory ef = null;
    
    private XMLEventReader xer = null;    
    private XMLEventWriter xew = null;
    
    private Map preCalcHeadings = new HashMap();
    private Map preCalcNotes = new HashMap();
    private String doctitleId = null;
    
    private String ncxTitle = null;
    private String ncxClipBegin = null;
    private String ncxClipEnd = null;
    private String ncxClipSrc = null;
    private Map smilCustomTests = new HashMap();
    
    private File dtbookFile = null;
    private File ncxFile = null;
    
    private int fakeId = 1;
    
    private class PreCalcHeading {
        private StringBuffer heading = new StringBuffer();
        private Collection ids = new ArrayList();
        public void append(String str) {
            heading.append(str);
        }
        public void addId(String id) {
            ids.add(id);
        }
        public String getHeading() {
            return heading.toString();
        }
        public Collection getIds() {
            return ids;
        }
    }
    
    
    public PreCalc(File dtbook, File ncx) throws CatalogExceptionNotRecoverable, FileNotFoundException, XMLStreamException {
        factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
        factory.setProperty(XMLInputFactory.RESOLVER, new StaxEntityResolver(CatalogEntityResolver.getInstance()));
        
        of = XMLOutputFactory.newInstance();
        ef = XMLEventFactory.newInstance();
        
        dtbookFile = dtbook;
        ncxFile = ncx;
        
        // Extract from DTBook
        buildPreCalcMap();
        
        // Extract from NCX
        buildNcxInfo();
    }
    
    public File getPreCalcFile() throws IOException, XMLStreamException {
        File outfile = TempFile.create();
        xew = of.createXMLEventWriter(new FileOutputStream(outfile), "utf-8");
        
        // Start document
        xew.add(ef.createStartDocument());
        
        // Root element start
        Collection coll = new ArrayList();
        if (doctitleId != null) {
            coll.add(ef.createAttribute("id", doctitleId));
        }
        xew.add(ef.createStartElement("", null, "doc", coll.iterator(), null));
        
        // Headings
        xew.add(ef.createStartElement("", null, "headings"));
        for (Iterator it = preCalcHeadings.keySet().iterator(); it.hasNext(); ) {
            String key = (String)it.next();    
            PreCalcHeading pch = (PreCalcHeading)preCalcHeadings.get(key);
            Attribute attId = ef.createAttribute("id", key);
            Attribute attTitle = ef.createAttribute("title", pch.getHeading());
            coll = new ArrayList();
            coll.add(attId);
            coll.add(attTitle);
            xew.add(ef.createStartElement("", null, "heading", coll.iterator(), null));
            for (Iterator it2 = pch.getIds().iterator(); it2.hasNext(); ) {
                String id = (String)it2.next();
                coll = new ArrayList();
                coll.add(ef.createAttribute("id", id));
                xew.add(ef.createStartElement("", null, "item", coll.iterator(), null));
                xew.add(ef.createEndElement("", null, "item"));
            }
            xew.add(ef.createEndElement("", null, "heading"));
        }
        xew.add(ef.createEndElement("", null, "headings"));
        
        // Notes
        xew.add(ef.createStartElement("", null, "notes"));
        for (Iterator it = preCalcNotes.keySet().iterator(); it.hasNext(); ) {
            String key = (String)it.next();    
            Collection noteIds = (Collection)preCalcNotes.get(key);
            Attribute attId = ef.createAttribute("id", key);
            coll = new ArrayList();
            coll.add(attId);
            xew.add(ef.createStartElement("", null, "note", coll.iterator(), null));
            for (Iterator it2 = noteIds.iterator(); it2.hasNext(); ) {
                String id = (String)it2.next();
                coll = new ArrayList();
                coll.add(ef.createAttribute("id", id));
                xew.add(ef.createStartElement("", null, "item", coll.iterator(), null));
                xew.add(ef.createEndElement("", null, "item"));
            }
            xew.add(ef.createEndElement("", null, "note"));
        }
        xew.add(ef.createEndElement("", null, "notes"));
        
        // NCX stuff
        coll = new ArrayList();
        coll.add(ef.createAttribute("src", ncxClipSrc));
        coll.add(ef.createAttribute("clipBegin", ncxClipBegin));
        coll.add(ef.createAttribute("clipEnd", ncxClipEnd));
        coll.add(ef.createAttribute("title", ncxTitle));
        xew.add(ef.createStartElement("", null, "ncx", coll.iterator(), null));
        for (Iterator it = smilCustomTests.keySet().iterator(); it.hasNext(); ) {
            String id = (String)it.next();
            String bookStruct = (String)smilCustomTests.get(id);
            coll = new ArrayList();
            coll.add(ef.createAttribute("id", id));
            coll.add(ef.createAttribute("bookStruct", bookStruct));
            xew.add(ef.createStartElement("", null, "customTest", coll.iterator(), null));
            xew.add(ef.createEndElement("", null, "customTest"));
        }
        xew.add(ef.createEndElement("", null, "ncx"));
        
        // Root element end 
        xew.add(ef.createEndElement("", null, "doc"));
        xew.add(ef.createEndDocument());
        xew.close();
        return outfile;
    }
    
    private void buildNcxInfo() throws FileNotFoundException, XMLStreamException {
        boolean inDocTitle = false;
        boolean inText = false;
        boolean stop = false;
        xer = factory.createXMLEventReader(new FileInputStream(ncxFile));
        while (xer.hasNext()) {
            XMLEvent ev = xer.nextEvent();
            
            if (ev.isStartElement()) {
                StartElement se = ev.asStartElement();
                if ("smilCustomTest".equals(se.getName().getLocalPart())) {
                    Attribute attId = se.getAttributeByName(new QName("id"));
                    Attribute attStruct = se.getAttributeByName(new QName("bookStruct"));
                    if (attId != null && attStruct != null) {
                        smilCustomTests.put(attId.getValue(), attStruct.getValue());
                    }
                } else if ("docTitle".equals(se.getName().getLocalPart())) {
                    inDocTitle = true;
                } else if ("text".equals(se.getName().getLocalPart()) && inDocTitle) {
                    inText = true;
                } else if ("audio".equals(se.getName().getLocalPart()) && inDocTitle) {
                    Attribute attBegin = se.getAttributeByName(new QName("clipBegin"));
                    if (attBegin != null) {
                        ncxClipBegin = attBegin.getValue();
                    }
                    Attribute attEnd = se.getAttributeByName(new QName("clipEnd"));
                    if (attEnd != null) {
                        ncxClipEnd = attEnd.getValue();
                    }
                    Attribute attSrc = se.getAttributeByName(new QName("src"));
                    if (attSrc != null) {
                        ncxClipSrc = attSrc.getValue();
                    }
                } 
            } else if (ev.isEndElement()) {
                EndElement ee = ev.asEndElement();
                if ("docTitle".equals(ee.getName().getLocalPart())) {
                    inDocTitle = false;
                }
            } else if (ev.isCharacters()) {
                if (inText && !stop) {
                    ncxTitle = ev.asCharacters().getData();
                    stop = true;
                }
            }
        }
    }
    
    private void buildPreCalcMap() throws XMLStreamException, FileNotFoundException {
        boolean inHeading = false;
        boolean inNote = false;
        String currentId = null;
        PreCalcHeading pch = null;
        Collection noteIds = null;
        xer = factory.createXMLEventReader(new FileInputStream(dtbookFile));
        while (xer.hasNext()) {
            XMLEvent ev = xer.nextEvent();
            
            if (ev.isStartElement()) {
                StartElement se = ev.asStartElement();
                if (isHeading(se.getName())) {
                    currentId = this.getId(se);
                    if (currentId == null) {
                        currentId = String.valueOf(fakeId++);
                    }
                    pch = new PreCalcHeading();
                    //pch.addId(currentId);
                    preCalcHeadings.put(currentId, pch);
                    inHeading = true;
                }
                if (isNote(se.getName())) {
                    currentId = this.getId(se);
                    noteIds = new ArrayList();
                    //noteIds.add(currentId);
                    preCalcNotes.put(currentId, noteIds);
                    inNote = true;
                }
                if ("doctitle".equals(se.getName().getLocalPart()) && doctitleId == null) {
                    Attribute att = se.getAttributeByName(new QName("id"));
                    if (att != null) {
                        doctitleId = att.getValue();
                    }
                }
                if (inHeading) {
                    String id = this.getId(se);
                    if (id != null) {
                        pch.addId(id);
                    }
                }
                if (inNote) {
                    String id = this.getId(se);
                    if (id != null) {
                        noteIds.add(id);
                    }
                }
            } else if (ev.isEndElement()) {
                EndElement ee = ev.asEndElement();
                if (isHeading(ee.getName())) {
                    inHeading = false;
                }
                if (isNote(ee.getName())) {
                    inNote = false;
                }
            } else if (ev.isCharacters()) {
                if (inHeading) {
                    pch.append(ev.asCharacters().getData());
                }
            }
        }
        System.err.println("done!");
    }
    
    private String getId(StartElement se) {
        String id = null;
        Attribute att = se.getAttributeByName(new QName("id"));
        if (att != null) {
            id = att.getValue();
        }
        return id;
    }
    
    public boolean isHeading(QName name) {
        if (name.getLocalPart().equals("h1")) {
            return true;
        } else if (name.getLocalPart().equals("h2")) {
            return true;
        } else if (name.getLocalPart().equals("h3")) {
            return true;
        } else if (name.getLocalPart().equals("h4")) {
            return true;
        } else if (name.getLocalPart().equals("h5")) {
            return true;
        } else if (name.getLocalPart().equals("h6")) {
            return true;
        } else if (name.getLocalPart().equals("hd")) {
            return true;
        } 
        return false;
    }
    
    public boolean isNote(QName name) {
        if (name.getLocalPart().equals("note")) {
            return true;
        }
        return false;
    }
    
}
