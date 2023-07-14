import java.util.*;

public class Implicant {
    private Set<Integer> minterms;
    private String binaryRepresentation;

    public Implicant(int minterm) {
        this.minterms = new HashSet<>();
        minterms.add(minterm);
        this.binaryRepresentation = calculateBinaryRepresentation();
    }

    public Implicant(Implicant i1, Implicant i2) {
        this.minterms = new HashSet<>();
        this.minterms.addAll(i1.minterms);
        this.minterms.addAll(i2.minterms);
        this.binaryRepresentation = calculateBinaryRepresentation();
        if (minterms.size() != i1.minterms.size() + i2.minterms.size())  {
            throw new ArithmeticException("Minterms are not disjoint");
        }
        if (i1.countDashes() != i2.countDashes() || i1.countDashes() + 1 != this.countDashes()) {
            throw new ArithmeticException("Cannot combine implicants");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Implicant implicant = (Implicant) o;
        return Objects.equals(binaryRepresentation, implicant.binaryRepresentation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(binaryRepresentation);
    }

    public static boolean isCombinable(Implicant i1, Implicant i2) {
        try {
            new Implicant(i1, i2);
        } catch (ArithmeticException e) {
            return false;
        }
        return true;
    }

    public List<Integer> getMinterms() {
        return new ArrayList<>(minterms);
    }

    public String getBinaryRepresentation() {
        return binaryRepresentation;
    }

    private String calculateBinaryRepresentation() {
        StringBuilder binary = new StringBuilder();
        int maxMinterm = minterms.stream().max(Integer::compareTo).orElse(0);
        int numBits = Integer.toBinaryString(maxMinterm).length();

        for (int i = 0; i < numBits; i++) {
            boolean allOnes = true;
            boolean allZeros = true;

            for (int minterm : minterms) {
                int bit = (minterm >> (numBits - i - 1)) & 1;

                if (bit == 0) {
                    allOnes = false;
                } else {
                    allZeros = false;
                }

                if (!allOnes && !allZeros) {
                    break;
                }
            }

            if (allOnes) {
                binary.append('1');
            } else if (allZeros) {
                binary.append('0');
            } else {
                binary.append('-');
            }
        }

        return binary.toString();
    }

    public int countOnes() {
        int count = 0;
        for (char c : binaryRepresentation.toCharArray()) {
            if (c == '1') {
                count++;
            }
        }
        return count;
    }

    public int countDashes() {
        int count = 0;
        for (char c : binaryRepresentation.toCharArray()) {
            if (c == '-') {
                count++;
            }
        }
        return count;
    }

    public boolean isCombined() {
        return minterms.size() > 1;
    }

    public boolean isMintermIn(String minterms) {
        return this.minterms.stream().anyMatch(m -> minterms.contains(String.valueOf(m)));
    }

    @Override
    public String toString() {
        return "Minterms: " + minterms + ", Binary: " + binaryRepresentation;
    }

    public static void main(String[] args) {
        Implicant i1 = new Implicant(8);
        Implicant i2 = new Implicant(9);
        Implicant i3 = new Implicant(10);
        Implicant i4 = new Implicant(11);
        Implicant i5 = new Implicant(9);
        System.out.println(i1);
        System.out.println(i2);
        System.out.println(i3);
        System.out.println(i4);
        System.out.println(i5);

        Implicant i6 = new Implicant(i1, i2);
        Implicant i7 = new Implicant(i3, i4);
        Implicant i8 = new Implicant(i6, i7);
        System.out.println(i6);
        System.out.println(i7);
        System.out.println(i8);
        System.out.println(i8.countDashes());
    }
}
