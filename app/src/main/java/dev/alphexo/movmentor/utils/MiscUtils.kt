package dev.alphexo.movmentor.utils


fun String.addCharAtIndex(char: Char, index: Int) =
    StringBuilder(this).apply { insert(index, char) }.toString()

// Calculates a formatted node string based on the provided node ID,
// with a hyphen after the first two digits and all leading zeros removed
// from the second part.
fun calculateNode(nodeId: String): String {
    return "${nodeId.substring(0, 2)}-${nodeId.substring(2).trimStart('0')}"
}
