package com.example.flashcardapp

import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.*

object Decks : LongIdTable("decks") {
    val name = varchar("name", 255)
    val description = text("description").nullable()
    val creationDate = long("creationDate")
}

object Flashcards : LongIdTable("flashcards") {
    val deckId = long("deck_id").references(Decks.id, onDelete = ReferenceOption.CASCADE)
    val type = varchar("type", 50)
    val question = text("question")
    val answer = text("answer")
    val options = text("options").nullable()
    val fullText = text("fullText").nullable()
    val creationDate = long("creationDate")
}

object StudyInfos : LongIdTable("study_info") {
    val flashcardId = long("flashcard_id").references(Flashcards.id, onDelete = ReferenceOption.CASCADE)
    val easeFactor = double("easeFactor")
    val interval = integer("interval")
    val repetitions = integer("repetitions")
    val lastDifficulty = integer("lastDifficulty")
    val nextReviewDate = long("nextReviewDate")
    val lastReviewDate = long("lastReviewDate")
    val reviewLocations = text("reviewLocations")
}

object StudyLocations : LongIdTable("locations") {
    val name = varchar("name", 255)
    val latitude = double("latitude")
    val longitude = double("longitude")
    val creationDate = long("creationDate")
}

object UserStatsTable : IntIdTable("user_stats") {
    val correctAnswers = integer("correctAnswers")
    val totalAnswers = integer("totalAnswers")
    val streakDays = integer("streakDays")
    val maxStreakDays = integer("maxStreakDays")
    val lastStudyDate = long("lastStudyDate")
    val totalStudyDays = integer("totalStudyDays")
}

class DeckEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<DeckEntity>(Decks)

    var name by Decks.name
    var description by Decks.description
    var creationDate by Decks.creationDate

    fun toDeck() = Deck(id.value, name, description, creationDate)
}

class FlashcardEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<FlashcardEntity>(Flashcards)

    var deckId by Flashcards.deckId
    var type by Flashcards.type
    var question by Flashcards.question
    var answer by Flashcards.answer
    var options by Flashcards.options
    var fullText by Flashcards.fullText
    var creationDate by Flashcards.creationDate

    fun toFlashcard() = Flashcard(
        flashcardId = id.value,
        deckId = deckId,
        type = FlashcardType.valueOf(type),
        question = question,
        answer = answer,
        options = options,
        fullText = fullText,
        creationDate = creationDate
    )
}

class StudyInfoEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<StudyInfoEntity>(StudyInfos)

    var easeFactor by StudyInfos.easeFactor
    var interval by StudyInfos.interval
    var repetitions by StudyInfos.repetitions
    var lastDifficulty by StudyInfos.lastDifficulty
    var nextReviewDate by StudyInfos.nextReviewDate
    var lastReviewDate by StudyInfos.lastReviewDate
    var reviewLocations by StudyInfos.reviewLocations

    fun toStudyInfo() = StudyInfo(
        flashcardId = id.value,
        easeFactor = easeFactor,
        interval = interval,
        repetitions = repetitions,
        lastDifficulty = lastDifficulty,
        nextReviewDate = nextReviewDate,
        lastReviewDate = lastReviewDate,
        reviewLocations = reviewLocations
    )
}

class StudyLocationEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<StudyLocationEntity>(StudyLocations)

    var name by StudyLocations.name
    var latitude by StudyLocations.latitude
    var longitude by StudyLocations.longitude
    var creationDate by StudyLocations.creationDate

    fun toStudyLocation() = StudyLocation(
        locationId = id.value,
        name = name,
        latitude = latitude,
        longitude = longitude,
        creationDate = creationDate
    )
}

class UserStatsEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserStatsEntity>(UserStatsTable)

    var correctAnswers by UserStatsTable.correctAnswers
    var totalAnswers by UserStatsTable.totalAnswers
    var streakDays by UserStatsTable.streakDays
    var maxStreakDays by UserStatsTable.maxStreakDays
    var lastStudyDate by UserStatsTable.lastStudyDate
    var totalStudyDays by UserStatsTable.totalStudyDays

    fun toUserStats() = UserStats(
        id = id.value,
        correctAnswers = correctAnswers,
        totalAnswers = totalAnswers,
        streakDays = streakDays,
        maxStreakDays = maxStreakDays,
        lastStudyDate = lastStudyDate,
        totalStudyDays = totalStudyDays
    )
}