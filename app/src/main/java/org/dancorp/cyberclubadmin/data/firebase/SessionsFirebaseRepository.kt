package org.dancorp.cyberclubadmin.data.firebase

import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import org.dancorp.cyberclubadmin.model.Session

class SessionsFirebaseRepository: AbstractFirebaseRepository<Session> {
    constructor(firebase: Firebase) : super(firebase, "sessions")


    override fun snapshotToObject(snapshot: DocumentSnapshot): Session {
        return snapshot.toObject(Session::class.java)!!
    }
}