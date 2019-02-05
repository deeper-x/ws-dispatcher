package com.websocketReport.db

import com.websocketReport.utils.FileSysManager
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.FileInputStream
import java.io.FileReader
import java.util.*

class Manager (){
    init {
        val fileSysmanager = FileSysManager()
        val authProperty = fileSysmanager.getAuthProperty()

        val username = authProperty["db.username"]
        val password = authProperty["db.password"]
        val host = authProperty["db.host"]
        val port = authProperty["db.port"]
        val driver = authProperty["db.driver"]
        val db = authProperty["db.name"]

        Database.connect("jdbc:postgresql://${host}:${port}/${db}", driver = "${driver}", user = "${username}", password = "${password}")
    }

    fun runSelectQuery(inputQuery: String, fieldList: List<String>): List<String> {
        var resVal: MutableList<String> = mutableListOf()

        transaction {
            TransactionManager.current().exec(inputQuery){ rs->
                while (rs.next()){
                    resVal.add(rs.getString(fieldList[0]))
                }
            }
        }
        return resVal
    }
}