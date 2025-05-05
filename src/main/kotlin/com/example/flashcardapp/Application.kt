package com.example.flashcardapp

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import kotlinx.serialization.json.Json
import io.ktor.http.*
import org.slf4j.LoggerFactory

fun main() {
    embeddedServer(Netty, port = 8080) {
        module()
    }.start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json(Json { prettyPrint = true })
    }
    val logger = LoggerFactory.getLogger("Routes")

    // Inicializa SQLite database
    //Database.connect("jdbc:sqlite:./database/flashcard.db", "org.sqlite.JDBC")
    Database.connect("jdbc:sqlite::memory:", driver = "org.sqlite.JDBC") // Banco de dados em momoria para utilizar no Render

    transaction {
        SchemaUtils.create(Decks, Flashcards, StudyInfos, StudyLocations, UserStatsTable)
    }

    routing {
        // Para testar se esta no ar
        get("/") {
            call.respondText("Servidor rodando! 🚀", ContentType.Text.Plain)
        }

        get("/teste") {
            call.respond(mapOf("mensagem" to "API funcionando com SQLite na pasta 'database'"))
        }
        route("/api") {

            // Processa todos os dados do cliente
            post("/sync") {
                val syncData = call.receive<SyncData>()

                logger.info("Recebeu dados"+ syncData.toString())

                transaction {
                    // Processa Decks
                    syncData.decks.forEach { clientDeck ->
                        val existingDeck = DeckEntity.findById(clientDeck.deckId)
                        if (existingDeck == null) {
                            DeckEntity.new(clientDeck.deckId) {
                                name = clientDeck.name
                                description = clientDeck.description
                                creationDate = clientDeck.creationDate
                            }
                        } else {
                            existingDeck.name = clientDeck.name
                            existingDeck.description = clientDeck.description
                            existingDeck.creationDate = clientDeck.creationDate
                        }
                    }

                    // Process Flashcards
                    syncData.flashcards.forEach { clientFlashcard ->
                        val existingFlashcard = FlashcardEntity.findById(clientFlashcard.flashcardId)
                        if (existingFlashcard == null) {
                            FlashcardEntity.new(clientFlashcard.flashcardId) {
                                deckId = clientFlashcard.deckId
                                type = clientFlashcard.type.toString()
                                question = clientFlashcard.question
                                answer = clientFlashcard.answer
                                options = clientFlashcard.options
                                fullText = clientFlashcard.fullText
                                creationDate = clientFlashcard.creationDate
                            }
                        } else {
                            existingFlashcard.deckId = clientFlashcard.deckId
                            existingFlashcard.type = clientFlashcard.type.toString()
                            existingFlashcard.question = clientFlashcard.question
                            existingFlashcard.answer = clientFlashcard.answer
                            existingFlashcard.options = clientFlashcard.options
                            existingFlashcard.fullText = clientFlashcard.fullText
                            existingFlashcard.creationDate = clientFlashcard.creationDate
                        }
                    }

                    // Processa StudyInfo
                    syncData.studyInfos.forEach { clientStudyInfo ->
                        val existingStudyInfo = StudyInfoEntity.findById(clientStudyInfo.flashcardId)
                        if (existingStudyInfo == null) {
                            StudyInfoEntity.new(clientStudyInfo.flashcardId) {
                                easeFactor = clientStudyInfo.easeFactor
                                interval = clientStudyInfo.interval
                                repetitions = clientStudyInfo.repetitions
                                lastDifficulty = clientStudyInfo.lastDifficulty
                                nextReviewDate = clientStudyInfo.nextReviewDate
                                lastReviewDate = clientStudyInfo.lastReviewDate
                                reviewLocations = clientStudyInfo.reviewLocations
                            }
                        } else {
                            existingStudyInfo.easeFactor = clientStudyInfo.easeFactor
                            existingStudyInfo.interval = clientStudyInfo.interval
                            existingStudyInfo.repetitions = clientStudyInfo.repetitions
                            existingStudyInfo.lastDifficulty = clientStudyInfo.lastDifficulty
                            existingStudyInfo.nextReviewDate = clientStudyInfo.nextReviewDate
                            existingStudyInfo.lastReviewDate = clientStudyInfo.lastReviewDate
                            existingStudyInfo.reviewLocations = clientStudyInfo.reviewLocations
                        }
                    }

                    // Processa StudyLocations
                    syncData.locations.forEach { clientLocation ->
                        val existingLocation = StudyLocationEntity.findById(clientLocation.locationId)
                        if (existingLocation == null) {
                            StudyLocationEntity.new(clientLocation.locationId) {
                                name = clientLocation.name
                                latitude = clientLocation.latitude
                                longitude = clientLocation.longitude
                                creationDate = clientLocation.creationDate
                            }
                        } else {
                            existingLocation.name = clientLocation.name
                            existingLocation.latitude = clientLocation.latitude
                            existingLocation.longitude = clientLocation.longitude
                            existingLocation.creationDate = clientLocation.creationDate
                        }
                    }

                    // Processa UserStats
                    syncData.userStats.forEach { clientStats ->
                        val existingStats = UserStatsEntity.findById(clientStats.id)
                        if (existingStats == null) {
                            UserStatsEntity.new(clientStats.id) {
                                correctAnswers = clientStats.correctAnswers
                                totalAnswers = clientStats.totalAnswers
                                streakDays = clientStats.streakDays
                                maxStreakDays = clientStats.maxStreakDays
                                lastStudyDate = clientStats.lastStudyDate
                                totalStudyDays = clientStats.totalStudyDays
                            }
                        } else {
                            existingStats.correctAnswers = clientStats.correctAnswers
                            existingStats.totalAnswers = clientStats.totalAnswers
                            existingStats.streakDays = clientStats.streakDays
                            existingStats.maxStreakDays = clientStats.maxStreakDays
                            existingStats.lastStudyDate = clientStats.lastStudyDate
                            existingStats.totalStudyDays = clientStats.totalStudyDays
                        }
                    }
                }
                call.respond(HttpStatusCode.OK)
            }

            // Envia todos os dados para o cliente
            get("/sync") {
                val syncData = transaction {
                    SyncData(
                        decks = DeckEntity.all().map { it.toDeck() },
                        flashcards = FlashcardEntity.all().map { it.toFlashcard() },
                        studyInfos = StudyInfoEntity.all().map { it.toStudyInfo() },
                        locations = StudyLocationEntity.all().map { it.toStudyLocation() },
                        userStats = UserStatsEntity.all().map { it.toUserStats() }
                    )
                }
                call.respond(syncData)
            }
        }
    }
}