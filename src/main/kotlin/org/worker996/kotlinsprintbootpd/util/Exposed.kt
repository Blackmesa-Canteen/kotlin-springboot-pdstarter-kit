package org.worker996.kotlinsprintbootpd.util

import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant


/**
 * Executes a database transaction with the given statement.
 *
 * @param statement the lambda expression representing the code to be executed within the transaction.
 * The lambda expression should be defined with a `Transaction` receiver and should return a value of type `T`.
 * Any database operations performed within the transaction should be written within this lambda expression.
 *
 * @return the result of executing the statement within the transaction.
 *
 */
inline fun <T> tx(crossinline statement: Transaction.() -> T): T {
    return transaction {
        addLogger(StdOutSqlLogger)
        statement()
    }
}

/**
 * Creates a [Column] of type String with the given name and optional collate value.
 *
 * @param name The name of the column.
 * @param collate The collate value for the column. Default is null.
 * @return A [Column] of type String.
 */
fun Table.string(name: String, collate: String? = null): Column<String> {
    return varchar(name, Int.MAX_VALUE, collate)
}

/**
 * Checks if a table row exists in the database based on the specified condition.
 *
 * @param op The condition to be checked, expressed as a function that takes a [ISqlExpressionBuilder] as parameter
 *           and returns a [Op] of type [Boolean]. The condition is evaluated on the current table instance (`this`).
 *
 * @return `true` if at least one row satisfies the condition, `false` otherwise.
 *
 * @throws SQLException if a database access error occurs.
 * @throws IllegalStateException if the current table instance has not been initialized properly.
 *
 * @see ISqlExpressionBuilder
 */
fun <T: Table> T.exists(op: T.(ISqlExpressionBuilder) -> Op<Boolean>): Boolean {
    return slice(Op.TRUE).select{
        op(this)
    }.empty().not()
}

/**
 * Checks if the given ExposedSQLException represents a unique constraint exception based on the SQL state and message.
 *
 * @param key The key to check against the exception message. If provided, the method will only return `true` if the
 *            message contains the key (case insensitive). Default value is `null`.
 *
 * @return `true` if the exception represents a unique constraint violation, `false` otherwise.
 */
fun ExposedSQLException.isUniqueConstraintException(key: String? = null): Boolean {
    return sqlState == "23505" && (key == null || message?.contains(key, ignoreCase = true) == true)
}

open class BaseTable(name: String = ""): Table(name) {
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp())
    val updatedAt = timestamp("updated_at").defaultExpression(CurrentTimestamp())
}

abstract class BaseIdTable<T: Comparable<T>>(name: String = ""): IdTable<T>(name) {
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp())
    val updatedAt = timestamp("updated_at").defaultExpression(CurrentTimestamp())
}

open class BaseEntity<ID: Comparable<ID>>(id: EntityID<ID>, table: BaseIdTable<ID>): Entity<ID>(id) {
    var createdAt by table.createdAt
    var updatedAt by table.updatedAt
}

open class BaseEntityClass<ID : Comparable<ID>, out T : BaseEntity<ID>>(table: BaseIdTable<ID>) :
    EntityClass<ID, T>(table) {
    init {
        // To Autofill updated columns on entity change.
        // See [here](https://github.com/paulkagiri/ExposedDatesAutoFill/blob/master/src/main/kotlin/app/Models.kt).
        // This approach is not perfect because the update is separated from other update statements.
        // In the many-to-many relationship, it may update the timestamp even if the entity is not changed.
        // Also, it will not work if the entity is updated by raw SQL.
        EntityHook.subscribe { action ->
            if (action.changeType == EntityChangeType.Updated) {
                action.toEntity(this)?.updatedAt = Instant.now()
            }
        }
    }
}

open class BaseLongIdTable(name: String = "", columnName: String = "id") : BaseIdTable<Long>(name) {
    final override val id: Column<EntityID<Long>> = long(columnName).autoIncrement().entityId()
    final override val primaryKey = PrimaryKey(id)
}

open class BaseLongEntity(id: EntityID<Long>, table: BaseLongIdTable) : BaseEntity<Long>(id, table)

open class BaseLongEntityClass<out T : BaseLongEntity>(table: BaseLongIdTable) : BaseEntityClass<Long, T>(table)
