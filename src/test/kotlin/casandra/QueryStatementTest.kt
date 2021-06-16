package casandra

import cassandra.COL_ASSET_NAME
import cassandra.KEY_SPACE
import cassandra.TABLE_V1
import cassandra.v1.COL_PRICE_DATE
import com.datastax.driver.core.querybuilder.QueryBuilder
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.Test


class QueryStatementTest {
    @Test
    fun `select statement`() {
        val statement = QueryBuilder.select().from(KEY_SPACE, TABLE_V1)
            .where(QueryBuilder.eq(COL_ASSET_NAME, "0700.HK"))
            .and(QueryBuilder.gte(COL_PRICE_DATE, "20200102"))
            .queryString
        assertThat(
            statement,
            equalTo("SELECT * FROM test_key_space.prices_v1 WHERE asset_name=? AND price_date>=?;")
        )
    }
}