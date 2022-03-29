package com.example.ocoor.Units

open class BaseUnit {

    val massUnit = mapOf(
        "g" to 1,
        "kg" to 1000,
        "pfund" to 453.6,
        "Pfund" to 453.6,
    )


    val volumeUnit = mapOf(
        "ml" to 1,
        "zl" to 10,
        "dl" to 100,
        "l"  to 1000,
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
