package com.respiroc.webapp.controller.response

data class Callout(
    val message: String,
    val type: MessageType,
    val link: String? = null
)

enum class MessageType(val variant: String, val iconName: String) {
    SUCCESS("success", "circle-check"),
    ERROR("danger", "circle-exclamation"),
    WARNING("warning", "triangle-exclamation"),
    INFO("brand", "circle-info")
} 