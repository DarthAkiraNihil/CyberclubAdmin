package org.dancorp.cyberclubadmin.service

interface Services {

    val sessions: AbstractSessionService
    val gameTables: AbstractGameTableService
    val games: AbstractGameService
    val subscriptions: AbstractSubscriptionService
    val subscriptionTypes: AbstractSubscriptionTypeService
    val notifications: AbstractNotificationService
    val users: AbstractUserService
    val auth: AbstractAuthService

}