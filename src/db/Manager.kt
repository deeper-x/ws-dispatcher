package com.websocketReport.db

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction

class Manager (val username: String, val password: String, val Host: String, val dbName: String){
    init {
        Database.connect("jdbc:postgresql://localhost:5432/$dbName", driver = "org.postgresql.Driver", user = username, password = password)
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