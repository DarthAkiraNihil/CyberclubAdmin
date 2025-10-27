package org.dancorp.cyberclubadmin.data.firebase

import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import org.dancorp.cyberclubadmin.model.SubscriptionType

class SubscriptionTypesFirebaseRepository: AbstractFirebaseRepository<SubscriptionType> {

    constructor(firebase: Firebase) : super(firebase, "subscription_types")


    override fun snapshotToObject(snapshot: DocumentSnapshot): SubscriptionType {
        return snapshot.toObject(SubscriptionType::class.java)!!
    }

}