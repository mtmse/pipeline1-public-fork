package int_daisy_filesetRenamer;

import int_daisy_filesetRenamer.segment.EchoSegment;
import int_daisy_filesetRenamer.segment.FilesetUIDSegment;
import int_daisy_filesetRenamer.segment.LabelSegment;
import int_daisy_filesetRenamer.segment.FixedSegment;
import int_daisy_filesetRenamer.segment.RandomUniqueSegment;
import int_daisy_filesetRenamer.segment.SegmentedFileName;
import int_daisy_filesetRenamer.segment.SequenceSegment;
import int_daisy_filesetRenamer.strategies.DefaultStrategy;
import int_daisy_filesetRenamer.strategies.RenamingStrategy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import javax.xml.stream.events.XMLEvent;

import org.daisy.dmfc.core.InputListener;
import org.daisy.dmfc.core.transformer.Transformer;
import org.daisy.dmfc.exception.TransformerRunException;
import org.daisy.util.file.EFile;
import org.daisy.util.file.EFolder;
import org.daisy.util.file.FileUtils;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.fileset.exception.FilesetFileException;
import org.daisy.util.fileset.exception.FilesetFileFatalErrorException;
import org.daisy.util.fileset.impl.FilesetImpl;
import org.daisy.util.fileset.interfaces.Fileset;
import org.daisy.util.fileset.interfaces.FilesetFile;
import org.daisy.util.fileset.interfaces.Referring;
import org.daisy.util.fileset.interfaces.xml.SmilFile;
import org.daisy.util.fileset.interfaces.xml.Xhtml10File;
import org.daisy.util.fileset.interfaces.xml.XmlFile;
import org.daisy.util.fileset.interfaces.xml.z3986.Z3986DtbookFile;
import org.daisy.util.fileset.manipulation.FilesetFileManipulator;
import org.daisy.util.fileset.manipulation.FilesetManipulationException;
import org.daisy.util.fileset.manipulation.FilesetManipulator;
import org.daisy.util.fileset.manipulation.FilesetManipulatorListener;
import org.daisy.util.fileset.manipulation.manipulators.RenamingCopier;
import org.daisy.util.fileset.manipulation.manipulators.XMLEventValueConsumer;
import org.daisy.util.fileset.manipulation.manipulators.XMLEventValueExposer;
import org.daisy.util.fileset.util.FilesetRegex;
import org.daisy.util.fileset.util.URIStringParser;
import org.daisy.util.xml.stax.ContextStack;

/**
 * Renames select members of a fileset using a combined-token algorithm.
 * @author Markus Gylling
 */
public class FilesetRenamer extends Transformer implements FilesetManipulatorListener, XMLEventValueConsumer {
   
	private FilesetManipulator mFilesetManipulator = null;
	private EFile mInputManifest = null;
	private Fileset mInputFileset = null;
	private EFolder mOutputDir = null;				//final output destination
	private EFolder mRoundtripOutputDir = null; 	//for temporary storage, not always used
	private SegmentedFileName mTemplateName = null;
	private RenamingStrategy mStrategy = null;
	private List mTypeExclusions = null;
	private boolean mFilesystemSafe = true;
	private FilesetFile currentFile = null;
	private String oldName = null;	
	private FilesetRegex rgx = null;
			
	public FilesetRenamer(InputListener inListener, Set eventListeners, Boolean isInteractive) {
		super(inListener, eventListeners, isInteractive);
		rgx = FilesetRegex.getInstance();
	}

	protected boolean execute(Map parameters) throws TransformerRunException {
		/*
		 * First, create the renaming strategy using the input tokens.
		 * Validate this strategy. 
		 * If valid,
		 *   render, return true.
		 * If invalid and fixable (ie not a multiple new identical names error),
		 *   do a first pass of scrambling renaming to a subfolder of out
		 *   and then rerun the input strategy
		 * Validate this strategy, 
		 * If valid
		 *   render, return true.
		 * If invalid,
		 *   render unrenamed fileset (unless in and out are the same)
		 *   send error, return true.    
		 */
		
		FilesetManipulator fman = null;
		try {  
			//set the input manifest
			mInputManifest = new EFile(FilenameOrFileURI.toFile((String)parameters.remove("input")));
			//set input fileset
			mInputFileset = new FilesetImpl(mInputManifest.toURI(),this,false,false);			
			//parse the renamingPattern param, create a template filename
			//to use while creating the new names
			mTemplateName = parsePatternTokens((String)parameters.remove("renamingPattern"));
			//create the type exclusion list
			mTypeExclusions = setExclusions((String)parameters.remove("exclude"));
			//whether to force ascii subset in output
			mFilesystemSafe = Boolean.parseBoolean((String)parameters.remove("filesystemSafe"));
			//set/create output dir
			mOutputDir = (EFolder)FileUtils.createDirectory(new EFolder(FilenameOrFileURI.toFile((String)parameters.remove("output"))));
			//if input and output dir are the same, skip and return true
			if(mOutputDir.getCanonicalPath().equals(mInputFileset.getManifestMember().getFile().getParentFile().getCanonicalPath())) {
				this.sendMessage(Level.SEVERE, i18n("IN_OUT_SAME_SKIPPING", mOutputDir.getCanonicalPath()));
				return true;
			}
					
			try{		
				//create a renaming strategy using the template name
				mStrategy = createStrategy(mInputFileset, mTemplateName, mTypeExclusions);
			}catch (FilesetRenamingException e) {
				//do a roundtrip
				//TODO analyze the exception to find out if a roundtrip helps at all
				//TODO means improving the validate method
				
				//create a new strategy with heavy random
				SegmentedFileName randomizedName = new SegmentedFileName();
				randomizedName.addSegment(FixedSegment.create("temp"));
				randomizedName.addSegment(RandomUniqueSegment.create(mInputFileset, 6));				
				mStrategy = createStrategy(mInputFileset, randomizedName, mTypeExclusions);				
				//render the randomized fileset to a subfolder of user output folder
				mRoundtripOutputDir = (EFolder)FileUtils.createDirectory(new EFolder(new File(mOutputDir,"dmfc_temp")));
				this.sendMessage(Level.WARNING, i18n("RENDER_ROUNDTRIP", e.getMessage(), mRoundtripOutputDir.getAbsolutePath()));
				renderStrategy(mInputFileset,mRoundtripOutputDir);				

				//reset the inputfileset to the randomized output
				mInputFileset = new FilesetImpl(getTempManifest(),this,false,false); 
				//redo strategy with original template, using the randomized output				
				mStrategy = createStrategy(mInputFileset, mTemplateName, mTypeExclusions);
			}
			
			//render the final output
			renderStrategy(mInputFileset,mOutputDir);
			
			//clean up the temp traces if utilized
			if(mRoundtripOutputDir!=null) {
				mRoundtripOutputDir.deleteContents();
				mRoundtripOutputDir.delete();
			}
		
		} catch (Exception e) {			
			this.sendMessage(Level.SEVERE, i18n("ERROR_COPYING_UNRENAMED", e.getMessage()));
			try {		
				fman.getOutputFolder().addFileset(fman.getInputFileset(),true);
			} catch (IOException ioe) {
				this.sendMessage(Level.SEVERE, i18n("ERROR_ABORTING", ioe.getMessage()));
				throw new TransformerRunException(ioe.getMessage(), ioe);
			}
		}	

		return true;
	}

	/**
	 * @throws FilesetRenamingException if resulting strategy is not valid or something else went wrong.
	 */
	private RenamingStrategy createStrategy(Fileset fileset, SegmentedFileName templateName, List typeExclusions) throws FilesetRenamingException {
		RenamingStrategy rs = new DefaultStrategy(fileset,templateName,mFilesystemSafe);
		rs.setTypeExclusion(typeExclusions);
		rs.create();
		rs.validate();
		return rs;
	}

	/**
	 * Render a fileset to disk. Register self as listener, 
	 * and use the callbacks to intervene and apply the rename strategy to the output.
	 */
	private void renderStrategy(Fileset fileset, EFolder outputDir) throws FilesetManipulationException, IOException {		
		//get a FilesetManipulator instance
		mFilesetManipulator = new FilesetManipulator();
		//implement FilesetManipulatorListener
		mFilesetManipulator.setListener(this);
		//set input fileset
		mFilesetManipulator.setInputFileset(fileset);
		//set destination
		mFilesetManipulator.setOutputFolder(outputDir); 
		//roll through the fileset			
		mFilesetManipulator.iterate();
		//done.
	}
	
	/**
	 * Create a List&lt;Class&gt; of excluded file types using an inparam.
	 */
	private List setExclusions(String param) {
		mTypeExclusions = new ArrayList();
		String[] types = param.split(",");
		for (int i = 0; i < types.length; i++) {
			String interfaceName = types[i].trim();
			//create the class instance
			if(!interfaceName.toLowerCase().equals("none")){
				String implName = "org.daisy.util.fileset.impl." + interfaceName + "Impl";					
				try {
					Class implClass = Class.forName(implName);
					mTypeExclusions.add(implClass);
				} catch (ClassNotFoundException e) {
					this.sendMessage(Level.WARNING, i18n("EXCLUDE_CLASS_NOT_FOUND", interfaceName));
				}			
			}
		}
		return mTypeExclusions;		
	}

	private URI getTempManifest() throws TransformerRunException {
		String newName = mStrategy.getNewLocalName(mInputFileset.getManifestMember().getFile().toURI());
		File newManifest = new File(mRoundtripOutputDir, newName);
		if(newManifest!=null && newManifest.exists()){
			return newManifest.toURI();
		}
		throw new TransformerRunException("temporary manifest could not be found");
	}



	/**
	 * Parse a string consisting of plus-separated tokens and create a template SegmentedFileName.
	 * <p>In the template, give each segment the value of the input token, so that later users can
	 * extract possible additional info from the tokens.</p>
	 * <p>Note - template does not include the extension segment.</p>
	 */
	private SegmentedFileName parsePatternTokens(String tokenstring) throws TransformerRunException {		
		SegmentedFileName smf = new SegmentedFileName();		
		String[] tokens = tokenstring.split("\\+");
		for (int i = 0; i < tokens.length; i++) {
			String token = tokens[i].trim();
			if(token.equals("uid")) {
				smf.addSegment(FilesetUIDSegment.create(token));				
			}else if(token.startsWith("rnd")){
				smf.addSegment(RandomUniqueSegment.create(token));			
			}else if(token.startsWith("fixed")){
				smf.addSegment(FixedSegment.create(token));			
			}else if(token.equals("label")){
				smf.addSegment(LabelSegment.create(token));			
			}else if(token.equals("seq")){
				smf.addSegment(SequenceSegment.create(token));
			}else if(token.equals("echo")){
				smf.addSegment(EchoSegment.create(token));				
			}
			else {
				//an unrecognized segment
				throw new TransformerRunException("unrecognized segment: " + token);
			}
		}
		return smf;
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.manipulation.FilesetManipulatorListener#nextFile(org.daisy.util.fileset.interfaces.FilesetFile)
	 */
	public FilesetFileManipulator nextFile(FilesetFile file) throws FilesetManipulationException {
		currentFile = file; //for checking filetype in nextValue() below
		try{
			if (file instanceof Referring) {
				//this file may have a new name
				//and may refer to other members that may have new names
				if(file instanceof XmlFile) {
					//use the constructor of xmleventfeeder that allows localname change				
					XMLEventValueExposer xeve = new XMLEventValueExposer(this,flatten(mStrategy.getNewLocalName(file)));					
					//default is to only replace in attributes (they typically carry URIs)
					xeve.setEventTypeRestriction(XMLEvent.ATTRIBUTE);				
					return xeve;
				}
				//FIXME if not xmlfile but still referring (css,html)
			}
			//else, this file does not refer to other members
			//but may still have a new name
			return new RenamingCopier(flatten(mStrategy.getNewLocalName(file)));			
		}catch (Exception e) {
			throw new FilesetManipulationException(e.getMessage(),e);
		}
	}

		
	/**
	 * Tries to assure that the name contains only ascii characters [A-Za-z0-9_-]
	 */
	private String flatten(String newLocalName) {
		//TODO implement
		//TODO make defeatable via inparam
		return newLocalName;
	}

	/**
	 * XMLEventValueConsumer impl
	 */
	public String nextValue(String value, ContextStack context) {
		//by default we get attribute values only here
		
		if(isUriCarrier(context, currentFile)) {
			int start = -1;
			StringBuilder sb = new StringBuilder();
			//replace oldNames with newNames and return
			sb.append(value);		
			Iterator it = mStrategy.getIterator();
			while(it.hasNext()) {
				try{
					URI oldNameURI = (URI)it.next();			
					//oldName = (new File(oldNameURI)).getName(); 
					//problem: above doesnt take escape sequences into account, gotta keep them, so:
					oldName = URIStringParser.stripPath(oldNameURI.toString());
					start = sb.indexOf(oldName);
					if(start > -1){
						//this value carries the old name
						sb.replace(start,start+oldName.length(),mStrategy.getNewLocalName(oldNameURI));
						break; //REVISIT are we sure first found is enough? values may contain several references...
					}	
				}catch (Exception e) {
					this.sendMessage(Level.WARNING,"exception when replacing values with new name: " + e.getMessage());
					return value;
				}
			}				
			return sb.toString();
		}
		return null; //if !isUriCarrier		
	}
	
	/**
	 * Performance enhancing; dont loop through name map if current node is recognized to not be a URI carrier
	 * If we dont recognize the filetype or context, return true. 
	 */
	private boolean isUriCarrier(ContextStack context, FilesetFile currentFile) {
				
		if(context.getLastEvent().getXMLEventType() == XMLEvent.ATTRIBUTE){
			String attrName = context.getLastEvent().getName().getLocalPart();
			if(currentFile instanceof SmilFile && !rgx.matches(rgx.SMIL_ATTRIBUTES_WITH_URIS,attrName)) {
				return false;
			}
			else if(currentFile instanceof Z3986DtbookFile && !rgx.matches(rgx.DTBOOK_ATTRIBUTES_WITH_URIS,attrName)) {
				return false;
			}
			else if(currentFile instanceof Xhtml10File && !rgx.matches(rgx.XHTML_ATTRS_WITH_URIS,attrName)) {
				return false;
			}
			//else its a value we are not sure about
		}
		//else its not an attribute; unexpected to be enabled for checking but not this methods role to
		//have an opinion on that
		return true;
	}

	/**
	 * FilesetErrorHandler impl
	 */
	public void error(FilesetFileException ffe) throws FilesetFileException {
		if (ffe instanceof FilesetFileFatalErrorException
				&& !(ffe.getCause() instanceof FileNotFoundException)) {
			this.sendMessage(Level.SEVERE, ffe.getCause()
					+ " in " + ffe.getOrigin());
		} else {
			this.sendMessage(Level.WARNING, ffe.getCause()
					+ " in " + ffe.getOrigin());
		}
	}
}
