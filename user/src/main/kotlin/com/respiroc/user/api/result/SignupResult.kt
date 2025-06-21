package com.respiroc.user.api.result

import com.respiroc.util.context.SpringUser

data class SignupResult(val token: String, val user: SpringUser)