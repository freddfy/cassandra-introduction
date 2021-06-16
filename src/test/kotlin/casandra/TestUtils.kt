package casandra

import com.datastax.driver.core.ConsistencyLevel
import com.datastax.driver.core.Statement
import com.datastax.driver.core.exceptions.InvalidQueryException
import com.datastax.driver.mapping.Mapper
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.Assert


fun assertInvalidQueryException(block: () -> Unit) {
    try {
        block.invoke()
        Assert.fail("Should have thrown InvalidQueryException")
    } catch (ex: InvalidQueryException) {
        val error = "Cannot execute this query as it might involve data filtering and thus may have unpredictable " +
                "performance. If you want to execute this query despite the performance unpredictability, " +
                "use ALLOW FILTERING"
        assertThat(ex.message, equalTo(error))
    }
}

fun <T> Statement.executeWith(mapper: Mapper<T>): List<T> {
    val resultSet = mapper.manager.session.execute(this.setConsistencyLevel(ConsistencyLevel.QUORUM))
    return mapper.map(resultSet).all()
}