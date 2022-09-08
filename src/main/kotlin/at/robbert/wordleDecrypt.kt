package at.robbert

import javax.script.Invocable
import javax.script.ScriptEngineManager

private object Decrypter {
    val invocable: Invocable

    init {
        System.setProperty("nashorn.args", "--language=es6")

        val factory = ScriptEngineManager()
        val nashorn = factory.getEngineByName("nashorn")

        this.javaClass.classLoader.getResourceAsStream("decrypt.js")?.reader().use {
            requireNotNull(it)

            nashorn.eval(it)
        }

        invocable = nashorn as Invocable
    }

    fun decrypt(string: String): String {
        return invocable.invokeFunction(
            "decrypt",
            "kmEc7iwmXccCEiuUKzq4NWvcRvWKrRQGqspVL9mqx304FEtwR7DYGXUAk+cf7SHIVK+NQJP0dX9pb7jU+v7gwjNsl0bBp3oFLTm44HsRa+0KVUt8tYxUvT5FvbIsUn8tLtwU1YRpr"
        ) as String
    }
}

fun decrypt(toDecrypt: String): String {
    return Decrypter.decrypt(toDecrypt)
}

fun wordleGameProperties(decryptedString: String): Map<String, String> {
    val split = decryptedString.split("&")
    val props = mutableMapOf<String, String>()

    split.forEach {
        val s = it.split("=")
        require(s.size == 2)
        props[s[0]] = s[1]
    }

    return props
}
