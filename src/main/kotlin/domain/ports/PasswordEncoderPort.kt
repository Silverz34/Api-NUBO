package domain.ports

interface PasswordEncoderPort {

    fun encode(contrasena:  String): String
    fun matches(contrasenaRaw : String, contrasenaHash : String): Boolean
}