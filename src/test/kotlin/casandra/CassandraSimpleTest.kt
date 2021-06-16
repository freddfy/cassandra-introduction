package casandra

import com.github.nosan.embedded.cassandra.Cassandra
import com.github.nosan.embedded.cassandra.local.LocalCassandraFactory
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.Test


class CassandraSimpleTest {
    @Test
    fun `Cassandra start and stop`() {
        val cassandra = LocalCassandraFactory().create()
        try{
            cassandra.start()
            assertThat(cassandra.state, equalTo(Cassandra.State.STARTED))
        }finally {
            cassandra.stop()
            assertThat(cassandra.state, equalTo(Cassandra.State.STOPPED))
        }
    }
}