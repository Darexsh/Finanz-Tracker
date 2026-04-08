package com.darexsh.finanztracker.domain.export

import java.io.ByteArrayOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

data class XlsxSheet(
    val name: String,
    val rows: List<List<String>>
)

object XlsxBuilder {
    fun buildWorkbook(sheets: List<XlsxSheet>): ByteArray {
        require(sheets.isNotEmpty()) { "At least one sheet is required." }

        val sharedStrings = linkedMapOf<String, Int>()
        sheets.forEach { sheet ->
            sheet.rows.forEach { row ->
                row.forEach { cell ->
                    if (!sharedStrings.containsKey(cell)) {
                        sharedStrings[cell] = sharedStrings.size
                    }
                }
            }
        }

        val out = ByteArrayOutputStream()
        ZipOutputStream(out).use { zip ->
            put(zip, "[Content_Types].xml", buildContentTypesXml(sheets.size))
            put(zip, "_rels/.rels", buildRootRelsXml())
            put(zip, "xl/workbook.xml", buildWorkbookXml(sheets))
            put(zip, "xl/_rels/workbook.xml.rels", buildWorkbookRelsXml(sheets.size))
            put(zip, "xl/styles.xml", buildStylesXml())
            put(zip, "xl/sharedStrings.xml", buildSharedStringsXml(sharedStrings.keys.toList()))

            sheets.forEachIndexed { index, sheet ->
                put(
                    zip,
                    "xl/worksheets/sheet${index + 1}.xml",
                    buildSheetXml(sheet, sharedStrings)
                )
            }
        }
        return out.toByteArray()
    }

    private fun put(zip: ZipOutputStream, path: String, content: String) {
        val entry = ZipEntry(path)
        zip.putNextEntry(entry)
        zip.write(content.toByteArray(Charsets.UTF_8))
        zip.closeEntry()
    }

    private fun buildContentTypesXml(sheetCount: Int): String {
        val sheetOverrides = (1..sheetCount).joinToString("") { index ->
            """<Override PartName="/xl/worksheets/sheet$index.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml"/>"""
        }
        return """
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
              <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
              <Default Extension="xml" ContentType="application/xml"/>
              <Override PartName="/xl/workbook.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml"/>
              <Override PartName="/xl/styles.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml"/>
              <Override PartName="/xl/sharedStrings.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.sharedStrings+xml"/>
              $sheetOverrides
            </Types>
        """.trimIndent()
    }

    private fun buildRootRelsXml(): String = """
        <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
        <Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
          <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="xl/workbook.xml"/>
        </Relationships>
    """.trimIndent()

    private fun buildWorkbookXml(sheets: List<XlsxSheet>): String {
        val sheetNodes = sheets.mapIndexed { index, sheet ->
            """<sheet name="${xml(sheet.name)}" sheetId="${index + 1}" r:id="rId${index + 1}"/>"""
        }.joinToString("")

        return """
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <workbook xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main"
              xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships">
              <sheets>
                $sheetNodes
              </sheets>
            </workbook>
        """.trimIndent()
    }

    private fun buildWorkbookRelsXml(sheetCount: Int): String {
        val sheetRels = (1..sheetCount).joinToString("") { index ->
            """<Relationship Id="rId$index" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet" Target="worksheets/sheet$index.xml"/>"""
        }
        val next = sheetCount + 1
        return """
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
              $sheetRels
              <Relationship Id="rId$next" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles" Target="styles.xml"/>
              <Relationship Id="rId${next + 1}" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/sharedStrings" Target="sharedStrings.xml"/>
            </Relationships>
        """.trimIndent()
    }

    private fun buildStylesXml(): String = """
        <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
        <styleSheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main">
          <fonts count="1"><font><sz val="11"/><name val="Calibri"/></font></fonts>
          <fills count="1"><fill><patternFill patternType="none"/></fill></fills>
          <borders count="1"><border/></borders>
          <cellStyleXfs count="1"><xf/></cellStyleXfs>
          <cellXfs count="1"><xf xfId="0"/></cellXfs>
          <cellStyles count="1"><cellStyle name="Normal" xfId="0" builtinId="0"/></cellStyles>
        </styleSheet>
    """.trimIndent()

    private fun buildSharedStringsXml(strings: List<String>): String {
        val items = strings.joinToString("") { value ->
            """<si><t>${xml(value)}</t></si>"""
        }
        return """
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <sst xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main" count="${strings.size}" uniqueCount="${strings.size}">
              $items
            </sst>
        """.trimIndent()
    }

    private fun buildSheetXml(sheet: XlsxSheet, sharedStrings: Map<String, Int>): String {
        val rows = sheet.rows.mapIndexed { rowIndex, row ->
            val cells = row.mapIndexed { colIndex, value ->
                val col = columnName(colIndex + 1)
                val ref = "$col${rowIndex + 1}"
                val idx = sharedStrings[value] ?: 0
                """<c r="$ref" t="s"><v>$idx</v></c>"""
            }.joinToString("")
            """<row r="${rowIndex + 1}">$cells</row>"""
        }.joinToString("")

        return """
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <worksheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main">
              <sheetData>
                $rows
              </sheetData>
            </worksheet>
        """.trimIndent()
    }

    private fun columnName(index: Int): String {
        var n = index
        val sb = StringBuilder()
        while (n > 0) {
            val rem = (n - 1) % 26
            sb.insert(0, ('A'.code + rem).toChar())
            n = (n - 1) / 26
        }
        return sb.toString()
    }

    private fun xml(value: String): String {
        return value
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;")
    }
}
