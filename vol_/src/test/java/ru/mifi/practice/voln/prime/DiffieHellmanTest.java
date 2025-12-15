package ru.mifi.practice.voln.prime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Диффи-Хеллман")
class DiffieHellmanTest {

    @Test
    @DisplayName("Совпадение общего секрета на малых параметрах")
    void sharedSecretSmallParams() {
        BigInteger p = BigInteger.valueOf(23);
        BigInteger g = BigInteger.valueOf(5);

        DiffieHellman alice = new DiffieHellman(p, g);
        DiffieHellman bob = new DiffieHellman(p, g);

        alice.calculateSharedSecret(bob.getPublicKey());
        bob.calculateSharedSecret(alice.getPublicKey());

        assertEquals(alice.getSharedSecret(), bob.getSharedSecret());
        // секрет должен быть в диапазоне [1, p-1]
        BigInteger secret = alice.getSharedSecret();
        assertTrue(secret.compareTo(BigInteger.ONE) >= 0);
        assertTrue(secret.compareTo(p) < 0);
    }

    @Test
    @DisplayName("Конструктор по битности и совпадение секрета")
    void bitLengthConstructorFlow() {
        DiffieHellman party1 = new DiffieHellman(256);
        DiffieHellman party2 = new DiffieHellman(party1.getPrime(), party1.getGenerator());

        // Проверяем диапазоны генератора и ключей
        BigInteger p = party1.getPrime();
        BigInteger g = party1.getGenerator();
        assertTrue(g.compareTo(BigInteger.ONE) >= 0);
        assertTrue(g.compareTo(p) < 0);

        BigInteger privateKey1 = party1.getPrivateKey();
        BigInteger privateKey2 = party2.getPrivateKey();
        assertTrue(privateKey1.compareTo(BigInteger.ONE) >= 0);
        assertTrue(privateKey1.compareTo(p.subtract(BigInteger.ONE)) < 0);
        assertTrue(privateKey2.compareTo(BigInteger.ONE) >= 0);
        assertTrue(privateKey2.compareTo(p.subtract(BigInteger.ONE)) < 0);

        BigInteger publicKey1 = party1.getPublicKey();
        BigInteger publicKey2 = party2.getPublicKey();
        assertTrue(publicKey1.compareTo(BigInteger.ONE) >= 0);
        assertTrue(publicKey1.compareTo(p) < 0);
        assertTrue(publicKey2.compareTo(BigInteger.ONE) >= 0);
        assertTrue(publicKey2.compareTo(p) < 0);

        party1.calculateSharedSecret(party2.getPublicKey());
        party2.calculateSharedSecret(party1.getPublicKey());

        assertEquals(party1.getSharedSecret(), party2.getSharedSecret());
    }
}
