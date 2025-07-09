package com.respiroc.webapp.config.menu

import org.springframework.stereotype.Component

@Component
class MenuProvider {
    fun getMenuSections(): List<MenuSection> {
        return listOf(
            MenuSection(
                title = "Dashboard", icon = "dashboard",
                items = listOf(MenuSectionItem(label = "Home", url = "/dashboard"))
            ),

            MenuSection(
                title = "Vouchers", icon = "file-text",
                items = listOf(
                    MenuSectionItem(label = "Overview", url = "/voucher/overview"),
                    MenuSectionItem(label = "Advanced Voucher", url = "/voucher/new-advanced-voucher")
                )
            ),

            MenuSection(
                title = "Accounts", icon = "receipt",
                items = listOf(MenuSectionItem(label = "General Ledger", url = "/ledger/general"))
            ),

            MenuSection(
                title = "Reports", icon = "chart-simple",
                items = listOf(MenuSectionItem(label = "Trial Balance", url = "/report/trial-balance"))
            )
        )
    }
}