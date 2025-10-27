package org.dancorp.cyberclubadmin.data.firebase

import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import org.dancorp.cyberclubadmin.model.Subscription

class SubscriptionsFirebaseRepository: AbstractFirebaseRepository<Subscription> {

    constructor(firebase: Firebase) : super(firebase, "subscriptions")


    override fun snapshotToObject(snapshot: DocumentSnapshot): Subscription {
        return snapshot.toObject(Subscription::class.java)!!
    }

}