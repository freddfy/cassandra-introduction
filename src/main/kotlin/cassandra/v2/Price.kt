package cassandra.v2

import cassandra.*
import com.datastax.driver.mapping.annotations.ClusteringColumn
import com.datastax.driver.mapping.annotations.Column
import com.datastax.driver.mapping.annotations.PartitionKey
import com.datastax.driver.mapping.annotations.Table

const val COL_YYYY = "yyyy"
const val COL_MMDD = "mmdd"

val SCHEMA_V2 = listOf(
    "CREATE KEYSPACE IF NOT EXISTS test_key_space WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor':'1' }",
    "CREATE TABLE IF NOT EXISTS $KEY_SPACE.$TABLE_V2 (" +
            "$COL_ASSET_NAME text," +
            "$COL_YYYY int," +
            "$COL_MMDD text," +
            "$COL_ASSET_TYPE text," +
            "close text," +
            "PRIMARY KEY(" +
                "( $COL_ASSET_NAME, $COL_YYYY)," + // Partition Key
                COL_MMDD + // Column Key
            ")" +
    ")"

)

@Table(keyspace = KEY_SPACE, name = TABLE_V2, writeConsistency = QUORUM, readConsistency = QUORUM)
data class Price(
    @PartitionKey(0)
    @Column(name = COL_ASSET_NAME)
    var assetName: String = "",

    @PartitionKey(1)
    @Column(name = COL_YYYY)
    var yyyy: Int = 1970,

    @ClusteringColumn(0)
    @Column(name = COL_MMDD)
    var mmdd: String = "",

    @Column(name = COL_ASSET_TYPE)
    var assetType: String = "",
    var close: String = ""
)