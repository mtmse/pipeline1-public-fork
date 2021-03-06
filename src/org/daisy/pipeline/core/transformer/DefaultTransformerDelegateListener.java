/*
 * Copyright (C) 2008  Daisy Consortium
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
package org.daisy.pipeline.core.transformer;

import javax.xml.stream.Location;

import org.daisy.pipeline.core.event.MessageEvent.Cause;
import org.daisy.pipeline.core.event.MessageEvent.Type;

/**
 * A default implementation of the {@link TransformerDelegateListener} that can
 * be used as an anonymous implementation.
 * 
 * @author Romain Deltour
 * 
 */
public class DefaultTransformerDelegateListener implements
		TransformerDelegateListener {
	
	/**
	 * Query the listener on whether it has received an abort event.
	 * This default implementation always returns <code>false</code>.
	 * @return <code>false</code>
	 */
	public boolean delegateCheckAbort() {
		return false;
	}

	/**
	 * Request localization through accessing the listeners message bundles.
	 * This default implementation returns <code>null</code>.
	 */
	public String delegateLocalize(String key, Object[] params) {
		return null;
	}

	
	/**
	 * Emit a delegate message to the listener.
	 * This default implementation does nothing.
	 */
	public void delegateMessage(Object delegate, String message, Type type,
			Cause cause, Location location) {
		// does nothing
	}

	/**
	 * Report a delegate progress measure (between 0 and 1) to the listener.
	 * This default implementation does nothinf.
	 */
	public void delegateProgress(Object delegate, double progress) {
		// does nothing
	}

}
