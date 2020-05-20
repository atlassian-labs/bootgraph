package com.atlassian.bootgraph.graphviz

import java.awt.GraphicsEnvironment

class FontFinder {

    companion object {
        /**
         * List of all fonts that will be searched in order of preference. If the first font isn't available,
         * the second will be searched and so on until a font has been found.
         */
        val FONT_PREFERENCE = listOf(
                "Arial",
                "Liberation Sans"
        )
    }

    private val availableFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().availableFontFamilyNames

    /**
     * Checks if the given font exists. If no font is passed into the method, will provide a default font that is
     * available on the current OS.
     */
    fun findFont(fontName: String?): String {

        if (fontName != null && !fontIsAvailable(fontName)) {
            throw UnknownFontException(String.format("The font '%s' is not available in your OS. Please choose another font or " +
                    "no font at all to take a default font.", fontName));
        }

        if (fontName != null && fontIsAvailable(fontName)) {
            return fontName
        }

        return findDefaultFont(availableFonts)
    }

    private fun findDefaultFont(availableFonts: Array<out String>): String {
        for (font in FONT_PREFERENCE) {
            if (fontIsAvailable(font)) {
                return font
            }
        }

        throw UnknownFontException(String.format("Couldn't find a default font in the list of available fonts of your OS. " +
                "Tried these fonts: %s. " +
                "Please raise a ticket to extend the list of default fonts.", FONT_PREFERENCE))
    }

    private fun fontIsAvailable(fontName: String): Boolean {
        return availableFonts.contains(fontName)
    }

}