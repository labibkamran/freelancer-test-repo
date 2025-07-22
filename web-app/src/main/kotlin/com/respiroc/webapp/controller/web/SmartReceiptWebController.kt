package com.respiroc.webapp.controller.web

import com.respiroc.webapp.controller.BaseController
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/smart-receipt")
class SmartReceiptWebController : BaseController() {

    @GetMapping("/upload")
    fun uploadInvoice(): String {
        // Redirect to voucher-reception page where upload functionality is now integrated
        return "redirect:/voucher-reception"
    }
} 