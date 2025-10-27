package org.dancorp.cyberclubadmin.service

import org.dancorp.cyberclubadmin.service.impl.UserService

class ManuallyProvidedServices(
    private val userService: UserService,
    private val authService: AbstractAuthService
): Services {

    override val users: UserService
        get() = this.userService

    override val auth: AbstractAuthService
        get() = this.authService

}