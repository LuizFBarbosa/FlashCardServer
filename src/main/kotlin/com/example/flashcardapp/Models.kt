package com.example.flashcardapp

import kotlinx.serialization.Serializable

@Serializable
data class Deck(
    val deckId: Long = 0,
    val name: String,
    val description: String? = null,
    val creationDate: Long = System.currentTimeMillis()
)

@Serializable
data class Flashcard(
    val flashcardId: Long = 0,
    val deckId: Long,
    val type: FlashcardType,
    val question: String,
    val answer: String,
    val options: String? = null,
    val fullText: String? = null,
    val creationDate: Long = System.currentTimeMillis()
)

@Serializable
data class StudyInfo(
    val flashcardId: Long,
    val easeFactor: Double = 2.5,
    val interval: Int = 0,
    val repetitions: Int = 0,
    val lastDifficulty: Int = 0,
    val nextReviewDate: Long = 0,
    val lastReviewDate: Long = 0,
    val reviewLocations: String = ""
)

@Serializable
data class StudyLocation(
    val locationId: Long = 0,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val creationDate: Long = System.currentTimeMillis()
)

@Serializable
data class UserStats(
    val id: Int = 1,
    val correctAnswers: Int = 0,
    val totalAnswers: Int = 0,
    val streakDays: Int = 0,
    val maxStreakDays: Int = 0,
    val lastStudyDate: Long = 0,
    val totalStudyDays: Int = 0
)

@Serializable
enum class FlashcardType {
    BASIC, QUIZ, CLOZE
}

@Serializable
data class SyncData(
    val decks: List<Deck>,
    val flashcards: List<Flashcard>,
    val studyInfos: List<StudyInfo>,
    val locations: List<StudyLocation>,
    val userStats: List<UserStats>
)