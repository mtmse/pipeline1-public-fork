Daisy Pipeline Release Notes
============================

This document contains joint release notes for the __DAISY Pipeline Core__ and the __DAISY Pipeline GUI__.

The DAISY Pipeline is a cross-platform, open source framework for DTB-related document transformations. It provides a comprehensive solution for converting text documents into accessible formats for people with print disabilities. The DAISY Pipeline project is fundamentally a collaborative multi-organization project, created by and for the DAISY community. It has been launched in 2005 and is maintained by the DAISY Consortium.

More information is available on the [project home page](http://www.daisy.org/pipeline).

--------------------------------------------------------------------

## Pipeline Maintenance Release, 2011-12-15

Bug fixes and improvements:

 * [DTBook Fixer] Fixed a bug occurring when using relative input file paths
 * [DTBook to HTML] Added support for the 'start' attribute on 'list' elements
 * [DTBook to LaTeX] The generated LaTeX is now based on the memoir class, with more features like crop marks, improved page styles, proper page numbering accross volume boundaries
 * [DTBook to LaTeX] Improved the XSLT extensibility / customizability
 * [DTBook to LaTeX] Added support for volume splitting
 * [DTBook to LaTeX] Many layout and styling improvements
 * [DTBook to LaTeX] Many bug fixes
 * [DTBook to PEF] Improved the Swedish braille table
 * [DTBook to PEF] Updated to rely on the 'dotify' library
 * [PEF to Text] Improved error messages
 * [Braille Utils] Updated the brailleUtils library to version 1.1.0
 * [Narrator] Updated to the latest version used in the Save as DAISY Add-In for Microsoft Word version 2.5.5.1
 * [Narrator] Improved support for locale country codes
 * [Narrator] Added Windows SAPI5 language codes for ar-EG, en-GB and en-AU

Also:

 * New "DTBook Volume Splitter" transformer: takes a DTBook XML and inserts special div elements as split points, to be used by other scripts like "DTBook to LaTex", contributed by Christian Egli (SBS)

## Pipeline Maintenance Release [RC], 2011-03-17 ##


Bug fixes and improvements:

* [Narrator] Improved tolerance to rounding errors by augmenting the default end audio padding.
* [Narrator] Improved wave file handling (now generates 5000 files max in each sub directory)
* [Narrator] Improved parsing of Roman numerals found in 'special' pages
* [Narrator] Improved handling of acronyms: non-pronounceable acronyms are now spoken letter-by-letter)
* [Narrator] Improved handling of abbreviations: the 'title' attribute is spoken when present
* [Narrator] Improved the  long-standing "duplicate doctitle" issue
  * The Pipeline no-longer systematically creates a frontmatter
  * when the frontmatter is present but has no doctitle a doctitle is added from the metadata value.
  * when the frontmatter is present but has no docauthor, a docauthor is inserted with the value of the metadata.
  * nothing is changed if no frontmatter is present
  * when the "dc:Title" metadata is not set (invalid), the value is set to the value of the first doctitle, or the first heading if no doctitle is found.  
* [DAISY 2.02 splitter] only perform the split if the input book is larger than the split size
* [DAISY 2.02 to DAISY 3 forward migrator] Fixed several bugs 
* [DAISY 3 Validator] the file browser now asks for *.opf files
* [DAISY 3 to 2.02 downgrader] Fixed a bug that could occur when some SMIL elements had no associated audio.
* [DTBook to DAISY 3 Text-Only] Improved: now trims whitespace from NCX labels
* [DTBook to DAISY 3 Text-Only] Improved support for mixed content (uses the int_daisy_mixedContentNormalizer)
* [DTbook to LaTeX] Fixed handling special chars in title, author and date
* [DTbook to LaTeX] Fixed a problem with noteref lookup
* [DTbook to LaTeX] Improved the  transformer:
  * Allow for custom specification of page dimensions
  * Allow specification of line spacing
* [EPUB Creator] Added a new parameter "forceXhtml" to always create an XHTML-based EPUB even from a DTBook 2005-2 input
* ... and other minor fixes and improvements 

Also: 

* New "DTBook to Translated Braille DTBook script", which provides a basic integration of Liblouis in the pipeline, contributed by Christian Egli and Bernhard Wagner (SBS)
* Updated pef2text and text2pef transformers and related scripts (Emboss PEF, Text to PEF, PEF to Text, Braille Text To Text) to use the Braille Utils library.
  More information at:  http://code.google.com/p/brailleutils/
  
--------------------------------------------------------------------

## Pipeline Maintenance Release, 2010-01-25 ##

* Refined documentation of the ODT to DAISY XML script and transformer
* Fixed a bug with DTB metadata repeatable in Z39.86 but not in DAISY 2.02
* Added support for Braillo 200 in PEF embosser scripts  

## Pipeline Maintenance Release [RC], 2010-01-18 ##

* New DTBook to DAISY 3 text-only script based on Java, contributed by James Pritchett, RFB&D.
* Added a DTBook Fix step to the XSLT-based DTBook to DAISY 3 text-only script. 
* Upgraded the Eclipse RCP target platform to a version based on Eclipse 3.5.1
* Updated the documentation of newly contributed scripts.

## Pipeline Maintenance Release [Beta], 2009-12-22 ##

* New Hyphenator script, contributed by Joel Håkansson, TPB
* New DTBook to PEF script, contributed by Joel Håkansson, TPB
* New DTBook to text script, contributed by Joel Håkansson, TPB 
* New PEF File splitter script, contributed by Joel Håkansson, TPB
* New PEF File merger script, contributed by Joel Håkansson, TPB
* New ODT to DTBook script, contributed by Vincent Spiewak, Freelance.
* Beta version of a DTBook to DAISY 3 text-only script based on XSLT, contributed by Alex Bernier, BrailleNet.
* Added Finnish localization of the Narrator transformers, contributed by Päivi Suhonen, Celia.
* Added Norwegian localization of the Narrator transformers, contributed by Olav Indergaard, NLB.
* Added Japanese localization of the Narrator transformers, contributed by ATDO
* Added support for country in the language codes of the ttsbuilder.xml
* Added an optional DTBookFix step to the DTBook to 2.02 text-only script
* Added a validator time tolerance parameter to the DAISY 3 to DAISY 2.02 converter
* Added a new int_daisy_metadataEditor transformer that is able to edit (add/overwrite/merge) metadata in a file or fileset
* Launcher scripts from the core ZIP distribution now have the "executable" permission
* SoX and LAME Universal Binaries are now bundled with the Mac OS X distribution, with no need to install them externally

... and many bug fixes, including:

* Fixed a bug in DTBookFixer: insertion of "docauthor" at the wrong place (reported by David Nelson)
* Fixed a bug in MixedContentNormalizer: ignorable elements in a foreign namespace were not recognized (reported by Joris van Eijk)
* Fixed a bug in Text-Only Fileset Generator, related to spaces in file names (reported by Marc Sutton)
* Fixed DTBook2Xhtml and CharsetSwitcher to also copy referring files from sub directories of the base input directory
* Fixed a bug in the Narrator: punctuation characters was not pronounced on Mac OS X in URLs, email addresses, etc (reported by Greg Kearney)
* Fixed a bug caused by '+' signs in the path of temporary files with the latest Java version on Mac OS X.
* Fixed a bug in Fileset Renamer with the pattern fixed(name)+seq (reported by Avneesh Singh)
* Fixed a bug in CharsetSwitcher: metadata was not indented, which is required by some player (reported by Mayu Hamada)
* Fixed the 2.02 NCC schemas to make http-equiv content-type case insensitive (reported by Mayu Hamada)
* Fixed the RTF to DTBook: language detection now support the \deflang RTF tag (reported by Paul Wood)
* Fixed NCXMaker from the fileset creator: could cause random errors when an element had both 'src' and 'smil:src' attributes (reported by Vincent Spiewak)
* Fixed the DTBookFixer to add dummy content to pagenum[@page='special'] (reported by Jamie Norrish)
* Fixed the Audio Tagger, failing on some 239.86 books (reported by Greg Kearney)
* Fixed the DAISY 2.02 to DAISY 3 Forward Migrator to generate a dtb:mediaType if none was present (reported by Sébastien Hinderer)
* Fixed the DTbook to LaTeX transformer to be able to handle many floats, more than 26 bullets in an alphanumeric enumeration, images within section headings and to handle hd inside sidebars correctly (all reported by Greg Kearney)

--------------------------------------------------------------------

## Pipeline Maintenance Release, 2009-04-10 ##

* Narrator now supports multiple language TTS rendering on all platforms
* New option to generate a Table of Contents in the Dtbook to XHTML conversion, thanks to Alex Bernier, BrailleNet
* New option to split the result of the Dtbook to XHTML conversion in multiple pages, thanks to Alex Bernier, BrailleNet
* ... and bug fixes.


## Pipeline Maintenance Release [RC], 2009-04-03 ##

* Reorganization of the script tree.
* Fixes to the DTBook 1.1.0 to DTBook 2005 Forward Migrator, thanks to Alex Bernier, BrailleNet
* Improvements the DTBook to RTF converter, thanks to Ole Holst Andersen, DBB
* Finnish localization of the Structure Announcer (used in the Narrator), thanks to Päivi Suhonen, Celia Library for the Visually Impaired
* Command line launcher scripts are now available in the Pipeline GUI installation.
* Improvement of the default Java heap space value

## Pipeline Maintenance Release [Beta], 2009-02-13 ##

* Support for MathML in DAISY in Narrator. Read more in the Narrator documentation.
* Support for Narrator on Linux. Read more in the Narrator documentation.
* The desktop user interface is now accessible via VoiceOver on Mac (RCP framework upgraded to 3.4.1).
* Many improvements to the Dtbook to Latex conversion, thanks to Christian Egli, SBS
* A beta of Save-as-DAISY Audacity.
* Several scripts relating to the Portable Embosser Format (PEF), thanks to Joel Håkansson, TPB
* ... and many bug fixes.

--------------------------------------------------------------------

## Pipeline Maintenance Release, 2008-05-02


* Complete Windows installer
  * The Windows installer is now complete, allowing installation of the Pipeline without having to install any additional libraries (except the Java runtime).
  * Note - this installer allows both single-user and multiple-users installations
* Support for DTBook 2005-3 in relevant Pipeline Scripts/Transformers
* Changes in the Narrator TTS Script
  * The Narrator script has been ascertained to support input from the latest version of MS Word Save-as-DAISY.
* Changes in the DTBook Fix Script
  * Several performance enhancements added to the DTBook Fix script.
* Changes in the OPS/EPUB Creator script
  * Now uses EpubCheck for output validation.
  * Dynamically converts input documents to XHTML 1.1 when needed for validity.
* Changes in the Xhtml to DTBook script
  * improved the overall stability and input sensitivity
* New Script: DTBook Migrator
  * An upwards migrator for DTBook documents versions 1.1.0 to 2005-3, and all versions inbetween. This script is in a beta version.
* New Script: DTB Forward Migrator
  * An upwards migrator for Daisy 2.02 DTBs to DAISY/NISO 2005. Supports NCC-Only and Full Text DTB input. This script is in a beta version.
* New Script: Daisy 2.02 Text-Only DTB generator.
  * Allows generating Daisy 2.02 Text-Only DTBs from DTBook or XHTML input. This script is in a beta version.
* Localization
  * A partial Hindi localization is provided with the default installer.
  * The GUI now has a menu item to switch the GUI locale
* ODF to DTBook deprecated and removed
  * The ODF to DTBook Script is no longer included in the default installer, as it was broken and no longer supported by the original developer.
  * A new ODF to DTBook transformation (supporting MathML) is under active development.
* ... and a long list of bug fixes!

--------------------------------------------------------------------

## Pipeline Maintenance Release, 2007-12-21 ##

### Changes regarding the installation process:

* A complete package handling for Mac OS X installation
* Beta installer for Windows (this installer will be finalized in the January/February 2008 timeframe)

### Changes in the GUI
* New feature for software updates ("Updates" available in the Help menu)

### New Functionality

Comments, suggestions and bug reports welcome!

* New DTBook to RTF transformer and script, contributed by Ole Holst Andersen (DBB).

  The script is available in the /conversion/simple/ category of the Script selector.

* Initial version of an XHTML to DTBook transformer, contributed by Per Sennels (Huseby Norway)

  This early version is provided for input and comments that will guide further development. The Script and Transformer documentation describes the current status and principles. Note * the DTBook output from this script is not yet ready for use; it will most likely be invalid.

  The script is available in the /conversion/advanced/ category of the Script selector.

* New DTBook Fixer transformer and script, contributed by Joel Håkansson (TPB), James Pritchett (RFB&D) and Linus Ericson (TPB)

  This transformer will try to repair common problems in DTBook files; primarly problems that occur in files that are output from automated conversion processes, such as the WordML to DTBook Script.

 The standalone script is available in the /manipulation/simple/ category of the Script selector. The DTBook Fixer transformer is now also included as a part of the WordML to DTBook, RTF to DTBook and ODF to Dtbook scripts.

* New DTBook 2005-1 to DTBook 2005-2 migrator transformer and script, contributed by James Pritchett (RFB&D)
  
  This is a part of the "DAISY Migration Suite" script collection, that will be finalized during 2008.

* Improved DTBook to XHTML transformer, after a feature request from Chris Koch (ViewPlus Technologies)

  The DTBook to XHTML conversion now has experimental support for the DTBook MathML extension. If the input DTBook file contains MathML, the output XHTML file will include MathML islands as defined by the W3C XHTML 1.1 plus MathML 2.0 plus SVG 1.1 profile.

* Validation updates

  The Z3986 DTB Validator now uses the latest ZedVal release (2.0 RC4, released 20071219)

### Bug fixes ,stability and performance improvements

Too many to mention here, the full list can be reviewed [online](http://sourceforge.net/mailarchive/forum.php?forum_name=daisymfc-svn). 

--------------------------------------------------------------------

## Pipeline Maintenance Release, 2007-10-03 ##

This is a maintenance release of the first public version of the Pipeline.

### Changes in the Core:


* Narrator now works on Mac OS X (many thanks to Greg Kearney!)
* Added context information for validation error messages
* Added new transformer for image conversion (se_tpb_imageConverter)
* Added new script "Image Converter"
* Many enhancements to the WordML to DTBook transformer
* Added a new script "DTB Audio Encoder Renamer Splitter" (requested by Einar Lee, BBI, Iceland)
* Fixed Bug 1804731: OPSCreator creates ids in foreign namespace
* Fixed Bug 1804664: NumberOfEntries field misplaced in PLS playlist
* Fixed Bug 1802128: Error in validation of dtbook files
* Fixed Bug 1779266: odf2dtbook, header chain changed
* Fixed Bug 1770791: NPE in Daisy202Validator
* Fixed Bug 1770771: bug in OCFCreator
* Fixed Bug 1770770: XSLTRunner reference wrong

### Changes in the GUI:

* Fixed Request 1777526: Replace the old keys preference pane with the new 3.3/Europa one
* Fixed Request 1751028: add a "reset perpective" action
* Fixed Request 1751027: Add a preference for the path to ImageMagick
* Fixed Request 1752454: sartup.jar should be renamed to a more unique name
* Fixed Request 1777504: Improve the refresh of the Progress view
  The progress view now shows the last selected Job. A newly created job is automatically selected.
* Fixed Request 1777498: Add more textual info to the Progress view items
  The Task list in the progress view should be recognized as a list control by accessible tools. Each task has a custom accessible text.
* Fixed Bug 1751017: Need a TAB after F6 to set the focus to the Browser
* Fixed Bug  1751023: Doc is not updated if ToC is changed via Ctrl+TAB
  The user must now press the Enter key to update the documentation pane.
* Fixed Bug 1752458: parameter nodes don't have right Accessible label (in the Job view)
* Fixed Bug 1777477: Show next view action doesn't cycle the views
  To cycle through the view, the user must hold the Ctrl key while pressing F7. 
  (Note that it's the same behaviour in Microsoft Visual Studio, where Ctrl+TAB only cycle the two latest tabs but Ctrl(Hold)+TAB cycles the whole list)
* Fixed Bug 1777483: ToC title is not spoken by SR after Ctrl+TAB
  The focus is now set the the Tab header control after a Ctrl+TAB switch so that the screen reader speaks the ToC page title.
* Fixed Bug 1777494: Header widget of progress view isn't spoken by SR
  The header of the progress view is now a read-only text that can take the focus and has a custom accessible name.
* Fixed Bug 1777497: Arrow Up/Down cycle the tasks in the Progress view
* Fixed Bug 1747754: Cannot change the value of a boolean script parameter in GUI
* Fixed Bug 1738845: Problem opening Windows ZIP archive
* Fixed several bugs in the Move Job actions

--------------------------------------------------------------------


## Pipeline Final Release, 2007-06-29 ##

This is the final public release of the DAISY Pipeline, with full documentation.

### Changes in GUI
* Fixed several bugs.
* Added preferences dialog for re-execution of jobs
* Finalized branding
* Added a full-blown welcome screen
* Added a complete documentation

### Changes in Core:
* Minor bugfixes
* Complete documentation

## Pipeline Release Candidate, 2007-06-08 ##

The development team initially intended to release a fully public release today, but due to an underestimated workload to get the Pipeline GUI and Core co- operating as desired, we have decided to delay the public release approximately two to three weeks.

Nevertheless, todays release is 100% feature complete, give or take some percents, and a still not finalized introductory first-launch welcome screen. Further, some bugs remain, and some documents remain to be written.

This release will be followed by a fully equipped release in late June 2007.

### Changes in GUI
* Fixed several bugs and accessibility issues. Many thanks to Avneesh for providing screen reader usage input.
* Added preferences dialog
* Added branding features (note - branding is not 100% complete.)
* Developer oriented changes: substantial changes to build process

### Changes in Core
* Complete set of scripts for this release committed. Special thanks to Dave Pawson for helping to finalize the ODF to DTBook transform.
* Added documentation (note - documentation suite is not 100% complete)
 

## Pipeline Beta Release, 2007-05-25 ##

The primary purpose of this release is to enable testing and evaluation of a new GUI. This release will be followed by a fully equipped release in mid June 2007.

### Changes in GUI  

This release contains the first public access to a new and redesigned cross-platform GUI, that runs on Windows, Mac and Linux platforms.

### Changes in Core
* Provisions of script-level (end-user oriented) documentation.
* Support for jarness.
* Miscellaneous refactorings and new transformers. Note * not all functionality is exposed in scripts shipping with this release.

--------------------------------------------------------------------

## Pipeline First Public Access, 2006-10-17 ##

This is the second release of DAISY Pipeline. Some notable updates to the software is listed below:

### Licensing

Software User Agreements for the DAISY Pipeline(both the DMFC and the GUI) provide specific licensing information regarding 3rd party software. As in all agreements, it is the obligation of the user to read and accept all such terms and conditions of all third party software packages prior to the use of the DAISY Pipeline.


### WordML2DTBook  
Version 1.4.1 (10 October 2006)

The most significant new feature in this release is that the converter now extracts images embedded in Word documents. Other enhancements include:

* Automatic frontmatter creation: everything preceding the first heading will be part of the frontmatter.
* Attributes: several attributes have been added or changed.
* Customization: two new input parameters have been added: 'stylesheet' and 'images'.
* Footnotes: The numbering of native footnotes have been improved

See the documentation for further information.

### rtf2dtbook
Version 1.1 (13 October 2006)

The converter uses command-line python and not jython (the java-python bridge) to complete the rtf-to-xml transformation. The switch has increased the speed and reliablity of the transformer. In particular:

*  The rtf-to-dtbook transformer may be run more than once/session (resolution of issue #1543841)
*  The installation of the DAISY Pipeline does not terminate without notice (resolution of issue #1551549)
*  The rtf-to-dtbook transformer using python performs at many times the speed of the same 
   transformer using jython.

### Java Virtual Machine parameters

The default heap size is set to 128 MB (-Xms128m -Xmx128m). To set it to a different value, create a file named params.cfg in the installation directory containing a single line describing any parameters that should be sent to the Java virtual machine.

### Resolved issues from the FPA Release

* 1543841

  Running the RTF-to-Dtbook script more than once during one session will make the application freeze. The transformer was changed to call python directly, instead of using jython, a java-python bridge. This allows the RTF-to-DTBook script to run more than once.

* licenseinfo

  The GUI does not yet provide information on specific licensing for specific scripts&transformers. A Software User Agreement for both the DMFC and GUI enumerates specific licensing for all transformers. The Software Agreement must be accepted prior to installation of the program.

* installer

  Running the windows installer as non admin makes it impossible to uninstall using the uninstaller. The windows installer may be run by admin or non-admin users, and the uninstaller works for both user types.

### Pending features Below features are not yet implemented, but high on the list for the next release:

* Access to transformer documentation from the GUI


## Pipeline First Public Access, 2006-08-25 ##

This is the First Public Access (FPA) release of DMFC.

### Known Issues
Below issues are already identified and marked for fixing til next release:

* 1543841

 Running the RTF-to-Dtbook script more than once during one session will make the application freeze. The cause of the issue is known (#1543841) and will be fixed by next release.

* 1543978

 Running the int_daisy_unicodeTranscoder transformer with output encoding set to other than utf-8 or utf-16 produces incorrect representation of certain characters. The cause of the issue is known (#1543978) and will be fixed by next release.

* licenseinfo

 The GUI does not yet provide information on specific licensing for specific scripts&transformers.

* installer

 Running the windows installer as non admin makes it impossible to uninstall using the uninstaller. Workaround is to delete the install directory, and the link on the start menu.

### Pending features
Below features are not yet implemented, but high on the list for the next release

* Access to transformer documentation from the GUI
* Specific licensing information when using particular 3d party libraries.