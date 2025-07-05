package com.respiroc.customer.domain.model

enum class CustomerType(val type: String) {
    CUSTOMER("CUSTOMER"),
    SUPPLIER("SUPPLIER"),
    CUSTOMER_SUPPLIER("CUSTOMER/SUPPLIER")
}