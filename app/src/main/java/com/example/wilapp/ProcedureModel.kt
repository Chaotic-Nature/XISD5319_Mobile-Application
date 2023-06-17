package com.example.wilapp

class ProcedureModel() {
    lateinit var category : String
    lateinit var description : String
    lateinit var procedurePerformer : String
    lateinit var datePerformed : String

    constructor(category : String, description: String, procedurePerformer : String, datePerformed : String) : this(){
        this.category = category
        this.description = description
        this.procedurePerformer = procedurePerformer
        this.datePerformed = datePerformed
    }

}