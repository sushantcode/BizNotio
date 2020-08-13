package com.example.biznoti0.Model

class Proposal(val owner: String, val proposalId: String, val proposalName: String, val proposalType: String, val proposalDescription: String, val minimumCase: String, val link: String, val timeCreated: Long) {
    constructor() : this("","","","","","","", 0)
}


