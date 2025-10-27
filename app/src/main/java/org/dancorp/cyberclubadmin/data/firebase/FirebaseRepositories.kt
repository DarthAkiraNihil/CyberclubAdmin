package org.dancorp.cyberclubadmin.data.firebase

import com.google.firebase.Firebase
import org.dancorp.cyberclubadmin.data.AbstractRepository
import org.dancorp.cyberclubadmin.data.Repositories
import org.dancorp.cyberclubadmin.model.Game
import org.dancorp.cyberclubadmin.model.GameTable
import org.dancorp.cyberclubadmin.model.Notification
import org.dancorp.cyberclubadmin.model.Session
import org.dancorp.cyberclubadmin.model.Subscription
import org.dancorp.cyberclubadmin.model.SubscriptionType
import org.dancorp.cyberclubadmin.model.User

class FirebaseRepositories: Repositories {

    private val gamesRepository: GamesFirebaseRepository
    private val gameTablesRepository: GameTablesFirebaseRepository
    private val notificationsRepository: NotificationsFirebaseRepository
    private val sessionsRepository: SessionsFirebaseRepository
    private val subscriptionsRepository: SubscriptionsFirebaseRepository
    private val subscriptionTypesRepository: SubscriptionTypesFirebaseRepository
    private val usersRepository: UsersFirebaseRepository

    constructor(firebase: Firebase) {
        this.gamesRepository = GamesFirebaseRepository(firebase)
        this.gameTablesRepository = GameTablesFirebaseRepository(firebase)
        this.notificationsRepository = NotificationsFirebaseRepository(firebase)
        this.sessionsRepository = SessionsFirebaseRepository(firebase)
        this.subscriptionsRepository = SubscriptionsFirebaseRepository(firebase)
        this.subscriptionTypesRepository = SubscriptionTypesFirebaseRepository(firebase)
        this.usersRepository = UsersFirebaseRepository(firebase)
    }

    override val sessions: AbstractRepository<Session>
        get() = this.sessionsRepository
    override val games: AbstractRepository<Game>
        get() = this.gamesRepository
    override val gameTables: AbstractRepository<GameTable>
        get() = this.gameTablesRepository
    override val notifications: AbstractRepository<Notification>
        get() = this.notificationsRepository
    override val subscriptions: AbstractRepository<Subscription>
        get() = this.subscriptionsRepository
    override val subscriptionTypes: AbstractRepository<SubscriptionType>
        get() = this.subscriptionTypesRepository
    override val users: AbstractRepository<User>
        get() = this.usersRepository

}