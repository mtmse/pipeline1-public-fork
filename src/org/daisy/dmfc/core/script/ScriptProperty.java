/*
 * Daisy Pipeline
 * Copyright (C) 2007  Daisy Consortium
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.daisy.dmfc.core.script;

import java.util.Map;

/**
 * A class representing a script property. 
 * @author Linus Ericson
 */
public class ScriptProperty extends AbstractProperty {

	/**
	 * Constructor.
	 * @param name the name of the property
	 * @param value the value of the property
	 * @param properties a Map of properties containing all properties defined in this script.
	 * @throws ScriptValidationException 
	 */
	ScriptProperty(String name, String value, Map<String, AbstractProperty> properties) throws ScriptValidationException {
		super(name, value, properties);
	}

}
