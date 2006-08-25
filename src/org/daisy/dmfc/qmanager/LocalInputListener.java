/*
 * DMFC - The DAISY Multi Format Converter
 * Copyright (C) 2005  Daisy Consortium
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

package org.daisy.dmfc.qmanager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.daisy.dmfc.core.InputListener;
import org.daisy.dmfc.core.Prompt;

public class LocalInputListener implements InputListener {

	private boolean isAborted = false;
	
	public String getInputAsString(Prompt prompt) {
		System.err.println("[" + prompt.getMessageOriginator() + "] Prompt: " + prompt.getMessage());
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String line = null;
		try {
		    line = br.readLine();
        } catch (IOException e) {
        }
		return line;
	}

    public boolean isAborted() {
        return isAborted;
    }
    
    public void setAborted(boolean aborted){
    	this.isAborted=aborted;
    }
    
	
}
