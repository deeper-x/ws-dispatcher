package com.websocketReport.utils

import java.io.FileReader
import java.util.*

class FileSysManager {
    private val _properties: Properties = Properties()
    private val _propertyFile = System.getProperty("user.dir") + "/assets/auth/auth.properties"

    fun getAuthProperty(): Properties {
        val fileReader = FileReader(this._propertyFile)
        this._properties.load(fileReader)
        return this._properties
    }
}
