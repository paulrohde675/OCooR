package com.example.listed.Utils

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.example.ocoor.Utils.SingletonHolder
import com.example.ocoor.Utils.SpeechRecognizerModule

class ImageDataBase(context: Context) {

    val imageNames = mapOf(
        "ananas" to "ananas",
        "apfel" to "apfel",
        "artischocke" to "artischocke",
        "aubergine" to "aubergine",
        "auster" to "austern",
        "avocado" to "avocado",
        "bacon" to "bacon",
        "speck" to "bacon",
        "baguette" to "baguette",
        "banane" to "banane",
        "basilikum" to "basilikum",
        "birne" to "birne",
        "blumenkohl" to "blumenkohl",
        "bohnenkraut" to "bohnenkraut",
        "brezel" to "brezel",
        "brokkoli" to "brokkoli",
        "brombeere" to "brombeeren",
        "brot" to "brot",
        "brötchen" to "brötchen",
        "butternusskürbis" to "butternusskürbis",
        "cashew" to "cashews",
        "champignon" to "champignons",
        "chili" to "chili",
        "chilli" to "chili",
        "chinakohl" to "chinakohl",
        "cocktailtomaten" to "cocktailtomaten",
        "croissant" to "croissant",
        "dattel" to "datteln",
        "dill" to "dill",
        "edamer" to "edamer",
        "käse" to "edamer",
        "emmentaler" to "emmentaler",
        "erdbeere" to "erdberee",
        "erdnüsse" to "erdnüsse",
        "estragon" to "estragon",
        "farfalle" to "farfalle",
        "feta" to "feta",
        "fleischtomaten" to "fleischtomaten",
        "forelle" to "forelle",
        "fisch" to "forelle",
        "frühlingszwiebel" to "frühlingszwiebel",
        "gurke" to "gurke",
        "haselnüsse" to "haselnüsse",
        "haselnuss" to "haselnüsse",
        "heidelbeere" to "heidelbeeren",
        "hering" to "hering",
        "himbeere" to "himbeeren",
        "hokkaidokürbis" to "hokkaidokürbis",
        "kürbis" to "Hokkaidokürbis",
        "hummer" to "hummer",
        "ingwer" to "ingwer",
        "johannisbeere" to "johannisbeere",
        "kabeljau" to "kabeljau",
        "karotte" to "karotte",
        "kirsche" to "kirsche",
        "kiwi" to "kiwi",
        "knoblauch" to "knoblauch",
        "knollensellerie" to "knollensellerie",
        "sellerie" to "knollensellerie",
        "kohl" to "kohl",
        "kohlrabi" to "kohlrabi",
        "koriander" to "koriander",
        "kürbiskern" to "kürbiskerne",
        "lachs" to "lachs",
        "lauch" to "lauch",
        "porree" to "lauch",
        "leinensamen" to "leinensamen",
        "lorbeerblatt" to "lorbeerblatt",
        "lorbeerblätter" to "lorbeerblatt",
        "lorbeerblatt" to "lorbeerblatt",
        "macadamianüsse" to "macadamianüsse",
        "macadamianuss" to "macadamianüsse",
        "mais" to "mais",
        "majoran" to "majoran",
        "mandarine" to "mandarine",
        "mandel" to "mandel",
        "mango" to "mango",
        "maracuja" to "maracuja",
        "maultasche" to "maultaschen",
        "minze" to "minze",
        "mozzarella" to "mozzarella",
        "buratta" to "mozzarella",
        "muffin" to "muffin",
        "muschel" to "muscheln",
        "auster" to "muscheln",
        "nektarine" to "nektarine",
        "olive" to "olive",
        "orange" to "orange",
        "oregano" to "oregano",
        "paprika" to "paprika",
        "parmesan" to "parmesan",
        "pecanüsse" to "pecanüsse",
        "pecanuss" to "pecanüsse",
        "petersilie" to "petersilie",
        "pfirsisch" to "pfirsisch",
        "pflaume" to "pflaume",
        "pininenkerne" to "pininenkerne",
        "pstazie" to "pistazien",
        "radischen" to "radischen",
        "ricotta" to "ricotta",
        "rosmarin" to "rosmarin",
        "rote beete" to "rote beete",
        "ruccula" to "ruccula",
        "salamie" to "salamie",
        "wurst" to "salamie",
        "aufschnitt" to "salamie",
        "salat" to "salat",
        "salbei" to "salbei",
        "scampi" to "scampi",
        "seafood" to "scampi",
        "schnittlauch" to "schnittlauch",
        "seasam" to "sesam",
        "schrimps" to "schrimps",
        "sonnenblumenkern" to "sonnenblumenkerne",
        "spargel" to "spargel",
        "stachelbeere" to "stachelbeere",
        "staudensellerie" to "staudensellerie",
        "thunfisch" to "tunfisch",
        "thymian" to "thymian",
        "tintenfisch" to "tintenfisch",
        "toast" to "toast",
        "traube" to "trauben",
        "walnüsse" to "walnüsse",
        "walnuss" to "walnüsse",
        "wassermelone" to "wassermelone",
        "zitrone" to "zitrone",
        "zucchini" to "zucchini",
        "zwiebel" to "zwiebel",
    )


    fun findImageToGood(goodName: String): String{
        var imageName: String

        // iterate over string to find substring that matches db
        imageName = findGoodNameInDB(goodName.lowercase().trim())

        // if no matching string is found use the first letter
        if(imageName.isEmpty()){
            imageName = goodName.lowercase().trim()[0].toString()
        }

        return imageName
    }

    private fun findGoodNameInDB(goodName: String): String{
        // iterate over string to find substring that matches db
        var imageName: String? = null
        val len = goodName.length
        for (r in len downTo 2){
            for (l in 0..r-2){
                imageName = imageNames[goodName.substring(l,r)]
                if(imageName != null){
                    return imageName
                }
            }
        }
        return ""
    }

    companion object : SingletonHolder<ImageDataBase, Context>(::ImageDataBase)
}

