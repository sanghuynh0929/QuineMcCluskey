import java.util.*;

public class QuineMcCluskey {
    private IntermediateTable intermediateTable;
    private List<Implicant> primeImplicants;
    public QuineMcCluskey(List<Integer> minterms) {
        Set<Implicant> impl = new HashSet<>();
        for (int mt : minterms) {
            impl.add(new Implicant(mt));
        }
        intermediateTable = new IntermediateTable(impl);
        Set<Implicant> primeImplicants = intermediateTable.getPrimeImplicants();
        Set<Implicant> essentialPrimeImplicants = getEssentialPrimeImplicants(primeImplicants);

        System.err.println(essentialPrimeImplicants);
        List<Implicant> nonessentialPrimeImplicant = new ArrayList<>(primeImplicants);
        nonessentialPrimeImplicant.removeAll(essentialPrimeImplicants);
        Set<Integer> uncovered = new HashSet<>(minterms);
        for (Implicant imp : essentialPrimeImplicants)
            uncovered.removeAll(imp.getMinterms());

        System.err.println(uncovered);
        System.err.println(nonessentialPrimeImplicant);
        int result = 0;
        outer:
        for (int i = 1; i <= nonessentialPrimeImplicant.size(); i++) {
            // Loop through all possible permutations in order of increasing size.
            int mask = (1 << i) - 1;
            while (mask <= (1 << nonessentialPrimeImplicant.size())) {
                if (checkCoverage(nonessentialPrimeImplicant, mask, uncovered)) {
                    result = mask;
                    break outer;
                }
                mask = nextBitPermutation(mask);
            }
        }
        List<Implicant> finalPrimeImplicants = new ArrayList<>();
        for (int i = 0; i < nonessentialPrimeImplicant.size(); i++) {
            if (((result >> i) & 1) == 1) {
                Implicant imp = nonessentialPrimeImplicant.get(i);
                finalPrimeImplicants.add(imp);
            }
        }
        finalPrimeImplicants.addAll(essentialPrimeImplicants);
        this.primeImplicants = finalPrimeImplicants;

    }

    private boolean checkCoverage(List<Implicant> implicants, int mask, Set<Integer> uncovered) {
        Set<Integer> minterms = new HashSet<>(uncovered);
        for (int i = 0; i < implicants.size(); i++) {
            if (((mask >> i) & 1) == 1) {
                Implicant imp = implicants.get(i);
                minterms.removeAll(imp.getMinterms());
            }
        }
        return minterms.isEmpty();
    }

    private int nextBitPermutation(int mask) {
        // Generate next bit permutation, e.g. 0001 -> 0010; 0101 -> 0110;...
        assert(mask > 0);
        int v = mask;
        int t = (v | (v - 1)) + 1;
        int w = t | ((((t & -t) / (v & -v)) >> 1) - 1);
        return w;
    }
    private Set<Implicant> getEssentialPrimeImplicants(Set<Implicant> primeImplicants) {
        Set<Implicant> essentialPI = new HashSet<>();
        Set<Integer> minterms = new HashSet<>();

        for (Implicant impl : primeImplicants) {
            minterms.addAll(impl.getMinterms());
        }
        // Step 1: Find essential prime implicants
        for (int mt : minterms) {
            int countImplicants = 0;
            for (Implicant pi : primeImplicants) {
                if (pi.getMinterms().contains(mt)) {
                    countImplicants++;
                }
            }
            if (countImplicants == 1) {
                for (Implicant pi : primeImplicants) {
                    if (pi.getMinterms().contains(mt)) {
                        essentialPI.add(pi);
                        break;
                    }
                }
            }
        }
        return essentialPI;
    }
    public List<Implicant> getPrimeImplicants() {
        return new ArrayList<>(primeImplicants);
    }

    public static void main(String[] args) {
        QuineMcCluskey qm = new QuineMcCluskey(Arrays.asList(0,1,2,5,6,7));
        System.out.println(qm.getPrimeImplicants());
    }
}
