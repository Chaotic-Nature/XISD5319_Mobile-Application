package com.example.wilapp

class LearnerModel():java.io.Serializable {
    lateinit var id : String
    lateinit var name : String
    lateinit var surname : String
    var age : Int = 0
    lateinit var sex : String
    lateinit var school : String

    constructor(lId : String, lName : String, lSurname : String,
                lAge : Int, lSex : String, lSchool : String) : this() {
        id = lId
        name = lName
        surname = lSurname
        age = lAge
        sex = lSex
        school = lSchool

    }
}
