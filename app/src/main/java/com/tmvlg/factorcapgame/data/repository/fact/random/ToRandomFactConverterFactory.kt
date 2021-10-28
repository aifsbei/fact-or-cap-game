package com.tmvlg.factorcapgame.data.repository.fact.random

import java.lang.reflect.Type
import okhttp3.MediaType
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit

/*
<div id='z'>Crocodile babies don't have sex chromosomes; the temperature at which
    the egg develops determines gender.
                    <br/>
                    <br/>
*/

class ToRandomFactConverterFactory : Converter.Factory() {
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation?>?,
        retrofit: Retrofit?
    ): Converter<ResponseBody, *>? {
        return if (RandomFact::class.java == type) {
            Converter { value ->
                val stringBody = value.string()
                val stringBodyWithoutPrerubbish = stringBody.substring(
                    stringBody.findAnyOf(listOf("<div id='z'>"))!!.first
                )
                val sbwp = stringBodyWithoutPrerubbish
                val fact = sbwp.substring(
                    "<div id='z'>".length,
                    sbwp.findAnyOf(listOf("<br/>"))!!.first
                ).trim()
                return@Converter RandomFact(
                    true,
                    fact
                )
            }
        } else null
    }

    companion object {
        private val MEDIA_TYPE: MediaType = MediaType.parse("text/plain")!!
    }
}