package org.daisy.util.xml.validation.jaxp;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.validation.ValidatorHandler;

import org.daisy.util.exception.ExceptionTransformer;
import org.daisy.util.file.TempFile;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.xslt.Stylesheet;
import org.daisy.util.xml.xslt.XSLTException;
import org.daisy.util.xml.xslt.stylesheets.Stylesheets;
import org.xml.sax.SAXException;

public class SchematronSchema extends AbstractSchema implements ErrorListener {
	TransformerFactory transformerFactory = null;
	javax.xml.transform.Transformer transformer = null;
	
	SchematronSchema(URL schema, SchemaFactory originator) throws 	IOException, TransformerConfigurationException, 
																	XSLTException, CatalogExceptionNotRecoverable {
		super(schema,originator);
		//clean to a single namespace doc and replace the super attributes with result
		StreamSource ss = new StreamSource(super.schemaURL.openStream(),super.schemaURL.toExternalForm());		
		super.sources = transform(new Source[]{ss});
		super.schemaURL = null;
	}
	
	SchematronSchema(Source[] sources, SchemaFactory originator) throws IOException, TransformerConfigurationException, 
																		XSLTException, CatalogExceptionNotRecoverable {
		super(sources,originator);
		//clean to a single namespace doc and replace the super attributes with result
		super.sources = transform(super.sources);
	}
	
	public Validator newValidator() {
		SchematronValidator validator = new SchematronValidator(this);
		validator.propagateHandlers(this.originator);	
		validator.initialize();
		return validator;
	}

	public ValidatorHandler newValidatorHandler() {
		return null;
	}
	
	/**
	 * Strips possible foreign namespaces from input using XSLT.
	 * @throws IOException 
	 * @throws TransformerConfigurationException 
	 * @throws XSLTException 
	 * @throws CatalogExceptionNotRecoverable 
	 */
	private Source[] transform(Source[] inSources) throws TransformerConfigurationException, IOException, 
														XSLTException, CatalogExceptionNotRecoverable {
		if(null==transformerFactory) {
			transformerFactory = TransformerFactory.newInstance();
			transformerFactory.setErrorListener(this);
			//TODO fix below method in CatalogEntityResolver by incorporating linus code as fallback
			transformerFactory.setURIResolver(CatalogEntityResolver.getInstance());
			transformer = transformerFactory.newTransformer
				(new StreamSource(Stylesheets.get("RNG2Schtrn.xsl").openStream()));			
		}
				
		Source[] outSources = new Source[inSources.length];
		for (int i = 0; i < inSources.length; i++) {
			File fileResult = TempFile.create();
			StreamResult streamResult = new StreamResult(fileResult);
			Stylesheet.apply(inSources[i],transformer,streamResult,null);
			outSources[i] = new StreamSource(fileResult);			 
		}
		return outSources;
	}

	
	public void warning(TransformerException exception) throws TransformerException {
		if(null!=this.originator.getErrorHandler()) {
			try {
				this.originator.getErrorHandler().warning(ExceptionTransformer.newSAXParseException(exception));
				return;
			} catch (SAXException e) {
				System.err.println(e.getMessage());
			}
		}
		System.err.println(exception.getMessage());
	}

	public void error(TransformerException exception) throws TransformerException {
		if(null!=this.originator.getErrorHandler()) {
			try {
				this.originator.getErrorHandler().error(ExceptionTransformer.newSAXParseException(exception));
				return;
			} catch (SAXException e) {
				System.err.println(e.getMessage());
			}
		}		
		System.err.println(exception.getMessage());
	}

	public void fatalError(TransformerException exception) throws TransformerException {
		if(null!=this.originator.getErrorHandler()) {
			try {
				this.originator.getErrorHandler().fatalError(ExceptionTransformer.newSAXParseException(exception));
				return;
			} catch (SAXException e) {
				System.err.println(e.getMessage());
			}
		}		
		System.err.println(exception.getMessage());
	}

}