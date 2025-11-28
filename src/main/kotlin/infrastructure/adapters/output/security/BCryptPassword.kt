package infrastructure.adapters.output.security

import domain.ports.PasswordEncoderPort
import org.mindrot.jbcrypt.BCrypt

class BCryptPassword : PasswordEncoderPort {
    override fun encode(contrasena: String): String {
        return BCrypt.hashpw(contrasena, BCrypt.gensalt())
    }
    override fun matches(contrasenaRaw: String, contrasenaHash: String): Boolean {
        return BCrypt.checkpw(contrasenaRaw, contrasenaHash)
    }
}