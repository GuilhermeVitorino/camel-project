<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output omit-xml-declaration="no" indent="yes"/>

    <xsl:template match="/order">
		<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:nota="http://financeiro.com.br/nota">
           <soap:Body>
                <nota:nota>
                    <nota:data><xsl:value-of select="purchaseDate"/></nota:data>
                    <nota:value><xsl:value-of select="payment/value"/></nota:value>
                    <nota:itens>
                        <xsl:for-each select="itens/item">
                            <nota:item>
                            	<nota:quantidade><xsl:value-of select="quantidade"/></nota:quantidade>
                            	<nota:format><xsl:value-of select="format"/></nota:format>
                            	<nota:code><xsl:value-of select="book/code"/></nota:code>
                            </nota:item>
                        </xsl:for-each>
                    </nota:itens>
                 </nota:nota>
            </soap:Body>
        </soap:Envelope>        
    </xsl:template>

</xsl:stylesheet>