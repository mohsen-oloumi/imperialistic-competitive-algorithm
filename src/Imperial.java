import java.util.*;
import java.util.stream.Collectors;

public class Imperial {
    private final int iterations = 1000;
    private final double alpha = 0.5;
    private final double beta = 2;
    private final double gama = 0.01 * Math.PI;
    private final Map<String, Range> range;
    private final int N = 100;
    private final int NumberOfEmpires = 10;
    Map<Long, Nation> nations;
    List<Nation> empires = new ArrayList<>();
    List<Nation> colonies = new ArrayList<>();

    public Imperial() {
        range = new HashMap<>();
        range.put("x", new Range(-10, 10));
        range.put("y", new Range(-10, 10));
        int NumberOfColonies = N - NumberOfEmpires;
        nations = GenerateNations(N, range);
        List<Nation> nationSortList = new ArrayList<>();
        nations.forEach((aLong, nation) -> nationSortList.add(nation));
        Collections.sort(nationSortList);
        for (int i = NumberOfColonies; i < nationSortList.size(); i++) {
            empires.add(nationSortList.get(i));
        }
        for (int i = 0; i < NumberOfColonies; i++) {
            colonies.add(nationSortList.get(i));
        }
        AssignColonyMetropolisRelation();
        for (int i = 0; i < iterations; i++) {
            Assimilate();
            try {
                Collections.sort(empires);
                Collections.sort(colonies);
            }catch (Exception e){
            }
            UpdateColonyMetropolisRelation();
            RemoveEmpiresWithoutColonies();
            if (empires.size() == 1) {
                System.out.println("done with " + i + " iterations");
                return;
            }
            ImperialisticCompetition();
            try {
                Collections.sort(empires);
                Collections.sort(colonies);
            }catch (Exception e){
            }
        }
        System.out.println("done with " + iterations + " iterations");
    }

    Map<Long, Nation> GenerateNations(int nations, Map<String, Range> range) {
        Map<Long, Nation> nationMap = new HashMap<>();
        for (int i = 0; i < nations; i++) {
            double x = Helper.RandomFromRange(range.get("x"));
            double y = Helper.RandomFromRange(range.get("y"));
            long id = i;
            double val = Helper.F(x, y);
            Nation nation = new Nation(x, y, id, val);
            nationMap.put(id, nation);
        }
        return nationMap;
    }

    Nation FindMetropolis(Nation colony) {
        return empires.stream().filter(nation -> nation.id == colony.metropolis).findFirst().get();
    }

    double GetNewMetropolis() {
        List<Double> strengths = GetNormalizedStrengths(null);
        List<Long> EmpiresIndexes = new ArrayList<>();
        this.empires.forEach(nation -> EmpiresIndexes.add(nation.id));
        return Helper.GetRandomFromArrayWithProbabilites(EmpiresIndexes, strengths);
    }

    List<Double> GetNormalizedStrengths(List<Nation> empires) {
        List<Nation> empires_ = new ArrayList<>();
        if (empires == null) {
            List<Nation> finalEmpires_ = empires_;
            this.empires.forEach(nation -> finalEmpires_.add(new Nation(nation.x, nation.y, nation.id, nation.val)));
        } else empires_ = empires;
        double weakestValue = empires_.get(0).val;
        List<Double> normalized = new ArrayList<>();
        List<Double> finalNormalized = normalized;
        empires_.forEach(nation -> finalNormalized.add(-nation.val + weakestValue));
        double sum = normalized.stream().mapToDouble(aDouble -> aDouble).sum();
        normalized = normalized.stream().map(aDouble -> Math.abs(aDouble / sum)).collect(Collectors.toList());
        return normalized;
    }

    void AssignColonyMetropolisRelation() {
        colonies.forEach(nation -> nation.metropolis = (long) GetNewMetropolis());
    }

    void RemoveFromEmpires(Nation e) {
        empires = empires.stream().filter(nation -> nation.id != e.id).collect(Collectors.toList());
    }

    Nation GetTheWeakest(List<Nation> colonies) {
        return colonies.stream().max(Comparator.comparing(Nation::getVal)).orElse(colonies.get(0));
    }

    Nation GetWeakestEmpire() {
        double weakestValue = GetTheWeakest(colonies).val;
        Nation minNormalized = empires.get(0);
        for (Nation nation : empires) {
            if (normalizedValue(nation, weakestValue) < normalizedValue(minNormalized, weakestValue))
                minNormalized = nation;
        }
        return minNormalized;
    }

    double normalizedValue(Nation empire, double weakestValue) {
        List<Nation> currentColonies = GetColonies(empire);
        final double[] sum = {0};
        currentColonies.forEach(nation -> {
            sum[0] += normalize(nation.val, weakestValue);
        });
        return normalize(empire.val, weakestValue) + sum[0] * alpha / currentColonies.size();
    }

    double normalize(double value, double weakestValue) {
        return weakestValue - value;
    }

    void MakeItToColony(Nation emp) {
        emp.metropolis = (long) GetNewMetropolis();
        colonies.add(emp);
    }

    void RemoveEmpiresWithoutColonies() {
        List<Nation> toDemote = empires.stream().filter(nation -> GetColonies(nation).size() == 0).collect(Collectors.toList());
        toDemote.forEach(this::RemoveFromEmpires);
        toDemote.forEach(this::MakeItToColony);
    }

    double GetOtherMetropolis(Nation empire) {
        List<Nation> filterEmpires = empires.stream().filter(nation -> nation.id != empire.id).collect(Collectors.toList());
        List<Double> strengths = GetNormalizedStrengths(filterEmpires);
        List<Long> EmpiresIndexes = filterEmpires.stream().map(nation -> nation.id).collect(Collectors.toList());
        return Helper.GetRandomFromArrayWithProbabilites(EmpiresIndexes, strengths);
    }

    void ImperialisticCompetition() {
        Nation weakestEmpire = GetWeakestEmpire();
        List<Nation> coloniesOfTheWeakest = GetColonies(weakestEmpire);
        Nation weakestColony = GetTheWeakest(coloniesOfTheWeakest);
        weakestColony.metropolis = (long) GetOtherMetropolis(weakestEmpire);
    }

    Vector Rotate(Vector vector, double angle) {
        return new Vector(Math.cos(angle) * vector.x - Math.sin(angle) * vector.y,
                Math.sin(angle) * vector.x + Math.cos(angle) * vector.y);
    }

    void Translate(Nation c, Vector vector) {
        c.x += vector.x;
        c.y += vector.y;
    }

    void RestrainToSearchDomain(Nation c) {
        c.x = Helper.Relaxation(c.x, range.get("x"));
        c.y = Helper.Relaxation(c.y, range.get("y"));
    }

    void Assimilate() {
        colonies.forEach(nation -> {
            Nation metropolis = FindMetropolis(nation);
            double BetaMultiplier = Helper.RandomFromRange(new Range(0, beta));
            Vector d = new Vector((metropolis.x - nation.x) * BetaMultiplier, (metropolis.y - nation.y) * BetaMultiplier);
            double angle = Helper.RandomFromRange(new Range(-gama, gama));
            Translate(nation, Rotate(d, angle));
            RestrainToSearchDomain(nation);
            nation.val = Helper.F(nation.x, nation.y);
        });
    }

    void MakeItMetropolis(Nation col, Nation oldMetro, List<Nation> coloniesOfOld) {
        coloniesOfOld.forEach(nation -> nation.metropolis = col.id);
        col.metropolis = null;
        oldMetro.metropolis = col.id;
        RemoveFromEmpires(oldMetro);
        empires.add(col);
        colonies = colonies.stream().filter(nation -> nation.id != col.id).collect(Collectors.toList());
        colonies.add(oldMetro);
    }

    List<Nation> GetColonies(Nation nation) {
        return colonies.stream().filter(nation1 -> nation1.metropolis == nation.id).collect(Collectors.toList());
    }

    void UpdateColonyMetropolisRelation() {
        empires.forEach(nation -> {
            List<Nation> colons = GetColonies(nation);
            Nation best = colons.stream().min(Comparator.comparing(Nation::getVal)).orElse(nation);
            if (best.id != nation.id)
                MakeItMetropolis(best, nation, colons);
        });
    }

}
