package com.respiroc.user.api.result

import com.respiroc.util.dto.SpringUser

data class SignupResult(val token: String, val user: SpringUser)