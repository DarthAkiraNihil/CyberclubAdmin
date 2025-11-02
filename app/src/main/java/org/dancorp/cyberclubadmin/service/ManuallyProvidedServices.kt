package org.dancorp.cyberclubadmin.service

import org.dancorp.cyberclubadmin.service.impl.GameService

class ManuallyProvidedServices(
    private val sessionService: AbstractSessionService,
    private val gameTableService: AbstractGameTableService,
    private val gameService: AbstractGameService,
    private val subscriptionService: AbstractSubscriptionService,
    private val notificationService: AbstractNotificationService,
    private val userService: AbstractUserService,
    private val authService: AbstractAuthService
): Services {

    override val sessions: AbstractSessionService
        get() = this.sessionService

    override val gameTables: AbstractGameTableService
        get() = this.gameTableService

    override val games: AbstractGameService
        get() = this.gameService

    override val subscriptions: AbstractSubscriptionService
        get() = this.subscriptionService

    override val notifications: AbstractNotificationService
        get() = this.notificationService

    override val users: AbstractUserService
        get() = this.userService

    override val auth: AbstractAuthService
        get() = this.authService

}