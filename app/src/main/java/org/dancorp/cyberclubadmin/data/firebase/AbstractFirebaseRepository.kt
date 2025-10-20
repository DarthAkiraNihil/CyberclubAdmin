package org.dancorp.cyberclubadmin.data.firebase

import com.google.firebase.Firebase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import org.dancorp.cyberclubadmin.data.AbstractRepository
import org.dancorp.cyberclubadmin.util.Mappable
import org.dancorp.cyberclubadmin.util.WithId

abstract class AbstractFirebaseRepository<T>: AbstractRepository<T> where T: Mappable, T: WithId {

    private val collection: CollectionReference

    constructor(
        firebase: Firebase,
        collectionName: String
    ) {
        this.collection = firebase.firestore.collection(collectionName)
    }

    override suspend fun get(
        id: Int,
    ): T? {
        val snapshot = this
            .collection
            .document(id.toString())
            .get()
            .await()

        if (snapshot.exists()) {
            return this.snapshotToObject(snapshot)
        }
        return null
    }

    override suspend fun list(): List<T> {
        return this
            .collection
            .get()
            .await()
            .map { obj -> this.snapshotToObject(obj) }
    }

    override suspend fun create(obj: T) {
        this
            .collection
            .document(obj.id())
            .set(obj)
            .await()
    }

    override suspend fun update(id: Int, updated: T) {
        this
            .collection
            .document(id.toString())
            .update(updated.toMap())
            .await()
    }

    override suspend fun delete(id: Int) {
        this
            .collection
            .document(id.toString())
            .delete()
            .await()
    }

    protected abstract fun snapshotToObject(snapshot: DocumentSnapshot): T

}