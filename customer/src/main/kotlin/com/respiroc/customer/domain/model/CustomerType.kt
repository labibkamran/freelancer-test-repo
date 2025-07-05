package com.respiroc.customer.domain.model

enum class CustomerType(val type: String) {
    CUSTOMER("customer"),
    SUPPLIER("supplier"),
    CUSTOMER_SUPPLIER("customer/supplier")
}