package dev.lumentae.logkeepr.screen.project.utils

fun parseDurationToSeconds(input: String): Long {
    val regex = Regex("""(?:(\d+)d)?\s*(?:(\d+)h)?\s*(?:(\d+)m)?\s*(?:(\d+)s)?""")
    val match = regex.matchEntire(input.trim())
        ?: return -1 // Input invalid

    val (d, h, m, s) = match.destructured

    val days = d.toLongOrNull() ?: 0
    val hours = h.toLongOrNull() ?: 0
    val minutes = m.toLongOrNull() ?: 0
    val seconds = s.toLongOrNull() ?: 0

    return days * 86400 + hours * 3600 + minutes * 60 + seconds
}

fun formatDurationToString(seconds: Long): String {
    val days = seconds / 86400
    val hours = (seconds % 86400) / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60

    return buildString {
        if (days > 0) append("${days}d ")
        if (hours > 0) append("${hours}h ")
        if (minutes > 0) append("${minutes}m ")
        if (secs > 0 || this.isEmpty()) append("${secs}s")
    }.trim()
}