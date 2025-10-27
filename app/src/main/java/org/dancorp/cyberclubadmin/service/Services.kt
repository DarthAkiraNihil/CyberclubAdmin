package org.dancorp.cyberclubadmin.service

import org.dancorp.cyberclubadmin.service.impl.UserService

interface Services {

    val users: UserService
    val auth: AbstractAuthService

}