package com.respiroc.user.api.result

import com.respiroc.util.dto.UserContext

data class LoginResult(val token: String, val user: UserContext)
