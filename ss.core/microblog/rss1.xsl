<?xml version="1.0" encoding="UTF-8"?>
<!--
  Title: RSS 1.0 XSL Template
  Author: Rich Manalang (http://manalang.com)
  Description: This sample XSLT will convert any valid RSS 1.0 feed to HTML.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:foo="http://purl.org/rss/1.0/">
  	<xsl:output method="html"/>
	<xsl:template match="/">
		<style>
			<xsl:comment>
			.syndication-content-area {
			}
			.syndication-title {
				font-size: 1.1em;
				font-weight: bold;
			}
			.syndication-description {
				font-size: .9em;
				margin: 0 0 10px 0;
			}
			.syndication-list {
				font-size: .8em;
				margin:0 0 0 20px;
			}
			.syndication-list-item {
				margin: 0 0 5px 0;
			}
			.syndication-list-item a,
			.syndication-list-item a:link {
				color: blue;
			}
			.syndication-list-item a:active,
			.syndication-list-item a:hover {
				color: red;
			}
			.syndication-list-item a:visited {
				color: black;
				text-decoration: none;
			}
			.syndication-list-item-date {
				font-size: .8em;
			}
			.syndication-list-item-description {
				font-size: .9em;
			}
			</xsl:comment>
		</style>
		<xsl:apply-templates select="/rdf:RDF/foo:channel"/>
	</xsl:template>
	<xsl:template match="/rdf:RDF/foo:channel">
		<div class="syndication-content-area">
			<div class="syndication-title">
				<xsl:value-of select="foo:title"/>
			</div>
			<div class="syndication-description">
				<xsl:value-of select="foo:description"  disable-output-escaping="yes"/>
			</div>
			<ul class="syndication-list">
				<xsl:apply-templates select="/rdf:RDF/foo:item"/>
			</ul>
		</div>
	</xsl:template>
	<xsl:template match="/rdf:RDF/foo:item">
		<li class="syndication-list-item">
			<a href="{foo:link}" title="{foo:description}">
				<xsl:value-of select="foo:title"/>
			</a>
			<span class="syndication-list-item-date">
						(
				<xsl:value-of select="dc:date"/>)
			</span>
			<div class="syndication-list-item-description">
				<xsl:value-of select="foo:description"  disable-output-escaping="yes"/>
			</div>
		</li>
	</xsl:template>
</xsl:stylesheet>
