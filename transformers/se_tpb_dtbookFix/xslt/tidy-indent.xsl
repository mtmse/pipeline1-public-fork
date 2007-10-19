<?xml version="1.0" encoding="UTF-8"?>
<!--
		Indent
		Indents output to aid debugging

		Joel Håkansson, TPB
		Version 2007-10-19
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:dtb="http://www.daisy.org/z3986/2005/dtbook/" xmlns="http://www.daisy.org/z3986/2005/dtbook/" exclude-result-prefixes="dtb">
	
	<xsl:include href="recursive-copy.xsl"/>
	<xsl:include href="output.xsl"/>

	<!-- Indent elements not in inline context -->
	<xsl:template match="*[count(parent::*/text()[normalize-space()!=''])=0]"> <!-- not(following-sibling::text()[normalize-space()!=''] or preceding-sibling::text()[normalize-space()!='']) -->
		<xsl:if test="following-sibling::* or preceding-sibling::* or count(descendant::*) &gt; 1">	
			<xsl:text>&#x0a;</xsl:text>
			<xsl:for-each select="ancestor::*">
				<xsl:text>&#x09;</xsl:text>
			</xsl:for-each>
		</xsl:if>
		<xsl:copy>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
			<xsl:if test="not(text()[normalize-space()!='']) and count(descendant::*) &gt; 1">
				<xsl:text>&#x0a;</xsl:text>
				<xsl:for-each select="ancestor::*">
					<xsl:text>&#x09;</xsl:text>
				</xsl:for-each>
			</xsl:if>
		</xsl:copy>
	</xsl:template>
	
	<!-- remove whitespace text nodes not in inline context -->
	<xsl:template match="text()[count(parent::*/text()[normalize-space()!=''])=0]"/>
	
</xsl:stylesheet>
