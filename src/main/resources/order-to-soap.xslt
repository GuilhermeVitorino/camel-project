<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output omit-xml-declaration="no" indent="yes"/>

    <xsl:template match="/order">
		<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:invoice="http://financial.com.br/invoice">
           <soap:Body>
                <invoice:invoice>
                    <invoice:date><xsl:value-of select="purchaseDate"/></invoice:date>
                    <invoice:value><xsl:value-of select="payment/value"/></invoice:value>
                    <invoice:itens>
                        <xsl:for-each select="itens/item">
                            <invoice:item>
                            	<invoice:quantity><xsl:value-of select="quantity"/></invoice:quantity>
                            	<invoice:format><xsl:value-of select="format"/></invoice:format>
                            	<invoice:code><xsl:value-of select="book/code"/></invoice:code>
                            </invoice:item>
                        </xsl:for-each>
                    </invoice:itens>
                 </invoice:invoice>
            </soap:Body>
        </soap:Envelope>        
    </xsl:template>

</xsl:stylesheet>