package com.respiroc.webapp.exception

class TenantNotFoundException(message: String) : RuntimeException(message)

class TenantAccessDeniedException(message: String) : RuntimeException(message)

class NoTenantProvidedException(message: String) : RuntimeException(message) 