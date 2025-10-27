package org.dancorp.cyberclubadmin.data.firebase

import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import org.dancorp.cyberclubadmin.model.Notification

class NotificationsFirebaseRepository: AbstractFirebaseRepository<Notification> {

    constructor(firebase: Firebase) : super(firebase, "notifications")

    override fun snapshotToObject(snapshot: DocumentSnapshot): Notification {
        return snapshot.toObject(Notification::class.java)!!
    }

}