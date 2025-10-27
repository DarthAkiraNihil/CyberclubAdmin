package org.dancorp.cyberclubadmin.data.firebase

import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import org.dancorp.cyberclubadmin.model.Game

class GamesFirebaseRepository: AbstractFirebaseRepository<Game> {

    constructor(firebase: Firebase) : super(firebase, "games")

    override fun snapshotToObject(snapshot: DocumentSnapshot): Game {
        return snapshot.toObject(Game::class.java)!!
    }

}