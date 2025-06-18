package com.respiroc.user.api.result

import com.respiroc.util.dto.SpringUser

data class LoginResult(val token: String, val user: SpringUser)
