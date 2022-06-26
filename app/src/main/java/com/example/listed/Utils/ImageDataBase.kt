package com.example.listed.Utils

import android.content.Context
import com.example.ocoor.Utils.SingletonHolder
import com.example.ocoor.Utils.SpeechRecognizerModule

class ImageDataBase(context: Context) {

    val imageNames = mapOf(
        "erdbeere" to "img_20220621_wa0001",

        "ruccula" to "img_20220621_wa0002",

        "oregano" to "img_20220621_wa0003",

        "frühlingszwiebel" to "img_20220621_wa0004",

        "majoran" to "img_20220621_wa0005",

        "zwiebel" to "img_20220621_wa0006",

        "bohnenkraut" to "img_20220621_wa0007",

        "melone" to "img_20220621_wa0008",
        "wassermelone" to "img_20220621_wa0008jpg",

        "banane" to "img_20220621_wa0009",

        "schnittlauch" to "img_20220621_wa0010",

        "kürbis" to "img_20220621_wa0011",

        "tomate" to "img_20220621_wa0012",

        "dill" to "img_20220621_wa00013",

        "orange" to "img_20220621_wa0014",

        "mais" to "img_20220621_wa0015",

        "zucchini" to "img_20220621_wa0016",

        "koriander" to "img_20220621_wa0017",

        "traube" to "img_20220621_wa00018",

        "möhre" to "img_20220621_wa00019",
        "karotte" to "img_20220621_wa00019",


        "salbei" to "img_20220621_wa0020",

        "avocado" to "img_20220621_wa0021",

        "birne" to "img_20220621_wa0022",

        "pilz" to "img_20220621_wa0023",
        "champinon" to "img_20220621_wa0023",

        "petersilie" to "img_20220621_wa00024",

        "rote bete" to "img_20220621_wa0025",
        "rotebete" to "img_20220621_wa0025",

        "apfel" to "img_20220621_wa0026",
        "äpfel" to "img_20220621_wa0026",

        "kirsche" to "img_20220621_wa0027",

        "lauch" to "img_20220621_wa0028",
        "porree" to "img_20220621_wa0028",

        "chili" to "img_20220621_wa00029",
        "chilli" to "img_20220621_wa00029",
        "peperoini" to "img_20220621_wa00029",

        "radischen" to "img_20220621_wa0030",

        "estragon" to "img_20220621_wa0032",

        "tomate" to "img_20220621_wa0033",
        "strauchtomate" to "img_20220621_wa0033",

        "butternusskürbis" to "img_20220621_wa0034",

        "basilikum" to "img_20220621_wa00035",

        "thymian" to "img_20220621_wa0036",

        "ingwer" to "img_20220621_wa0037",

        "aubergine" to "img_20220621_wa0038",

        "knoblauch" to "img_20220621_wa0039",

        "zitrone" to "img_20220621_wa0040",

        "lorbeer" to "img_20220621_wa0041",
        "lorbeerblatt" to "img_20220621_wa0041",

        "minze" to "img_20220621_wa0042",

        "paprika" to "img_20220621_wa0043",

        "rosmarin" to "img_20220621_wa0044",

        "gurke" to "img_20220621_wa0045",
    )


    companion object : SingletonHolder<ImageDataBase, Context>(::ImageDataBase)
}

