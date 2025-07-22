package com.respiroc.webapp.controller.web

import com.respiroc.webapp.controller.BaseController
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile

@Controller
@RequestMapping("/smart-receipt")
class SmartReceiptWebController : BaseController() {

    @GetMapping("/upload")
    fun uploadInvoice(model: Model): String {
        addCommonAttributesForCurrentTenant(model, "Upload Invoice")
        return "smart-receipt/upload"
    }

    @PostMapping("/upload")
    fun handleFileUpload(
        @RequestParam("invoiceFile") file: MultipartFile,
        model: Model
    ): String {
        addCommonAttributesForCurrentTenant(model, "Upload Invoice")
        
        try {
            if (file.isEmpty) {
                model.addAttribute("error", "Please select a file to upload")
                return "smart-receipt/upload"
            }
            
            val allowedExtensions = listOf(".pdf", ".jpg", ".jpeg", ".png")
            val fileName = file.originalFilename ?: ""
            val fileExtension = fileName.substringAfterLast('.', "").lowercase()
            
            if (!allowedExtensions.contains(".$fileExtension")) {
                model.addAttribute("error", "Invalid file format. Please upload PDF, JPG, JPEG, or PNG files only.")
                return "smart-receipt/upload"
            }
            
            model.addAttribute("uploadedFile", fileName)
            model.addAttribute("success", "File uploaded successfully!")
            
        } catch (e: Exception) {
            model.addAttribute("error", "Error uploading file: ${e.message}")
        }
        
        return "smart-receipt/upload"
    }
} 