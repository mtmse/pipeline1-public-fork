package org_pef_dtbook2pef.system.tasks;

import java.io.File;
import java.io.IOException;

import org.daisy.util.file.FileUtils;
import org_pef_dtbook2pef.system.InternalTask;
import org_pef_dtbook2pef.system.InternalTaskException;

/**
 * <p>DebugTask aids debugging by copying the file both to output and to a separate file.
 * This way the state of the conversion can be preserved.</p>
 * <p>No specific input requirements.</p>
 * 
 * @author Joel Håkansson, TPB
 */
public class DebugTask extends InternalTask {
	private File debug;
	
	/**
	 * Create a new DebugTask
	 * @param name a descriptive name for the task
	 * @param debug path to debug output
	 */
	public DebugTask(String name, File debug) {
		super(name);
		this.debug = debug;
	}

	@Override
	public void execute(File input, File output) throws InternalTaskException {
		try {
			FileUtils.copy(input, debug);
			FileUtils.copy(input, output);
		} catch (IOException e) {
			throw new InternalTaskException("Exception while copying file.", e);
		}
	}

}
