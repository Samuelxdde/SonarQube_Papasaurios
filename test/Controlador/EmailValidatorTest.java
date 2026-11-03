package Controlador;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EmailValidatorTest {

    @Test
    public void testCorreoValido() {
        assertNull(EmailValidator.validar("juan@correo.com"));
    }

    @Test
    public void testCorreoVacio() {
        assertEquals("El correo es obligatorio.", EmailValidator.validar(""));
    }

    @Test
    public void testCorreoSinArroba() {
        assertNotNull(EmailValidator.validar("juancorreo.com"));
    }

    @Test
    public void testCorreoDemasiadoLargo() {
        String largo = "a".repeat(250) + "@correo.com";
        assertNotNull(EmailValidator.validar(largo));
    }
}
