package org_pef_dtbook2pef.system.tasks.layout.page;

import java.util.ArrayList;


/**
 * ConfigurableLayoutMaster will ensure that the LayoutMaster measurements adds up.
 * @author joha
 *
 *
 */
// TODO: Since class isn't abstract anymore, it is possible to change LayoutMasterConfigurator into a regular builder
public class ConfigurableLayoutMaster implements LayoutMaster {
	protected final int headerHeight;
	protected final int footerHeight;
	protected final int flowWidth;
	//protected final int flowHeight;
	protected final int pageWidth;
	protected final int pageHeight;
	protected final int innerMargin;
	protected final int outerMargin;
	protected final float rowSpacing;
	protected final boolean duplex;
	protected final ArrayList<Template> templates;

	public ConfigurableLayoutMaster(LayoutMasterConfigurator config) {
		// int flowWidth, int flowHeight, int headerHeight, int footerHeight, int innerMargin, int outerMargin, float rowSpacing
		this.headerHeight = config.headerHeight;
		this.footerHeight = config.footerHeight;
		this.flowWidth = config.pageWidth-config.innerMargin-config.outerMargin;
		//this.flowHeight = config.pageHeight-config.headerHeight-config.footerHeight;
		this.pageWidth = config.pageWidth;
		this.pageHeight = config.pageHeight;
		this.innerMargin = config.innerMargin;
		this.outerMargin = config.outerMargin;
		this.rowSpacing = config.rowSpacing;
		this.duplex = config.duplex;
		this.templates = config.templates;
	}
	
	public int getPageWidth() {
		return pageWidth;
	}

	public int getPageHeight() {
		return pageHeight;
	}

	public int getFlowWidth() {
		return flowWidth;
	}
/*
	public int getFlowHeight() {
		return flowHeight;
	}*/

	public int getHeaderHeight() {
		return headerHeight;
	}

	public int getFooterHeight() {
		return footerHeight;
	}

	public int getInnerMargin() {
		return innerMargin;
	}

	public int getOuterMargin() {
		return outerMargin;
	}
	
	public float getRowSpacing() {
		return rowSpacing;
	}
	
	public boolean duplex() {
		return duplex;
	}
	
	public Template getTemplate(int pagenum) {
		for (Template t : templates) {
			if (t.appliesTo(pagenum)) { return t; }
		}
		return null;
	}

}