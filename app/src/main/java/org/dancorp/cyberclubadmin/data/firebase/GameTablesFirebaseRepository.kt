package org.dancorp.cyberclubadmin.data.firebase

import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import org.dancorp.cyberclubadmin.model.GameTable

class GameTablesFirebaseRepository: AbstractFirebaseRepository<GameTable> {

    constructor(firebase: Firebase) : super(firebase, "game_tables")

    override fun snapshotToObject(snapshot: DocumentSnapshot): GameTable {
        return snapshot.toObject(GameTable::class.java)!!
    }

}