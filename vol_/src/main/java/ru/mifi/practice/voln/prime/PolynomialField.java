package ru.mifi.practice.voln.prime;

import java.util.Arrays;

/**
 * Поле многочленов GF(p)[x]/(m(x)) для простого p и монического многочлена m(x).
 */
public final class PolynomialField {
    private final int p;
    private final int[] mod; // монический модуль, коэффициенты по возрастанию степеней
    private final int modDeg;

    /**
     * Создает поле GF(p)[x]/(mod(x)). Требуется простое p и монический модуль.
     * @param prime простое p (характеристика поля коэффициентов)
     * @param modulus монический неприводимый многочлен (для учебных тестов проверка неприводимости не выполняется)
     */
    public PolynomialField(int prime, int[] modulus) {
        if (prime < 2 || !SieveOfEratosthenes.isPrime(prime)) {
            throw new IllegalArgumentException("p должно быть простым числом");
        }
        if (modulus == null || modulus.length == 0) {
            throw new IllegalArgumentException("Модуль не должен быть пустым");
        }
        int[] m = normalize(modulus, prime);
        if (m[m.length - 1] % prime != 1 % prime) {
            throw new IllegalArgumentException("Модуль должен быть моническим (старший коэффициент = 1)");
        }
        this.p = prime;
        this.mod = m;
        this.modDeg = m.length - 1;
    }

    public int characteristic() {
        return p;
    }

    public int[] modulus() {
        return Arrays.copyOf(mod, mod.length);
    }

    /**
     * Сложение полиномов по модулю p, без редукции по модульному многочлену.
     */
    public int[] add(int[] a, int[] b) {
        int n = Math.max(len(a), len(b));
        int[] r = new int[n];
        for (int i = 0; i < n; i++) {
            int av = i < len(a) ? a[i] : 0;
            int bv = i < len(b) ? b[i] : 0;
            r[i] = modP(av + bv);
        }
        return trimZeros(r);
    }

    /**
     * Умножение полиномов по модулю p с последующим приведением по модульному многочлену mod(x).
     */
    public int[] mul(int[] a, int[] b) {
        if (isZero(a) || isZero(b)) {
            return new int[0];
        }
        int[] an = normalize(a, p);
        int[] bn = normalize(b, p);
        int[] temp = new int[an.length + bn.length - 1];
        for (int i = 0; i < an.length; i++) {
            for (int j = 0; j < bn.length; j++) {
                temp[i + j] = modP(temp[i + j] + an[i] * bn[j]);
            }
        }
        return reduce(temp);
    }

    /**
     * Быстрое возведение полинома base в степень exp в поле GF(p)[x]/(mod(x)).
     */
    public int[] pow(int[] base, int exp) {
        if (exp < 0) {
            throw new IllegalArgumentException("Степень должна быть неотрицательной");
        }
        int[] result = one();
        int[] b = reduce(normalize(base, p));
        int e = exp;
        while (e > 0) {
            if ((e & 1) == 1) {
                result = mul(result, b);
            }
            e >>= 1;
            if (e > 0) {
                b = mul(b, b);
            }
        }
        return result;
    }

    /**
     * Единица поля: полином 1.
     */
    public int[] one() {
        return new int[]{1 % p};
    }

    /**
     * Нормализация коэффициентов по модулю p и обрезка ведущих нулей.
     */
    public static int[] normalize(int[] poly, int p) {
        if (poly == null || poly.length == 0) {
            return new int[0];
        }
        int[] r = new int[poly.length];
        for (int i = 0; i < poly.length; i++) {
            int v = poly[i] % p;
            if (v < 0) {
                v += p;
            }
            r[i] = v;
        }
        return trimZeros(r);
    }

    private int[] reduce(int[] poly) {
        int[] a = trimZeros(poly);
        if (a.length == 0) {
            return a;
        }
        int degA = a.length - 1;
        while (degA >= modDeg) {
            int shift = degA - modDeg;
            int factor = a[degA]; // так как mod монический, старший коэффициент = 1
            // a = a - factor * (mod << shift)
            for (int i = 0; i <= modDeg; i++) {
                int idx = i + shift;
                a[idx] = modP(a[idx] - factor * mod[i]);
            }
            a = trimZeros(a);
            degA = a.length - 1;
        }
        return a;
    }

    private static boolean isZero(int[] a) {
        return a == null || a.length == 0 || (a.length == 1 && a[0] == 0);
    }

    private static int len(int[] a) {
        return a == null ? 0 : a.length;
    }

    private int modP(int x) {
        int r = x % p;
        if (r < 0) {
            r += p;
        }
        return r;
    }

    private static int[] trimZeros(int[] a) {
        int n = a.length;
        int i = n - 1;
        while (i >= 0 && a[i] == 0) {
            i--;
        }
        if (i < 0) {
            return new int[0];
        }
        if (i == n - 1) {
            return a;
        }
        return Arrays.copyOf(a, i + 1);
    }
}
