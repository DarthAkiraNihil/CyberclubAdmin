package org.dancorp.cyberclubadmin.service

class ManuallyProvidedServices(
    private val userService: AbstractUserService,
    private val authService: AbstractAuthService
): Services {

    override val users: AbstractUserService
        get() = this.userService

    override val auth: AbstractAuthService
        get() = this.authService

}