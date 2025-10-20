package org.dancorp.cyberclubadmin.data

import org.dancorp.cyberclubadmin.model.Session

interface Repositories {

    fun sessions(): AbstractRepository<Session>

}