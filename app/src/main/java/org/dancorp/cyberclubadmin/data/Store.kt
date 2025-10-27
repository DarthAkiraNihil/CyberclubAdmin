package org.dancorp.cyberclubadmin.data

import org.dancorp.cyberclubadmin.model.Game
import org.dancorp.cyberclubadmin.model.GameTable
import org.dancorp.cyberclubadmin.model.Notification
import org.dancorp.cyberclubadmin.model.Session
import org.dancorp.cyberclubadmin.model.Subscription
import org.dancorp.cyberclubadmin.model.SubscriptionType
import org.dancorp.cyberclubadmin.model.User
import java.util.Date

object Store {
    private var users: MutableList<User> = mutableListOf()
    private var games: MutableList<Game> = mutableListOf()
    private var tables: MutableList<GameTable> = mutableListOf()
    private var subscriptions: MutableList<Subscription> = mutableListOf()
    private var subscriptionTypes: MutableList<SubscriptionType> = mutableListOf()
    private var sessions: MutableList<Session> = mutableListOf()
    private var notifications: MutableList<Notification> = mutableListOf()
    private var currentUser: User? = null

    // User operations
    fun getUsers(): List<User> = users.toList()
    fun saveUsers(newUsers: List<User>) { users = newUsers.toMutableList() }

    // Game operations
    fun getGames(): List<Game> = games.toList()
    fun saveGames(newGames: List<Game>) { games = newGames.toMutableList() }

    // Table operations
    fun getTables(): List<GameTable> = tables.toList()
    fun saveTables(newTables: List<GameTable>) { tables = newTables.toMutableList() }

    // Subscription operations
    fun getSubscriptions(): List<Subscription> = subscriptions.toList()
    fun saveSubscriptions(newSubscriptions: List<Subscription>) {
        subscriptions = newSubscriptions.toMutableList()
    }

    // Subscription Type operations
    fun getSubscriptionTypes(): List<SubscriptionType> = subscriptionTypes.toList()
    fun saveSubscriptionTypes(newTypes: List<SubscriptionType>) {
        subscriptionTypes = newTypes.toMutableList()
    }

    // Session operations
    fun getSessions(): List<Session> = sessions.toList()
    fun saveSessions(newSessions: List<Session>) { sessions = newSessions.toMutableList() }

    // Notification operations
    fun getNotifications(): List<Notification> = notifications.toList()
    fun saveNotifications(newNotifications: List<Notification>) {
        notifications = newNotifications.toMutableList()
    }

    fun addNotification(notification: Notification) {
        notifications.add(notification)
    }

    // Current user operations
    fun getCurrentUser(): User? = currentUser
    fun setCurrentUser(user: User?) { currentUser = user }

    // Initialize with sample data
    fun initializeSampleData() {
        if (users.isEmpty()) {
            users = mutableListOf(
                User(
                    id = "1",
                    email = "admin@club.ru",
                    verified = true,
                    verifiedBy = null,
                    createdAt = Date()
                )
            )
        }

        if (subscriptionTypes.isEmpty()) {
            subscriptionTypes = mutableListOf(
                SubscriptionType(
                    id = "1",
                    name = "Стандарт",
                    pricePerMonth = 1000.0,
                    tariffCoefficient = 1.0
                ),
                SubscriptionType(
                    id = "2",
                    name = "Премиум",
                    pricePerMonth = 2000.0,
                    tariffCoefficient = 0.8
                )
            )
        }

        if (tables.isEmpty()) {
            tables = mutableListOf(
                GameTable(
                    id = "1",
                    number = 1,
                    cpu = "Intel Core i7-12700K",
                    ram = 32,
                    diskTotal = 1000,
                    diskUsed = 350,
                    gpu = "NVIDIA RTX 3070",
                    hourlyRate = 150,
                    installedGames = emptyList()
                ),
                GameTable(
                    id = "2",
                    number = 2,
                    cpu = "AMD Ryzen 7 5800X",
                    ram = 16,
                    diskTotal = 500,
                    diskUsed = 200,
                    gpu = "NVIDIA RTX 3060",
                    hourlyRate = 120,
                    installedGames = emptyList()
                )
            )
        }

        if (games.isEmpty()) {
            games = mutableListOf(
                Game(
                    id = "1",
                    name = "Counter-Strike 2",
                    description = "Тактический шутер от первого лица",
                    coverUrl = "",
                    diskSpace = 35
                ),
                Game(
                    id = "2",
                    name = "Dota 2",
                    description = "Многопользовательская онлайн-баталия",
                    coverUrl = "",
                    diskSpace = 25
                ),
                Game(
                    id = "3",
                    name = "Valorant",
                    description = "Тактический шутер с персонажами",
                    coverUrl = "",
                    diskSpace = 30
                )
            )
        }
    }
}