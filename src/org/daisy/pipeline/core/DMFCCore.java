/*
 * DMFC - The DAISY Multi Format Converter Copyright (C) 2005-2007 Daisy
 * Consortium
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
package org.daisy.pipeline.core;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Handler;
import java.util.logging.Logger;

import org.daisy.pipeline.core.event.CoreMessageEvent;
import org.daisy.pipeline.core.event.EventBus;
import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.script.Creator;
import org.daisy.pipeline.core.script.Job;
import org.daisy.pipeline.core.script.Runner;
import org.daisy.pipeline.core.script.Script;
import org.daisy.pipeline.core.script.ScriptValidationException;
import org.daisy.pipeline.core.transformer.TransformerHandler;
import org.daisy.pipeline.core.transformer.TransformerHandlerLoader;
import org.daisy.pipeline.exception.DMFCConfigurationException;
import org.daisy.pipeline.exception.JobFailedException;
import org.daisy.pipeline.exception.TransformerDisabledException;
import org.daisy.util.file.TempFile;
import org.daisy.util.i18n.I18n;
import org.daisy.util.i18n.XMLProperties;
import org.daisy.util.i18n.XMLPropertyResourceBundle;

/**
 * This is the class users of DMFC should instantiate. A common usage of DMFC
 * would include the following:
 * 
 * <pre>
 * DMFCCore dmfc = new DMFCCore(inputListener, locale);
 * dmfc.executeScript(scriptFile);
 * </pre>
 * 
 * @author Linus Ericson
 */
public class DMFCCore implements TransformerHandlerLoader {

    private File mHomeDirectory = null;

    private InputListener mInputListener;
    private Map<String, TransformerHandler> mTransformerHandlers = new HashMap<String, TransformerHandler>();

    private Creator mCreator;
    private Runner mRunner;

    /**
     * Create an instance of the Daisy Pipeline. This constructor will fetch the
     * Pipeline properties files in the <code>bin/</code> sub-directory of
     * <code>homeDir</code>.
     * 
     * @param inListener a listener of (user) input events
     * @param homeDir the home directory
     */
    public DMFCCore(InputListener inListener, File homeDir)
            throws DMFCConfigurationException {
        this(inListener, homeDir, null);
    }

    /**
     * Create an instance of the Daisy Pipeline.
     * 
     * @param inListener a listener of (user) input events
     * @param homeDir the home directory
     * @param propsDir the location of the pipeline properties file
     */
    public DMFCCore(InputListener inListener, File homeDir, Properties userProps)
            throws DMFCConfigurationException {
        mInputListener = inListener;
        mHomeDirectory = homeDir;
        mCreator = new Creator(this);
        mRunner = new Runner();
        initialize(userProps);
    }

    private void initialize(Properties userProps)
            throws DMFCConfigurationException, SecurityException {
        // Load properties
        // mg 20070530: we use two properties files; one with likelihood of user
        // access and one less likely
        // Init system properties
        URL propsURL = getClass().getClassLoader().getResource(
                "pipeline.properties");
        XMLProperties properties = new XMLProperties(System.getProperties());
        try {
            properties.loadFromXML(propsURL.openStream());
        } catch (IOException e) {
            throw new DMFCConfigurationException(
                    "Can't read pipeline.properties", e);
        }
        System.setProperties(properties);
        // Init user properties
        setUserProperties(userProps);

        // Load messages
        ResourceBundle bundle = XMLPropertyResourceBundle.getBundle((this
                .getClass().getPackage().getName()).replace('.', '/')
                + "/pipeline.messages", Locale.ENGLISH, this.getClass()
                .getClassLoader());
        // ResourceBundle bundle =
        // XMLPropertyResourceBundle.getBundle(this.getClass().getPackage().getName()
        // + ".pipeline.messages", Locale.getDefault(),
        // this.getClass().getClassLoader());
        // alternatively:
        // ResourceBundle bundle = XMLPropertyResourceBundle.getBundle(
        // this.getClass().getResource("pipeline.messages"), Locale.ENGLISH);

        I18n.setDefaultBundle(bundle);

        // Setup logging
        Logger lg = Logger.getLogger("");
        Handler[] handlers = lg.getHandlers();
        for (int i = 0; i < handlers.length; ++i) {
            lg.removeHandler(handlers[i]);
        }

    }

    /**
     * Configure the Pipeline with the given user properties. This
     * implementation adds the given properties to the System properties.
     * 
     * @param properties the user properties used to configure the Pipeline
     */
    public void setUserProperties(Properties properties) {
        // Set system properties
        for (Object key : properties.keySet()) {
            String name = (String) key;
            System.setProperty(name, properties.getProperty(name));
        }
        // Apply new properties if possible
        TempFile.setTempDir(new File(System.getProperty("dmfc.tempDir")));
    }

    /**
     * Gets the home directory of DMFC.
     * 
     * @return the home directory of DMFC or <code>null</code> if it has not
     *         yet been set.
     */
    public File getHomeDirectory() {
        return mHomeDirectory;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.daisy.pipeline.core.transformer.TransformerHandlerLoader#getTransformerHandler(java.lang.String)
     */
    public TransformerHandler getTransformerHandler(String transformerName)
            throws TransformerDisabledException {
        if (mTransformerHandlers.containsKey(transformerName)) {
            return mTransformerHandlers.get(transformerName);
        }
        File transformersDir = new File(getHomeDirectory(), "transformers");

        // mg20070520: if subdir (such as se_tpb_dtbSplitterMerger.split)
        transformerName = transformerName.replace('.', '/');

        // Try to load TDF from directory
        File[] files = new File(transformersDir, transformerName)
                .listFiles(new FileFilter() {
                    public boolean accept(File file) {
                        return file.getName().endsWith(".tdf");
                    }
                });

        if (files != null && files.length > 1) {
            EventBus.getInstance().publish(
                    new CoreMessageEvent(this,
                            "Error! Incorrect number of TDFs for transformer "
                                    + transformerName,
                            MessageEvent.Type.WARNING));
        } else if (files != null && files.length == 1) {
            TransformerHandler th = new TransformerHandler(files[0],
                    transformersDir, mInputListener);
            mTransformerHandlers.put(transformerName, th);
            return th;
        } else {
            // Trying JAR instead
            // System.err.println("trying jar...");
            File jarFile = new File(getHomeDirectory(), "transformers/"
                    + transformerName + ".jar");
            if (jarFile.exists()) {
                TransformerHandler th = new TransformerHandler(jarFile,
                        transformerName, mInputListener, true);
                mTransformerHandlers.put(transformerName, th);
                return th;
            } else {
                // System.err.println("jar doesn't exist");
            }
        }
        return null;
    }

    /**
     * Creates a new Script object from a script file
     * 
     * @param url
     * @return
     * @throws ScriptValidationException
     */
    public Script newScript(URL url) throws ScriptValidationException {
        return mCreator.newScript(url);
    }

    /**
     * Execute a script contained in a ScriptRunner object.
     * 
     * @param job
     * @throws JobFailedException
     */
    public void execute(Job job) throws JobFailedException {
        this.mRunner.execute(job);
    }

    /**
     * Gets the number of completed tasks in the current script. If no script is
     * currently being executed, 0 is returned.
     * 
     * @return
     */
    public int getCompletedTasks() {
        return mRunner.getCompletedTasks();
    }

    /**
     * Is a script currently being run?
     * 
     * @return
     */
    public boolean isRunning() {
        return mRunner.isRunning();
    }

    /**
     * Tests if a given directory is the home directory.
     * 
     * @param folder
     * @return
     */
    public static boolean testHomeDirectory(File folder) {
        File[] files = folder.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return ("transformers".equals(file.getName()) && file
                        .isDirectory());
            }
        });
        return files != null && files.length == 1;
    }
}