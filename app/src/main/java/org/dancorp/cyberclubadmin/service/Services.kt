package org.dancorp.cyberclubadmin.service

interface Services {

    val sessions: AbstractSessionService
    val gameTables: AbstractGameTableService
    val subscriptions: AbstractSubscriptionService
    val notifications: AbstractNotificationService
    val users: AbstractUserService
    val auth: AbstractAuthService

}