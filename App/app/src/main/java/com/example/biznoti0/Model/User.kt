package com.example.biznoti0.Model

class User(val ACType:String, val FName:String, val LName:String, val usersID:String, val Image:String, val profileImageUrl: String, val Email: String, val MName: String)
{
    constructor() : this("","","","","","","", "")
}