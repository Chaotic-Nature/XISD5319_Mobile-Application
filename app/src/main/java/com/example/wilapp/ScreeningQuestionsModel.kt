package com.example.wilapp

class ScreeningQuestionsModel(){
    lateinit var learnerId : String
    lateinit var question : String
    var answer : Boolean = false

    constructor(id : String, ques : String, ans : Boolean) : this(){
        learnerId = id
        question = ques
        answer = ans
    }

}