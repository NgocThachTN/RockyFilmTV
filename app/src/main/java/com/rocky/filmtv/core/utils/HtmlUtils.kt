package com.rocky.filmtv.core.utils

/**
 * Extension function to strip HTML tags and decode common HTML entities from raw strings
 * returned by the OPhim API.
 */
fun String.stripHtml(): String {
    return this.replace(Regex("<[^>]*>"), "")
        .replace("&nbsp;", " ")
        .replace("&amp;", "&")
        .replace("&lt;", "<")
        .replace("&gt;", ">")
        .replace("&quot;", "\"")
        .replace("&apos;", "'")
        .trim()
}
