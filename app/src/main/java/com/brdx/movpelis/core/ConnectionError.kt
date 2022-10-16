package com.brdx.movpelis.core


sealed class ConnectionError() : Throwable() {
    data class InternalApiError(override val message: String? = "Internal Api Error") : ConnectionError()
    data class ApiKeyInvalid(override val message: String? = "Api Key Invalid") : ConnectionError()
    data class UnauthorizedAction(override val message: String? = "Unauthorized Action") : ConnectionError()

    class Unknown(val exception: String) : ConnectionError()

    companion object {
        fun getError(customError: CustomError): ConnectionError =
            when(customError){
                CustomError.ApiKeyInvalid -> ApiKeyInvalid()
                CustomError.UnauthorizedAction -> UnauthorizedAction()
                CustomError.Unknown -> Unknown(customError.default.toString())
            }

    }
}