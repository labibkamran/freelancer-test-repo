package com.respiroc.user.api.result

import com.respiroc.util.dto.UserContext

data class SignupResult(val token: String, val user: UserContext)