package com.example.wilapp

class ProcedureModel() {
     var id : String = ""
     var category : String = ""
     var description : String = ""
     var procedurePerformer : String = ""
     var datePerformed : String = ""

    constructor(id : String, category : String, description: String, procedurePerformer : String, datePerformed : String) : this(){
        this.id = id
        this.category = category
        this.description = description
        this.procedurePerformer = procedurePerformer
        this.datePerformed = datePerformed
    }

}