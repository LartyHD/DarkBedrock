/*
 * © Copyright by Astride UG (haftungsbeschränkt) 2018 - 2020.
 */
package net.darkdevelopers.darkbedrock.darkness.general.databases.sql

import com.google.gson.JsonObject
import net.darkdevelopers.darkbedrock.darkness.general.configs.ConfigData
import net.darkdevelopers.darkbedrock.darkness.general.configs.toConfigMap
import net.darkdevelopers.darkbedrock.darkness.general.functions.load
import net.darkdevelopers.darkbedrock.darkness.general.functions.save
import net.darkdevelopers.darkbedrock.darkness.general.functions.toMap
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.util.logging.Logger
import kotlin.coroutines.suspendCoroutine

@Suppress("unused", "MemberVisibilityCanBePrivate")
/**
 * @author Lars Artmann | LartyHD
 * Created by Lars Artmann | LartyHD on 10.11.2017 00:44.
 * Last edit 07.06.2019
 */
class SQL(private val sqlData: SQLData, private val logger: Logger = Logger.getGlobal()) {
    private var connection: Connection? = null
        get() {
            if (field?.isValid(2) != true) {
                disconnect(field)
                connect()
            }
            return field
        }

    constructor(configData: ConfigData, logger: Logger = Logger.getGlobal()) : this(
        "".run {
            val values = configData.load<JsonObject>().toMap()
            if (values.isEmpty()) logger.warning("${prefix}SQL data are not configured yet (It is resorted to the standard)")
            val mySQLConfigs = SQLData(values)
            configData.save(mySQLConfigs.toConfigMap())
            mySQLConfigs
        }, logger
    )

    init {
        connect()
    }

    fun preparedStatement(statement: String): PreparedStatement? = connection?.prepareStatement(statement)

    suspend fun update(update: String) = suspendCoroutine<Unit> { updateSync(update) }

    fun updateSync(update: String) {
        connection?.createStatement()?.executeUpdate(update)
    }

    suspend fun preparedUpdate(update: PreparedStatement) = suspendCoroutine<Unit> { preparedUpdateSync(update) }

    fun preparedUpdateSync(update: PreparedStatement) {
        update.executeUpdate()
    }

    suspend fun query(query: String) = suspendCoroutine<ResultSet> { querySync(query) }

    fun querySync(query: String) = connection?.createStatement()?.executeQuery(query)!!

    suspend fun preparedQuery(preparedQuery: PreparedStatement) =
        suspendCoroutine<ResultSet> { preparedQuerySync(preparedQuery) }

    fun preparedQuerySync(preparedQuery: PreparedStatement) = preparedQuery.executeQuery()!!

    private fun connect() /*= if (isOpen())*/ {
        logger.info("$prefix[${sqlData.type}] The connection is made...")
        connection = DriverManager.getConnection(
            "jdbc:${sqlData.type}://${sqlData.host}:${sqlData.port}/${sqlData.database}${sqlData.addon}",
            sqlData.username,
            sqlData.password
        )
        logger.info("$prefix[${sqlData.type}] The connection was successfully established")
    } //else System.err.println("${prefix}The connection has already established")

    private fun disconnect(connection: Connection?)/* = if (!isOpen()) */ {
        logger.info("$prefix[${sqlData.type}] The connection is closing...")
        connection?.close()
        this.connection = null
        logger.info("$prefix[${sqlData.type}] The connection was successfully closed")
    } //else System.err.println("${prefix}The connection has already closed")

    //private fun isOpen() = connection != null && connection?.isClosed == false

    companion object {
        private const val prefix = "[SQL] "
    }

}

