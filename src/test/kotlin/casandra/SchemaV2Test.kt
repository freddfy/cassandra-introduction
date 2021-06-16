package casandra

import cassandra.COL_ASSET_NAME
import cassandra.COL_ASSET_TYPE
import cassandra.KEY_SPACE
import cassandra.TABLE_V2
import cassandra.v2.COL_MMDD
import cassandra.v2.COL_YYYY
import cassandra.v2.Price
import cassandra.v2.SCHEMA_V2
import com.datastax.driver.core.Cluster
import com.datastax.driver.core.querybuilder.QueryBuilder
import com.datastax.driver.mapping.Mapper
import com.datastax.driver.mapping.MappingManager
import com.github.nosan.embedded.cassandra.cql.CqlStatements
import com.github.nosan.embedded.cassandra.local.LocalCassandraFactory
import com.github.nosan.embedded.cassandra.test.ClusterFactory
import com.github.nosan.embedded.cassandra.test.junit4.CassandraRule
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.ClassRule
import org.junit.Test


class SchemaV2Test {

    companion object {
        @ClassRule
        @JvmField
        val cassandra = CassandraRule(LocalCassandraFactory(), CqlStatements(SCHEMA_V2))

        private lateinit var cluster: Cluster
        private lateinit var mapper: Mapper<Price>
        private lateinit var priceHistory: List<Price>

        @BeforeClass
        @JvmStatic
        fun initCluster() {
            cluster = ClusterFactory().create(cassandra.settings)
            val manager = MappingManager(cluster.connect())
            mapper = manager.mapper(Price::class.java)

            val expectedPrice1 = Price("0700.HK", 2021, "0102", "equity", "700")
            val expectedPrice2 = Price("0700.HK", 2021, "0103", "equity", "710")
            val expectedPrice3 = Price("0700.HK", 2021, "0104", "equity", "720")
            priceHistory = listOf(expectedPrice1, expectedPrice2, expectedPrice3)
            priceHistory.forEach { mapper.save(it) }
        }

        @AfterClass
        @JvmStatic
        fun closeCluster() {
            cluster.close()
        }
    }

    @Test
    fun `Can get an existing price by both asset name and price date`() {
        val result = mapper.get("0700.HK", 2021, "0102")
        assertThat(result, equalTo(Price("0700.HK", 2021, "0102", "equity", "700")))
    }

    @Test
    fun `Can get a time series with asset name and a range of price dates`() {
        val result = QueryBuilder.select().from(KEY_SPACE, TABLE_V2)
            .where(QueryBuilder.eq(COL_ASSET_NAME, "0700.HK"))
            .and(QueryBuilder.eq(COL_YYYY, 2021))
            .and(QueryBuilder.gt(COL_MMDD, "0102"))
            .executeWith(mapper)

        assertThat(result, equalTo(priceHistory.filter{ it.mmdd != "0102" }))
    }

    @Test
    fun `Cannot get a range of asset names ( partition key)`() {
        assertInvalidQueryException {
            QueryBuilder.select().from(KEY_SPACE, TABLE_V2)
                .where(QueryBuilder.gte(COL_ASSET_NAME, "0700.HK"))
                .executeWith(mapper)
        }
    }

    @Test
    fun `Cannot query on asset type (given it's neither partition nor column key)`() {
        assertInvalidQueryException {
            QueryBuilder.select().from(KEY_SPACE, TABLE_V2)
                .where(QueryBuilder.eq(COL_ASSET_TYPE, "equity"))
                .executeWith(mapper)
        }
    }
}