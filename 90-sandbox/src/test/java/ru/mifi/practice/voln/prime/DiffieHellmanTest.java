package ru.mifi.practice.voln.prime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.math.BigInteger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;

@DisplayName("Диффи-Хеллман")
final class DiffieHellmanTest {

    @Test
    @Timeout(5)
    @DisplayName("Общий секрет совпадает у сторон на малых параметрах")
    void sharedSecretMatchesSmallParams() {
        BigInteger p = BigInteger.valueOf(23);
        BigInteger g = BigInteger.valueOf(5);
        DiffieHellman alice = new DiffieHellman(p, g);
        DiffieHellman bob = new DiffieHellman(p, g);
        alice.calculateSharedSecret(bob.getPublicKey());
        bob.calculateSharedSecret(alice.getPublicKey());
        assertThat("shared secrets of alice and bob dont match",
            alice.getSharedSecret(), is(bob.getSharedSecret()));
    }

    @Test
    @Timeout(5)
    @DisplayName("Общий секрет в диапазоне на малых параметрах")
    void sharedSecretInRangeSmallParams() {
        BigInteger p = BigInteger.valueOf(23);
        BigInteger g = BigInteger.valueOf(5);
        DiffieHellman alice = new DiffieHellman(p, g);
        DiffieHellman bob = new DiffieHellman(p, g);
        alice.calculateSharedSecret(bob.getPublicKey());
        assertThat("shared secret is outside range one to prime",
            alice.getSharedSecret(), both(greaterThanOrEqualTo(BigInteger.ONE)).and(lessThan(p)));
    }

    @Test
    @Timeout(5)
    @DisplayName("Генератор в диапазоне при конструкторе по битности")
    void generatorInRangeBitLength() {
        DiffieHellman party = new DiffieHellman(256);
        BigInteger p = party.getPrime();
        assertThat("generator is outside range one to prime",
            party.getGenerator(), both(greaterThanOrEqualTo(BigInteger.ONE)).and(lessThan(p)));
    }

    @Test
    @Timeout(5)
    @DisplayName("Закрытый ключ первой стороны в диапазоне")
    void privateKeyFirstInRange() {
        DiffieHellman party1 = new DiffieHellman(256);
        BigInteger p = party1.getPrime();
        assertThat("first private key is outside range one to prime minus one",
            party1.getPrivateKey(),
            both(greaterThanOrEqualTo(BigInteger.ONE)).and(lessThan(p.subtract(BigInteger.ONE))));
    }

    @Test
    @Timeout(5)
    @DisplayName("Закрытый ключ второй стороны в диапазоне")
    void privateKeySecondInRange() {
        DiffieHellman party1 = new DiffieHellman(256);
        DiffieHellman party2 = new DiffieHellman(party1.getPrime(), party1.getGenerator());
        BigInteger p = party1.getPrime();
        assertThat("second private key is outside range one to prime minus one",
            party2.getPrivateKey(),
            both(greaterThanOrEqualTo(BigInteger.ONE)).and(lessThan(p.subtract(BigInteger.ONE))));
    }

    @Test
    @Timeout(5)
    @DisplayName("Открытый ключ первой стороны в диапазоне")
    void publicKeyFirstInRange() {
        DiffieHellman party1 = new DiffieHellman(256);
        BigInteger p = party1.getPrime();
        assertThat("first public key is outside range one to prime",
            party1.getPublicKey(), both(greaterThanOrEqualTo(BigInteger.ONE)).and(lessThan(p)));
    }

    @Test
    @Timeout(5)
    @DisplayName("Открытый ключ второй стороны в диапазоне")
    void publicKeySecondInRange() {
        DiffieHellman party1 = new DiffieHellman(256);
        DiffieHellman party2 = new DiffieHellman(party1.getPrime(), party1.getGenerator());
        BigInteger p = party1.getPrime();
        assertThat("second public key is outside range one to prime",
            party2.getPublicKey(), both(greaterThanOrEqualTo(BigInteger.ONE)).and(lessThan(p)));
    }

    @Test
    @Timeout(5)
    @DisplayName("Общий секрет совпадает при конструкторе по битности")
    void sharedSecretMatchesBitLength() {
        DiffieHellman party1 = new DiffieHellman(256);
        DiffieHellman party2 = new DiffieHellman(party1.getPrime(), party1.getGenerator());
        party1.calculateSharedSecret(party2.getPublicKey());
        party2.calculateSharedSecret(party1.getPublicKey());
        assertThat("shared secrets of parties dont match",
            party1.getSharedSecret(), is(party2.getSharedSecret()));
    }
}
