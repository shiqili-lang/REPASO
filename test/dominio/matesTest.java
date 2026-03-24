package dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class matesTest {
    //Ejercicio 1 Repaso

    @Test
    public void testsuma(){
        assertEquals(mates.sumarEnteros(1), 1);
    }

    @Test
    public void testSuma_5(){
        assertEquals(mates.sumarEnteros(5), 15);
    }


}
