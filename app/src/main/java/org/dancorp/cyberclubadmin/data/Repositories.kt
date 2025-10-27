package org.dancorp.cyberclubadmin.data

import org.dancorp.cyberclubadmin.model.Game
import org.dancorp.cyberclubadmin.model.GameTable
import org.dancorp.cyberclubadmin.model.Notification
import org.dancorp.cyberclubadmin.model.Session
import org.dancorp.cyberclubadmin.model.Subscription
import org.dancorp.cyberclubadmin.model.SubscriptionType
import org.dancorp.cyberclubadmin.model.User

interface Repositories {

    val games: AbstractRepository<Game>
    val gameTables: AbstractRepository<GameTable>
    val notifications: AbstractRepository<Notification>
    val sessions: AbstractRepository<Session>
    val subscriptions: AbstractRepository<Subscription>
    val subscriptionTypes: AbstractRepository<SubscriptionType>
    val users: AbstractRepository<User>

}