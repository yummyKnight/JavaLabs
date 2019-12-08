<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:template match="/">
        <html>
            <body>
                <h2>Расписание</h2>
                <table border="1">
                    <tr bgcolor="#9acd32">
                        <th style="text-align:left">ID</th>
                        <th style="text-align:left">Остановки</th>
                        <th style="text-align:left">Время</th>
                        <th style="text-align:left">Водители</th>
                    </tr>
                    <xsl:for-each select="Schedule/Route">
                    <xsl:variable name="cnt" select="count(Drivers/driver) + 1"/>
                    <tr>
                        <td rowspan="{$cnt}">
                            <xsl:value-of select="ID/@id"/>
                        </td>
                        <td rowspan="{$cnt}">
                            <xsl:value-of select="Stops/@stopsToString"/>
                        </td>
                        <td rowspan="{$cnt}">
                            <xsl:value-of select="Time/@shortTime"/>
                        </td>
                    </tr>
                    <xsl:for-each select="Drivers/driver">
                        <tr>
                            <td>
                                <xsl:value-of select="@FIO"/>
                            </td>
                        </tr>
                    </xsl:for-each>
                    </xsl:for-each>
                </table>
        </body>
    </html>
</xsl:template>
        </xsl:stylesheet>