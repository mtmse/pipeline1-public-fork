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
package org.daisy.util.fileset;

import org.daisy.util.mime.MIMEConstants;

/**
 * Represents an XHTML 1.0 file
 * @author Markus Gylling 
 */
public interface Xhtml10File extends XmlFile{
	static String mimeStringConstant = MIMEConstants.MIME_APPLICATION_XHTML_XML;

	/**
	 * @return true if the xhtml heading sequence is correctly hierarchical, false otherwise
	 */
	public boolean hasHierarchicalHeadingSequence();

	/**
	 * @return The title of this XHTML file, where head/meta/@dc:identifier takes precedence over head/title. 
	 * Return null if none of these values are present in the instance.
	 */
	public String getTitle();
	
	/**
	 * @return The dc:identifier of this XHTML file, or null if this value is not present in the instance.
	 */
	public String getIdentifier();
	
}
