package org.dancorp.cyberclubadmin.service.impl

import com.google.firebase.Firebase
import org.dancorp.cyberclubadmin.data.Repositories
import org.dancorp.cyberclubadmin.data.firebase.FirebaseRepositories
import org.dancorp.cyberclubadmin.service.ManuallyProvidedServices
import org.dancorp.cyberclubadmin.service.Services
import org.dancorp.cyberclubadmin.service.ServicesLoader

class DefaultServicesLoader: ServicesLoader {

    override fun load(): Services {
        val firebase = Firebase
        val repositories: Repositories = FirebaseRepositories(firebase)

        val notificationService = NotificationService(repositories.notifications)
        val gameService = GameService(repositories.games)
        val gameTableService = GameTableService(repositories.gameTables, repositories.sessions, gameService)
        val subscriptionService = SubscriptionService(repositories.subscriptions)
        val subscriptionTypeService = SubscriptionTypeService(repositories.subscriptionTypes)
        val sessionService = SessionService(repositories.sessions, gameTableService, subscriptionService, notificationService)

        val userService = UserService(repositories.users)
        val authService = FirebaseAuthService(firebase, userService)

        return ManuallyProvidedServices(
            sessionService,
            gameTableService,
            gameService,
            subscriptionService,
            subscriptionTypeService,
            notificationService,
            userService,
            authService,
        )

    }
}