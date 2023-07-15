import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class IntermediateTable {
    private Set<Implicant> implicants;
    private List<Set<Implicant>> groups;
    private Set<Implicant> primeImplicants;

    public IntermediateTable(Set<Implicant> implicants) {
        this.implicants = implicants;
        this.groups = new ArrayList<>();
        primeImplicants = new HashSet<>();
        constructGroups();
        findImplicants();
    }

    private void printTable() {
        for (int i = 0; i < groups.size(); i++) {
            System.out.println("Group " + i + ":");
            for (Implicant implicant : groups.get(i)) {
                System.out.println(implicant);
            }
            System.out.println();
        }
    }

    private void constructGroups() {
        int maxOnes = findMaxOnes();
        for (int i = 0; i <= maxOnes; i++) {
            Set<Implicant> group = new HashSet<>();
            for (Implicant implicant : implicants) {
                if (implicant.countOnes() == i) {
                    group.add(implicant);
                }
            }
            groups.add(group);
        }
    }

    private int findMaxOnes() {
        int maxOnes = 0;
        for (Implicant implicant : implicants) {
            int countOnes = implicant.countOnes();
            if (countOnes > maxOnes) {
                maxOnes = countOnes;
            }
        }
        return maxOnes;
    }

    private void findImplicants() {
        boolean combined = false;
        do {
            Set<Implicant> nextImplicants = new HashSet<>();
            combined = false;
            int groupSize = groups.size();
            for (int i = 0; i < groupSize - 1; i++) {
                Set<Implicant> currentGroup = groups.get(i);
                Set<Implicant> nextGroup = groups.get(i + 1);

                List<Implicant> combinedImplicants = new ArrayList<>();
                for (Implicant implicant1 : currentGroup) {
                    for (Implicant implicant2 : nextGroup) {
                        if (Implicant.isCombinable(implicant1, implicant2)) {
                            Implicant combinedImplicant = new Implicant(implicant1, implicant2);
                            combinedImplicants.add(combinedImplicant);
                            combined = true;
                        }
                    }
                }
                if (!combinedImplicants.isEmpty()) {
                    nextImplicants.addAll(combinedImplicants);
                }
            }
            Set<Integer> minterms = new HashSet<>();
            for (Implicant impl : nextImplicants) {
                minterms.addAll(impl.getMinterms());
            }
//            System.err.println(minterms);
            for (Implicant implicant : implicants) {
                for (int mt : implicant.getMinterms()) {
                    if (!minterms.contains(mt)) {
                        primeImplicants.add(implicant);
                    }
                }
            }
//            printTable();
            implicants = nextImplicants;
            groups.clear();
            constructGroups();
        } while (combined);
    }

    public Set<Implicant> getPrimeImplicants() {
        return primeImplicants;
    }

    public static void main(String[] args) {
        Implicant i1 = new Implicant(4);
        Implicant i2 = new Implicant(8);
        Implicant i3 = new Implicant(9);
        Implicant i4 = new Implicant(10);
        Implicant i5 = new Implicant(11);
        Implicant i6 = new Implicant(12);
        Implicant i7 = new Implicant(14);
        Implicant i8 = new Implicant(15);

        Set<Implicant> implicants = new HashSet<>();
        implicants.add(i1);
        implicants.add(i2);
        implicants.add(i3);
        implicants.add(i4);
        implicants.add(i5);
        implicants.add(i6);
        implicants.add(i7);
        implicants.add(i8);

        IntermediateTable intermediateTable = new IntermediateTable(implicants);
        System.out.println(intermediateTable.primeImplicants);
    }
}
