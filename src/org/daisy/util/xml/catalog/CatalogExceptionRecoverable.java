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
package org.daisy.util.xml.catalog;

/**
 * Thrown when an exception occurs that means that the catalog implementation may still be usable
 * @author markusg
 *
 */
public class CatalogExceptionRecoverable extends CatalogException {

	public CatalogExceptionRecoverable(Exception e) {
        super(e);
    }

    public CatalogExceptionRecoverable(String message) {
        super(message);
    }

    public CatalogExceptionRecoverable(String message, Exception e) {
        super(message, e);
    }
    
    private static final long serialVersionUID = -5097421779148766368L;
}
