<?xml version="1.0"?>
<!--
  Title: RSS 2.0 XSL Template
  Author: Rich Manalang (http://manalang.com)
  Description: This sample XSLT will convert any valid RSS 2.0 feed to HTML.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:wfw="http://wellformedweb.org/CommentAPI/">
        <xsl:output method="html"/>

	<xsl:template match="contact">
		<html>
		<head>
			<!--

				CSS in the future
			-->

		</head>
		<body>
<!--			<div align="center">-->
			<table cellpadding="5" border="0" cellspacing="0">
				<tr>
					<td>First Name:</td>
					<td><xsl:value-of select="/contact/first_name/@value"/></td>

					<td width="80px"/>
					<td>Last Name:</td>
					<td><xsl:value-of select="/contact/last_name/@value"/></td>

				</tr>
				<tr>
					<td>Title:</td>
					<td><xsl:value-of select="/contact/title/@value"/></td>
					<td width="80px"/>
					<td>Organization:</td>
					<td><xsl:value-of select="/contact/organization/@value"/></td>
				</tr>
				<tr>
					<td>Street:</td>
					<td><xsl:value-of select="/contact/street/@value"/></td>
					<td width="80px"/>
					<td>Department:</td>
					<td><xsl:value-of select="/contact/department/@value"/></td>
				</tr>
				<tr>
					<td>Street 2:</td>
					<td><xsl:value-of select="/contact/street_cont/@value"/></td>
					<td width="80px"/>
					<td>Role:</td>
					<td><xsl:value-of select="/contact/role/@value"></xsl:value-of></td>
				</tr>
				<tr>
					<td>City:</td>
					<td><xsl:value-of select="/contact/city/@value"/></td>
					<td width="80px"/>
					<td>Mobile:</td>
					<td><xsl:value-of select="/contact/mobile/@value"/></td>
				</tr>
				<tr>
					<td>State:</td>
					<td><xsl:value-of select="/contact/state/@value"/></td>
					<td width="80px"/>
					<td>Voice1:</td>
					<td><xsl:value-of select="/contact/voice1/@value"/></td>
				</tr>
				<tr>
					<td>Zip Code:</td>
					<td><xsl:value-of select="/contact/zip_code/@value"/></td>
					<td width="80px"/>
					<td>Voice2:</td>
					<td><xsl:value-of select="/contact/voice2/@value"/></td>
				</tr>
				<tr>
					<td>Country:</td>
					<td><xsl:value-of select="/contact/country/@value"/></td>
					<td width="80px"/>
					<td>Fax:</td>
					<td><xsl:value-of select="/contact/fax/@value"/></td>
				</tr>
				<tr>
					<td>Email:</td>
					<td><xsl:value-of select="/contact/email_address/@value"/></td>
					<td width="80px"/>
					<td>Login:</td>
					<td>
						<xsl:value-of select="/contact/login/@value"></xsl:value-of></td>
				</tr>
				<tr>
					<td>URL:</td>
					<td><xsl:value-of select="/contact/URL/@value"/></td>
					<td width="80px"/>
					<td></td>
					<td></td>
				</tr>
				<tr>
					<td>Home Sphere:</td>
					<td><xsl:value-of select="/contact/home_sphere/@value"/></td>
					<td width="80px"/>
					<td></td>
					<td></td>
				</tr>
				<tr>
					<td>Original Note:</td>
					<td><xsl:value-of select="/contact/body"/></td>
					<td></td>
				</tr>
			</table>
					

				

		</body>

		</html>
			
	</xsl:template>



</xsl:stylesheet>
