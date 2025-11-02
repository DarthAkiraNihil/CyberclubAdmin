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

        val gameTableService = GameTableService(repositories.gameTables, repositories.sessions)
        val subscriptionService = SubscriptionService(repositories.subscriptions)
        val sessionService = SessionService(repositories.sessions, gameTableService, subscriptionService)
        val notificationService = NotificationService(repositories.notifications)
        val userService = UserService(repositories.users)
        val authService = FirebaseAuthService(firebase, userService)

        return ManuallyProvidedServices(
            sessionService,
            gameTableService,
            subscriptionService,
            notificationService,
            userService,
            authService,
        )

    }
}