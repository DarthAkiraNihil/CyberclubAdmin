package org.dancorp.cyberclubadmin.service

interface Services {

    val users: AbstractUserService
    val auth: AbstractAuthService

}