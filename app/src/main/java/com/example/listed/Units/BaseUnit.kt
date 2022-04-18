package com.example.ocoor.Units

object BaseUnit {

    val massUnit = mapOf(
        "g" to 1f,
        "kg" to 1000f,
        "Pfund" to 453.6f,
        "Messerspitze" to 0.25f,
        "Prise" to 0.5f,
        "Teelöffel" to 5f,
        "Esslöffel" to 15f,
    )

    val unitDict = mapOf(
        // mass
        "gramm" to "g",
        "kg" to "kg",
        "kilogramm" to "kg",
        "kilo gramm" to "kg",
        "pfund" to "Pfund,",
        // volume
        "liter" to "l",
        "milli liter" to "ml",
        "milliliter" to "ml",
        "dezi liter" to "dl",
        "deziliter" to "dl",
        "zenti liter" to "zl",
        "zentiliter" to "zl",
        // countable
        "stk." to "stk",
        "Stück" to "stk",
        "stück" to "stk",
        // numbers
        "ein" to "1",
        "einen" to "1",
        "zwei" to "2",
        "drei" to "3",
        "vier" to "4",
        "fünf" to "5",
        "sechs" to "6",
        "sieben" to "7",
        "acht" to "8",
        "neun" to "9",
        "zehn" to "10",
        "one" to "1",
        "two" to "2",
        "three" to "3",
        "four" to "4",
        "five" to "5",
        "six" to "6",
        "seven" to "7",
        "eight" to "8",
        "nine" to "9",
        "ten" to "10",
        "a" to "1",
        "an" to "1",
    )

    val volumeUnit = mapOf(
        "ml" to 1f,
        "zl" to 10f,
        "dl" to 100f,
        "l"  to 1000f,
    )

    val countableUnit = mapOf(
        "Packung" to 1f,
        "stk" to 1f,
        "dutzend" to 12f,
        "dozen"  to 12f,
        "Bund" to 1f,
    )

    fun isUnit(unit:String) : Boolean{
        if(massUnit.containsKey(unit)){
            return true
        }
        else if(volumeUnit.containsKey(unit)){
            return true
        }
        return false
    }

    fun convertEquiUnit(unit:String) : String?{
        val newUnit = unitDict.get(unit)
        println("New Unit: $newUnit | $unit")
        if(newUnit != null){
            return newUnit
        }
        return null
    }
}
