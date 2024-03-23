package dev.alphexo.movmentor.utils

import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.InputStreamReader
import java.util.Base64
import java.util.zip.GZIPInputStream

object GZIP {
    fun decompress(compressedString: String): String {
        val bytes = Base64.getDecoder().decode(compressedString)
        val bArrayInStream = ByteArrayInputStream(bytes)
        val gInStream = GZIPInputStream(bArrayInStream)
        val reader = BufferedReader(InputStreamReader(gInStream, Charsets.UTF_8))
        val sb = StringBuilder()
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            sb.append(line)
        }
        reader.close()
        gInStream.close()
        bArrayInStream.close()
        return sb.toString()
    }
}