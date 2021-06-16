package cassandra.v1

import cassandra.*
import com.datastax.driver.mapping.annotations.ClusteringColumn
import com.datastax.driver.mapping.annotations.Column
import com.datastax.driver.mapping.annotations.PartitionKey
import com.datastax.driver.mapping.annotations.Table

const val COL_PRICE_DATE = "price_date"

val SCHEMA_V1 = listOf(
    "CREATE KEYSPACE IF NOT EXISTS test_key_space WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor':'1' }",
    "CREATE TABLE IF NOT EXISTS $KEY_SPACE.$TABLE_V1 (" +
            "$COL_ASSET_NAME text," +
            "$COL_PRICE_DATE text," +
            "$COL_ASSET_TYPE text," +
            "close text," +
            "PRIMARY KEY(" +
                "$COL_ASSET_NAME," + // Partition Key
                COL_PRICE_DATE + // Column Key
            ")" +
    ")"
)

@Table(keyspace = KEY_SPACE, name = TABLE_V1, writeConsistency = QUORUM, readConsistency = QUORUM)
data class Price(
    @PartitionKey(0)
    @Column(name = COL_ASSET_NAME)
    var assetName: String = "",

    @ClusteringColumn(0)
    @Column(name = COL_PRICE_DATE)
    var priceDate: String = "",

    @Column(name = COL_ASSET_TYPE)
    var assetType: String = "",
    var close: String = ""
)