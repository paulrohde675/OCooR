package com.example.ocoor.Units

object BaseUnit {

    val massUnit = mapOf(
        "g" to 1f,
        "kg" to 1000f,
        "pfund" to 453.6f,
        "Pfund" to 453.6f,
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
        "stk." to 1f,
        "Stk." to 1f,
        "Stück" to 1f,
        "stück" to 1f,
        "dutzend" to 12f,
        "dozen"  to 12f,
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


}
