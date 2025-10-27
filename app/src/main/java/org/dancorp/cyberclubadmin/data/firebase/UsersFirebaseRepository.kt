package org.dancorp.cyberclubadmin.data.firebase

import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import org.dancorp.cyberclubadmin.model.User

class UsersFirebaseRepository: AbstractFirebaseRepository<User> {

    constructor(firebase: Firebase) : super(firebase, "users")


    override fun snapshotToObject(snapshot: DocumentSnapshot): User {
        return snapshot.toObject(User::class.java)!!
    }

}