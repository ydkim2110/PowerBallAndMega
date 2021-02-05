package com.reachfree.powerballandmega.utils

object Constants {

    const val POWER_BALL_BASE_URL = "https://data.ny.gov/resource/"
    const val MEGA_BALL_BASE_URL = "https://data.ny.gov/resource/"
    const val ADVICE_BASE_URL = "https://api.adviceslip.com/"

    const val TYPE_POWER = "power"
    const val TYPE_MEGA = "mega"

    const val ROUND_POWER = 69
    const val ROUND_POWER_PLAY = 26
    const val ROUND_MEGA = 70
    const val ROUND_MEGA_PLAY = 25

    const val TYPE_SAVE = "save"
    const val TYPE_CANCEL = "cancel"

    const val BOMB = 0
    const val RAINBOW = 1
    const val BEER = 2
    const val CLOVER = 3
    const val TRIPLE_CLOVER = 4
    const val RAT = 5

    const val CATEGORY_GENERATOR = 0
    const val CATEGORY_ROULETTE = 1
    const val CATEGORY_SCRATCH = 2
    const val CATEGORY_SLOT = 3

    const val GAME_GENERATOR = "Number Generator"
    const val GAME_ROULETTE = "Roulette"
    const val GAME_SCRATCH = "Scratch"
    const val GAME_SLOT = "Slot"

    const val RANK_FIRST = "1등"
    const val RANK_SECOND = "2등"
    const val RANK_THIRD = "3등"
    const val RANK_FOURTH = "4등"
    const val RANK_FIFTH = "5등"
    const val RANK_SIXTH = "6등"
    const val RANK_SEVENTH = "7등"
    const val RANK_EIGHTH = "8등"
    const val RANK_NINTH = "9등"
    const val RANK_LOSE = "Lose"

}

enum class RANK(
    val rank: String,
    val description: String
) {
    RANK_POWER_FIRST("first", "Jackpot!"),
    RANK_POWER_SECOND("second", "$1 Million"),
    RANK_POWER_THIRD("third", "$50,000"),
    RANK_POWER_FOURTH("fourth", "$100"),
    RANK_POWER_FIFTH("fifth", "$100"),
    RANK_POWER_SIXTH("sixth", "$7"),
    RANK_POWER_SEVENTH("seventh", "$7"),
    RANK_POWER_EIGHTH("eighth", "$4"),
    RANK_POWER_NINTH("ninth", "$4"),

    RANK_MEGA_FIRST("first", "Jackpot!"),
    RANK_MEGA_SECOND("second", "$1 Million"),
    RANK_MEGA_THIRD("third", "$10,000"),
    RANK_MEGA_FOURTH("fourth", "$500"),
    RANK_MEGA_FIFTH("fifth", "$200"),
    RANK_MEGA_SIXTH("sixth", "$10"),
    RANK_MEGA_SEVENTH("seventh", "$10"),
    RANK_MEGA_EIGHTH("eighth", "$4"),
    RANK_MEGA_NINTH("ninth", "$2"),
}