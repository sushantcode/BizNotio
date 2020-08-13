package com.example.biznoti0.Model

class Notification(val Id:String, val text:String, val postIdd:String, val ispost:Boolean)
{
    constructor() : this("","","",false)
}